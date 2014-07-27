package net.marcoreis.controlapreco.controlador;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import net.marcoreis.controlapreco.entidades.Usuario;

import org.apache.log4j.Logger;

public class ControladorGenerico implements Serializable {
    private static final long serialVersionUID = -5895396595360064610L;
    protected static final Logger logger = Logger
            .getLogger(ControladorGenerico.class);
    protected String MENSAGEM_SUCESSO = "Dados gravados com sucesso";
    protected String MENSAGEM_ERRO = "Não foi possível gravar";

    @PostConstruct
    public void init() {
    }

    public Usuario getUsuario() {
        Usuario usuario = (Usuario) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get("usuario");
        return usuario;
    }

    public void colocarUsuarioNaSessao(Usuario usuario) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .put("usuario", usuario);

    }

    protected void infoMsg(String message) {
        logger.info(message);
        FacesMessage msg = new FacesMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    protected void errorMsg(Throwable t) {
        logger.error(t);
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                t.getLocalizedMessage(), "Erro");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    protected void errorMsg(String message) {
        logger.error(message);
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                message, "Erro");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getParametro(String nome) {
        return FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get(nome);
    }

}
