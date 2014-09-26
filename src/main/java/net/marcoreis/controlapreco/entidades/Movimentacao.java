package net.marcoreis.controlapreco.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Movimentacao extends Persistente {
    private static final long serialVersionUID = 831627963678011028L;
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;
    private Double valor;
    @Temporal(TemporalType.DATE)
    private Date data;
    @ManyToOne (optional = false)
    private Categoria categoria;

    public void setData(Date data) {
        this.data = data;
    }

    public Date getData() {
        return data;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getValor() {
        return valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
