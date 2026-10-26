# Doce Encanto — Gestão de doceria

Projeto Java 17, Spring Boot 3.5.7, Thymeleaf, Spring Security, Spring Data JPA e H2 persistente em disco. Interface em português, responsiva e sem serviços pagos ou dependências de IA.

## Executar com Docker (recomendado)

1. Instale Docker com Compose.
2. Copie `.env.example` para `.env` e substitua as duas senhas.
3. Na pasta do projeto, execute `docker compose up -d --build`.
4. Acesse http://localhost:8080 e entre com `admin` e a senha definida.
5. Logs: `docker compose logs -f`. Parar: `docker compose down`.

Os dados ficam no volume `doce_dados`. Não use `docker compose down -v`: remove o banco. Para acesso externo, configure HTTPS com proxy reverso e restrinja o acesso. A porta é publicada apenas localmente por padrão.

O banco é persistente: receitas, ingredientes, eventos, lotes, vendas e despesas continuam gravados após reiniciar o sistema, o computador ou o contêiner. O Docker usa o volume nomeado `doce_dados` e aguarda até 30 segundos para o encerramento seguro. `docker compose down` preserva os dados; somente a opção destrutiva `-v` remove o volume.

## Executar no Windows / IDE

Instale JDK 17 ou superior e Maven 3.6.3+. Importe `pom.xml` no IntelliJ, Eclipse ou VS Code. No PowerShell:

```powershell
$env:APP_PASSWORD="sua-senha-forte"
$env:DB_PASSWORD="senha-do-banco"
mvn clean verify
mvn spring-boot:run
```

Ou execute `iniciar-windows.bat` com Java e Maven no PATH. Login `admin`. A senha não é fixada no código. Em Linux: `APP_PASSWORD='sua-senha' mvn spring-boot:run`.

No Windows, use preferencialmente `iniciar-windows.bat`: ele fixa a pasta do projeto antes de iniciar, garantindo que o sistema sempre reutilize `data/doceencanto.mv.db`, mesmo quando o atalho ou terminal foi aberto em outra pasta. A configuração usa gravação síncrona (`WRITE_DELAY=0`) para reduzir o risco de perda em desligamentos inesperados.

Empacotar: `mvn clean package`; executar: `java -jar target/doce-encanto-1.0.0.jar`, mantendo APP_PASSWORD definido. O banco fica em `./data/doceencanto.mv.db` relativo à pasta de execução. Use sempre a mesma pasta e não rode duas instâncias sobre o mesmo arquivo.

## Uso passo a passo

1. Bolos & custos: cadastre cada sabor, rendimento (12 fatias por padrão), mão de obra/energia por bolo, embalagem por fatia, perdas, taxas e margem. O painel mostra custo do bolo inteiro, custo por fatia, preço sugerido e lucro estimado por bolo.
2. Abra o produto e inclua cada ingrediente. Exemplo: leite condensado, unidade g, preço 7,00, pacote 395 g, uso 395 g. Açúcar comprado em kg pode ser informado em g: 1.000 g no pacote, 200 g usados. Todos os itens devem utilizar a mesma unidade para compra e consumo.
3. Veja custo e preço sugerido por fatia. Altere a receita ou remova e recadastre um ingrediente quando necessário.
4. Crie um evento com nome, data e local.
5. Inclua os produtos, quantidade de bolos e preço de venda escolhido. Cada inclusão é um lote independente que preserva os custos e taxas vigentes. Se errar um lote, crie outro evento com o planejamento correto; a versão inicial não permite editar/excluir produção.
6. Lance inscrição, transporte, equipe e outras despesas. Evite duplicar mão de obra já incluída na receita.
7. Durante o evento, registre vendas por produto. O preço efetivo pode variar do planejado (promoções). Para corrigir venda, estorne e registre novamente.
8. Acompanhe o percentual do estoque vendido, o ponto de equilíbrio, o resultado realizado e encerre o evento. Eventos encerrados bloqueiam lançamentos e podem ser reabertos.

## Regras dos cálculos

- Ingrediente = preço do pacote × quantidade usada / quantidade do pacote.
- Custo por fatia = ((ingredientes + mão de obra + energia) × (1 + perdas/100)) / rendimento + embalagem.
- Preço sugerido = custo por fatia / (1 − taxas/100 − margem/100), arredondado para cima em centavos.
- A margem é sobre o preço de venda, não acréscimo sobre o custo. Taxas + margem devem ser inferiores a 100%.
- Investimento = custo de toda produção levada ao evento + despesas adicionais.
- Faturamento previsto = quantidade produzida × preço planejado.
- Lucro previsto = faturamento previsto líquido de taxas − investimento.
- Resultado realizado = vendas registradas líquidas de taxas − investimento total. Pode ser negativo antes de recuperar o investimento; fatias não vendidas continuam como custo integral.
- Ponto de equilíbrio = investimento / receita líquida média por fatia planejada, arredondado para cima. É uma estimativa dependente do mix vendido, pode superar o estoque. Com preço líquido zero não há estimativa.
- Taxas são um percentual médio configurado por produto, também aplicado ao preço promocional. Inclua taxas de cartão, comissão e tributos aplicáveis no percentual, se desejar contabilizá-los. Não há apuração fiscal automática.
- Valores calculados com BigDecimal; precisão interna de 8 casas para custo histórico dos lotes e arredondamento monetário de 2 casas nos resultados. Dinheiro é exibido com ponto decimal nesta versão.

### Exemplo

Ingredientes + mão de obra + energia = R$ 120; sem perdas; 12 fatias; embalagem R$ 1/fatia: custo R$ 11. Taxa 5% e margem 30%: preço sugerido R$ 16,93.
No evento, um bolo vendido a R$ 20/fatia e despesa extra R$ 30: investimento R$ 162; faturamento previsto R$ 240; lucro previsto R$ 66. Vendendo só 6 fatias: faturamento R$ 120; receita líquida R$ 114; resultado R$ -48. Equilíbrio estimado: 9 fatias.

## Estrutura e testes

`src/main/java/br/com/doceencanto`: entidades, repositórios, cálculo financeiro, serviço transacional, controlador e segurança.
`src/main/resources/templates`: telas Thymeleaf.
`src/test/java`: testes financeiros e integração web/banco, incluindo CSRF, estoque e encerramento.

Execute `mvn clean verify`. O perfil de teste usa banco H2 em memória, sem alterar dados reais.

## Dados, segurança e limites

Login único configurável por variáveis; sem cadastro de múltiplos operadores. Sessão e CSRF ativos. Banco não exposto via console web. H2 indicado para instalação pequena de uma única instância. Não contém emissão fiscal, integração com pagamentos, controle de estoque de ingredientes, contas a receber ou auditoria completa de estornos. Vendas representam recebimento realizado; não há fiado. Estoque refere-se às fatias produzidas por evento. As despesas são consideradas pagas ao lançar.

Backup: pare a aplicação (`docker compose stop` ou encerre o Java), copie `data/doceencanto.mv.db` ou faça backup do volume `doce_dados` e depois reinicie. Faça backup antes de atualizar o projeto. Nunca publique `.env` ou o banco no Git. O esquema usa ddl-auto=update nesta primeira versão; migrações versionadas são recomendadas antes de evoluções em produção.
# doce_encato_fatias
