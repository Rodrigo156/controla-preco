package net.marcoreis.controlapreco.controlador;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import net.marcoreis.controlapreco.entidades.Categoria;
import net.marcoreis.controlapreco.service.ServicoCategoria;

import org.apache.log4j.Logger;

@ManagedBean
@RequestScoped
public class ControladorCategoria extends ControladorGenerico {
    private static final long serialVersionUID = 6273372374453297684L;
    private Categoria categoria;
    private List<Categoria> categorias;
    private ServicoCategoria servico = new ServicoCategoria();
    private static Logger logger = Logger.getLogger(ControladorCategoria.class);

    @PostConstruct
    public void init() {
        categoria = new Categoria();
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public List<Categoria> getCategorias() {
        if (categorias == null) {
            categorias = getServico().findAll(Categoria.class);
        }
        return categorias;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public ServicoCategoria getServico() {
        return servico;
    }

    public String editar(Categoria categoria) {
        this.categoria = categoria;
        return "categoria";
    }

    public void salvar() {
        try {
            getCategoria().setUsuario(getUsuario());
            getServico().salvar(getCategoria());
            infoMsg(MENSAGEM_SUCESSO);
        } catch (Exception e) {
            logger.error(e);
            errorMsg(MENSAGEM_ERRO);
        }
    }

    public List<Categoria> completarCategoria(String query) {
        return getServico().findCategoriasPorNome(query);
    }

}
