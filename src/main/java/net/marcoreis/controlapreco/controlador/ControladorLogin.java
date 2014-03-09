package net.marcoreis.controlapreco.controlador;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import net.marcoreis.controlapreco.entidades.Usuario;

@ManagedBean
@SessionScoped
public class ControladorLogin extends ControladorGenerico {
    private static final long serialVersionUID = -3509083378761959358L;
    private ServicoUsuario servico = new ServicoUsuario();

    @PostConstruct
    public void init() {
        setUsuario(new Usuario());
    }

    public ServicoUsuario getServico() {
        return servico;
    }

    public String login() {
        try {
            Usuario usuario = getServico().findByEmail(getUsuario().getEmail());
            if (usuario != null) {
                setUsuario(usuario);
                return "inicio";
            }
        } catch (Exception e) {
            errorMsg(LOGIN_INVALIDO);
        }
        errorMsg(LOGIN_INVALIDO);
        return null;
    }
}
