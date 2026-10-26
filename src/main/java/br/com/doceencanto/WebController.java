package br.com.doceencanto;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.*;

@Controller
public class WebController {
    final Gestao g;

    public WebController(Gestao g) {
        this.g = g;
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping("/")
    String home(Model m) {
        var eventos = g.eventosRepo().findAll();
        var produtos = g.produtosRepo().findAll();
        m.addAttribute("eventos", eventos);
        m.addAttribute("produtos", produtos);
        m.addAttribute("eventosAbertos", eventos.stream().filter(e -> !e.getEncerrado()).count());
        m.addAttribute("eventosEncerrados", eventos.stream().filter(Evento::getEncerrado).count());
        m.addAttribute("g", g);
        return "home";
    }

    @GetMapping("/produtos")
    String produtos(Model m) {
        m.addAttribute("produtos", g.produtosRepo().findAll());
        m.addAttribute("produto", new Produto());
        m.addAttribute("g", g);
        return "produtos";
    }

    @GetMapping("/produtos/{id}")
    String produto(@PathVariable Long id, Model m) {
        var p = g.produtosRepo().findById(id).orElseThrow();
        m.addAttribute("produto", p);
        m.addAttribute("itens", g.itens(id));
        m.addAttribute("custo", g.custoFatia(p));
        m.addAttribute("custoBolo", g.custoBolo(p));
        m.addAttribute("sugerido", Calculo.sugerido(p, g.itens(id)));
        m.addAttribute("lucroBolo", g.lucroSugeridoBolo(p));
        return "produto";
    }

    @PostMapping("/produtos")
    String salvar(@ModelAttribute Produto produto) {
        g.produto(produto);
        return "redirect:/produtos/" + produto.getId();
    }

    @PostMapping("/ingredientes")
    String ingrediente(@ModelAttribute Ingrediente ingrediente) {
        g.ingrediente(ingrediente);
        return "redirect:/produtos/" + ingrediente.getProdutoId();
    }

    @PostMapping("/ingredientes/{id}/excluir")
    String excluir(@PathVariable Long id) {
        var i = g.ingredientesRepo().findById(id).orElseThrow();
        g.ingredientesRepo().delete(i);
        return "redirect:/produtos/" + i.getProdutoId();
    }

    @GetMapping("/eventos/novo")
    String novo(Model m) {
        m.addAttribute("evento", new Evento());
        return "evento-form";
    }

    @PostMapping("/eventos")
    String evento(@ModelAttribute Evento evento) {
        Gestao.exigir(evento.getId() == null, "Cadastro inválido.");
        Gestao.exigir(!evento.getNome().isBlank() && evento.getData() != null, "Preencha nome e data.");
        evento.setEncerrado(false);
        g.eventosRepo().save(evento);
        return "redirect:/eventos/" + evento.getId();
    }

    @GetMapping("/eventos/{id}")
    String evento(@PathVariable Long id, Model m) {
        m.addAttribute("evento", g.eventosRepo().findById(id).orElseThrow());
        m.addAttribute("lotes", g.lotes(id));
        m.addAttribute("despesas", g.despesas(id));
        m.addAttribute("produtos", g.produtosRepo().findAll());
        m.addAttribute("resumo", g.resumo(id));
        m.addAttribute("g", g);
        return "evento";
    }

    @PostMapping("/eventos/{id}/lotes")
    String lote(@PathVariable Long id, @RequestParam Long produtoId, @RequestParam int bolos,
            @RequestParam BigDecimal preco) {
        g.lote(id, produtoId, bolos, preco);
        return "redirect:/eventos/" + id;
    }

    @PostMapping("/eventos/{id}/despesas")
    @org.springframework.transaction.annotation.Transactional
    String despesa(@PathVariable Long id, @RequestParam String descricao, @RequestParam BigDecimal valor) {
        g.aberto(id);
        Gestao.positivo(valor);
        Gestao.exigir(!descricao.isBlank(), "Informe a despesa.");
        var d = new Despesa();
        d.setEventoId(id);
        d.setDescricao(descricao);
        d.setValor(valor);
        g.despesasRepo().save(d);
        return "redirect:/eventos/" + id;
    }

    @PostMapping("/lotes/{id}/vendas")
    String venda(@PathVariable Long id, @RequestParam int quantidade, @RequestParam BigDecimal preco) {
        g.venda(id, quantidade, preco);
        return "redirect:/eventos/" + g.lotesRepo().findById(id).orElseThrow().getEventoId();
    }

    @PostMapping("/vendas/{id}/excluir")
    @org.springframework.transaction.annotation.Transactional
    String estorno(@PathVariable Long id) {
        var v = g.vendasRepo().findById(id).orElseThrow();
        var l = g.lotesRepo().findById(v.getLoteId()).orElseThrow();
        g.aberto(l.getEventoId());
        g.vendasRepo().delete(v);
        return "redirect:/eventos/" + l.getEventoId();
    }

    @PostMapping("/despesas/{id}/excluir")
    @org.springframework.transaction.annotation.Transactional
    String excluirDespesa(@PathVariable Long id) {
        var d = g.despesasRepo().findById(id).orElseThrow();
        g.aberto(d.getEventoId());
        g.despesasRepo().delete(d);
        return "redirect:/eventos/" + d.getEventoId();
    }

    @PostMapping("/eventos/{id}/status")
    @org.springframework.transaction.annotation.Transactional
    String status(@PathVariable Long id) {
        var e = g.eventosRepo().bloquear(id).orElseThrow();
        e.setEncerrado(!e.getEncerrado());
        return "redirect:/eventos/" + id;
    }

    @ExceptionHandler({ IllegalArgumentException.class, org.springframework.validation.BindException.class })
    String erro(Exception e, Model m) {
        m.addAttribute("mensagem", e instanceof IllegalArgumentException ? e.getMessage()
                : "Confira os campos e use ponto como separador decimal.");
        return "erro";
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    String naoEncontrado(Model m) {
        m.addAttribute("mensagem", "Registro não encontrado.");
        return "erro";
    }
}
