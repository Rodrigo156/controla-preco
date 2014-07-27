package net.marcoreis.controlapreco.controlador;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import net.marcoreis.controlapreco.entidades.Usuario;
import net.marcoreis.controlapreco.service.ServicoUsuario;

@ManagedBean
@RequestScoped
public class ControladorUsuario extends ControladorGenerico {
    private static final long serialVersionUID = -2658024901938874346L;
    private Usuario usuario;
    private Collection<Usuario> usuarios;
    private ServicoUsuario servico = new ServicoUsuario();

    @PostConstruct
    public void init() {
        carregarUsuarios();
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        if (usuario == null) {
            usuario = new Usuario();
        }
        return usuario;
    }

    public void salvar() {
        getServico().salvar(getUsuario());
        infoMsg(MENSAGEM_SUCESSO);
    }

    public ServicoUsuario getServico() {
        return servico;
    }

    public void carregarUsuarios() {
        usuarios = getServico().findAll(Usuario.class);
    }

    public Collection<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Collection<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public String editar(Usuario usuario) {
        return "usuario";
    }
}
