package com.petfriends.transporte.domain.valueobject;

import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 * Questão 4: Value Object (Objeto de Valor) usado no Transporte.
 * Representa os dados básicos do destino da entrega.
 */
@Embeddable
public class DadosDestino {
    private String cep;
    private String logradouro;

    protected DadosDestino() {
        // JPA
    }

    public DadosDestino(String cep, String logradouro) {
        if (cep == null || !cep.matches("\\d{5}-\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido. Use o formato XXXXX-XXX");
        }
        this.cep = cep;
        this.logradouro = logradouro;
    }

    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DadosDestino that = (DadosDestino) o;
        return Objects.equals(cep, that.cep) &&
               Objects.equals(logradouro, that.logradouro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cep, logradouro);
    }
}
