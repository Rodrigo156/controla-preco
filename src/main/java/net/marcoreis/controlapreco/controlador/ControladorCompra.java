package net.marcoreis.controlapreco.controlador;

import java.sql.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import net.marcoreis.controlapreco.entidades.Compra;

@ManagedBean
@RequestScoped
public class ControladorCompra extends ControladorGenerico {
    private static final long serialVersionUID = -3786512477792609776L;
    private Compra compra;
    private List<Compra> compras;
    private ServicoCompra servico = new ServicoCompra();

    @PostConstruct
    public void init() {
        compra = new Compra();
        compra.setData(new Date(System.currentTimeMillis()));
        compras = getServico().findAll(Compra.class);
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public ServicoCompra getServico() {
        return servico;
    }

    public void salvar() {
        try {
            getServico().salvar(getCompra());
            infoMsg(MENSAGEM_SUCESSO);
        } catch (Exception e) {
            errorMsg(e);
        }
    }

    public void editar(Compra compra) {
        this.compra = compra;
    }
}
