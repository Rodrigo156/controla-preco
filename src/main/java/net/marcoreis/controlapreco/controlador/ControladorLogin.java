package net.marcoreis.controlapreco.controlador;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import net.marcoreis.controlapreco.entidades.Usuario;
import net.marcoreis.controlapreco.service.ServicoUsuario;

@ManagedBean
@SessionScoped
public class ControladorLogin extends ControladorGenerico {
    private static final long serialVersionUID = -3509083378761959358L;
    private ServicoUsuario servico = new ServicoUsuario();
    protected static final String LOGIN_INVALIDO = "Usuário inválido";
    private String email;

    @PostConstruct
    public void init() {
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public ServicoUsuario getServico() {
        return servico;
    }

    public String login() {
        try {
            Usuario usuario = getServico().findByEmail(getEmail());
            if (usuario != null) {
                colocarUsuarioNaSessao(usuario);
                return "inicio";
            }
        } catch (Exception e) {
            errorMsg(LOGIN_INVALIDO);
        }
        errorMsg(LOGIN_INVALIDO);
        return null;
    }

}
