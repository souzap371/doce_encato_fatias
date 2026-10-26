package br.com.doceencanto;

import java.math.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Gestao {
    final ProdutoRepository produtos;
    final IngredienteRepository ingredientes;
    final EventoRepository eventos;
    final LoteRepository lotes;
    final DespesaRepository despesas;
    final VendaRepository vendas;

    public Gestao(ProdutoRepository p, IngredienteRepository i, EventoRepository e, LoteRepository l,
            DespesaRepository d, VendaRepository v) {
        produtos = p;
        ingredientes = i;
        eventos = e;
        lotes = l;
        despesas = d;
        vendas = v;
    }

    public static void exigir(boolean ok, String msg) {
        if (!ok)
            throw new IllegalArgumentException(msg);
    }

    public static void positivo(BigDecimal v) {
        exigir(v != null && v.signum() >= 0 && v.compareTo(new BigDecimal("100000000")) <= 0,
                "Valor deve estar entre zero e 100 milhões.");
    }

    public List<Ingrediente> itens(Long id) {
        return ingredientes.findAll().stream().filter(i -> i.getProdutoId().equals(id)).toList();
    }

    public List<Lote> lotes(Long id) {
        return lotes.findAll().stream().filter(l -> l.getEventoId().equals(id)).toList();
    }

    public List<Despesa> despesas(Long id) {
        return despesas.findAll().stream().filter(d -> d.getEventoId().equals(id)).toList();
    }

    public List<Venda> vendas(Long id) {
        return vendas.findAll().stream().filter(v -> v.getLoteId().equals(id)).toList();
    }

    public int vendido(Long id) {
        return vendas(id).stream().mapToInt(Venda::getQuantidade).sum();
    }

    public BigDecimal custoFatia(Produto produto) {
        return Calculo.dinheiro(Calculo.custo(produto, itens(produto.getId())));
    }

    public BigDecimal custoBolo(Produto produto) {
        return Calculo.dinheiro(Calculo.custo(produto, itens(produto.getId()))
                .multiply(BigDecimal.valueOf(produto.getRendimento())));
    }

    public BigDecimal precoSugerido(Produto produto) {
        return Calculo.sugerido(produto, itens(produto.getId()));
    }

    public BigDecimal lucroSugeridoBolo(Produto produto) {
        var receitaLiquida = Calculo.liquido(precoSugerido(produto)
                .multiply(BigDecimal.valueOf(produto.getRendimento())), produto.getTaxa());
        return Calculo.dinheiro(receitaLiquida.subtract(custoBolo(produto)));
    }

    public Evento aberto(Long id) {
        var e = eventos.bloquear(id).orElseThrow();
        exigir(!e.getEncerrado(), "Evento encerrado. Reabra para alterar.");
        return e;
    }

    public void produto(Produto p) {
        exigir(!p.getNome().isBlank() && p.getNome().length() <= 200, "Informe um nome de até 200 caracteres.");
        exigir(p.getRendimento() > 0 && p.getRendimento() <= 10000, "Rendimento inválido.");
        for (var v : List.of(p.getMaoObra(), p.getEnergia(), p.getEmbalagem(), p.getPerda(), p.getTaxa(),
                p.getMargem()))
            positivo(v);
        exigir(p.getTaxa().add(p.getMargem()).compareTo(Calculo.CEM) < 0, "Margem + taxas devem ser menores que 100%.");
        exigir(p.getPerda().compareTo(Calculo.CEM) <= 0, "Perdas devem ser de até 100%.");
        if (p.getId() != null)
            produtos.findById(p.getId()).orElseThrow();
        produtos.save(p);
    }

    public void ingrediente(Ingrediente i) {
        produtos.findById(i.getProdutoId()).orElseThrow();
        exigir(!i.getNome().isBlank(), "Informe o ingrediente.");
        positivo(i.getPrecoPacote());
        positivo(i.getQuantidadeUsada());
        exigir(i.getQuantidadePacote() != null && i.getQuantidadePacote().signum() > 0,
                "Quantidade da embalagem deve ser positiva.");
        ingredientes.save(i);
    }

    public void lote(Long eventoId, Long produtoId, int bolos, BigDecimal preco) {
        aberto(eventoId);
        var p = produtos.findById(produtoId).orElseThrow();
        exigir(bolos > 0 && bolos <= 10000, "Quantidade de receitas inválida.");
        exigir(!itens(produtoId).isEmpty(), "Cadastre os ingredientes antes de produzir.");
        positivo(preco);
        exigir(preco.signum() > 0, "Preço deve ser maior que zero.");
        var l = new Lote();
        l.setEventoId(eventoId);
        l.setProduto(p.getNome());
        l.setQuantidade(Math.multiplyExact(bolos, p.getRendimento()));
        l.setCustoUnitario(Calculo.custo(p, itens(produtoId)).setScale(8, RoundingMode.HALF_UP));
        l.setPreco(preco);
        l.setTaxa(p.getTaxa());
        lotes.save(l);
    }

    public void venda(Long loteId, int quantidade, BigDecimal preco) {
        var l = lotes.findById(loteId).orElseThrow();
        aberto(l.getEventoId());
        exigir(quantidade > 0 && quantidade <= l.getQuantidade() - vendido(loteId),
                "Quantidade maior que o saldo disponível ou inválida.");
        positivo(preco);
        var v = new Venda();
        v.setLoteId(loteId);
        v.setQuantidade(quantidade);
        v.setPreco(preco);
        vendas.save(v);
        /* O bloqueio pessimista do evento protege o saldo de vendas concorrentes. */}

    public Map<String, Object> resumo(Long id) {
        BigDecimal custo = BigDecimal.ZERO, previsto = BigDecimal.ZERO, real = BigDecimal.ZERO,
                liquido = BigDecimal.ZERO, prevLiquido = BigDecimal.ZERO;
        int unidades = 0, vendidas = 0;
        for (var l : lotes(id)) {
            custo = custo.add(l.getCustoUnitario().multiply(BigDecimal.valueOf(l.getQuantidade())));
            var br = l.getPreco().multiply(BigDecimal.valueOf(l.getQuantidade()));
            previsto = previsto.add(br);
            prevLiquido = prevLiquido.add(Calculo.liquido(br, l.getTaxa()));
            unidades += l.getQuantidade();
            vendidas += vendido(l.getId());
            for (var v : vendas(l.getId())) {
                var b = v.getPreco().multiply(BigDecimal.valueOf(v.getQuantidade()));
                real = real.add(b);
                liquido = liquido.add(Calculo.liquido(b, l.getTaxa()));
            }
        }
        for (var d : despesas(id))
            custo = custo.add(d.getValor());
        custo = Calculo.dinheiro(custo);
        var m = new LinkedHashMap<String, Object>();
        m.put("investimento", custo);
        m.put("previsto", previsto);
        m.put("real", real);
        m.put("lucroPrevisto", Calculo.dinheiro(prevLiquido.subtract(custo)));
        m.put("lucroReal", Calculo.dinheiro(liquido.subtract(custo)));
        m.put("saldo", unidades - vendidas);
        m.put("vendidas", vendidas);
        m.put("unidades", unidades);
        m.put("percentualVendido", unidades > 0 ? Math.round(vendidas * 100.0 / unidades) : 0);
        m.put("equilibrio", prevLiquido.signum() > 0 ? custo.multiply(BigDecimal.valueOf(unidades))
                .divide(prevLiquido, 0, RoundingMode.CEILING).toPlainString() : "—");
        return m;
    }

    public ProdutoRepository produtosRepo() {
        return produtos;
    }

    public IngredienteRepository ingredientesRepo() {
        return ingredientes;
    }

    public EventoRepository eventosRepo() {
        return eventos;
    }

    public LoteRepository lotesRepo() {
        return lotes;
    }

    public DespesaRepository despesasRepo() {
        return despesas;
    }

    public VendaRepository vendasRepo() {
        return vendas;
    }
}
