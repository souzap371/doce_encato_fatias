package br.com.doceencanto;

import java.math.*;
import java.util.*;

public final class Calculo {
    public static final BigDecimal CEM = new BigDecimal("100");

    public static BigDecimal dinheiro(BigDecimal x) {
        return x.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal percentual(BigDecimal x) {
        return x.divide(CEM, 10, RoundingMode.HALF_UP);
    }

    public static BigDecimal custo(Produto p, List<Ingrediente> itens) {
        BigDecimal total = p.getMaoObra().add(p.getEnergia());
        for (var i : itens)
            total = total.add(i.getPrecoPacote().multiply(i.getQuantidadeUsada()).divide(i.getQuantidadePacote(), 10,
                    RoundingMode.HALF_UP));
        return total.multiply(BigDecimal.ONE.add(percentual(p.getPerda())))
                .divide(BigDecimal.valueOf(p.getRendimento()), 10, RoundingMode.HALF_UP).add(p.getEmbalagem());
    }

    public static BigDecimal sugerido(Produto p, List<Ingrediente> itens) {
        return custo(p, itens).divide(BigDecimal.ONE.subtract(percentual(p.getTaxa().add(p.getMargem()))), 2,
                RoundingMode.CEILING);
    }

    public static BigDecimal liquido(BigDecimal bruto, BigDecimal taxa) {
        return dinheiro(bruto.multiply(BigDecimal.ONE.subtract(percentual(taxa))));
    }
}