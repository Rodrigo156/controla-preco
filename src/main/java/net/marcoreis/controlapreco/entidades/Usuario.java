package net.marcoreis.controlapreco.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Usuario {
    private Long id;
    private String email;
    private String nome;
    private Date ultimoLogin;

    @Id
    @GeneratedValue
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getEmail() {
	return email;
    }

    public void setNome(String nome) {
	this.nome = nome;
    }

    public String getNome() {
	return nome;
    }

    public void setUltimoLogin(Date ultimoLogin) {
	this.ultimoLogin = ultimoLogin;
    }

    public Date getUltimoLogin() {
	return ultimoLogin;
    }
}
