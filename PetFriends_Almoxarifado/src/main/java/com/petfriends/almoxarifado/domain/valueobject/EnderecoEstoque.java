package com.petfriends.almoxarifado.domain.valueobject;

import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 * Questão 2: Value Object (Objeto de Valor) usado no Almoxarifado.
 * Representa a localização física de um item no estoque.
 */
@Embeddable
public class EnderecoEstoque {
    private String corredor;
    private String prateleira;

    protected EnderecoEstoque() {
        // Construtor vazio exigido pelo JPA
    }

    public EnderecoEstoque(String corredor, String prateleira) {
        this.corredor = corredor;
        this.prateleira = prateleira;
    }

    public String getCorredor() {
        return corredor;
    }

    public String getPrateleira() {
        return prateleira;
    }

    // Value Objects devem implementar equals e hashCode baseados em seus atributos
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnderecoEstoque that = (EnderecoEstoque) o;
        return Objects.equals(corredor, that.corredor) &&
               Objects.equals(prateleira, that.prateleira);
    }

    @Override
    public int hashCode() {
        return Objects.hash(corredor, prateleira);
    }
}
