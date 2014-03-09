package net.marcoreis.controlapreco.entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Produto implements IPersistente {
    @Id
    @GeneratedValue
    private Long id;
    private String nome;
    @ManyToOne
    private Categoria categoria;
    @Lob
    private byte[] imagem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
