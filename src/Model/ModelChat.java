/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author charles
 */
public class ModelChat implements Serializable {

    private String grupo;
    private String nome;
    private String texto;
    private String nomeReservado;
    private String grupoReservado;
    private Set<String> setOnlines = new HashSet<>();
    private Set<String> setGrupo = new HashSet<>();
    private Action action;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getNomeReservado() {
        return nomeReservado;
    }

    public void setNomeReservado(String nomeReservado) {
        this.nomeReservado = nomeReservado;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * @return the setOnlines
     */
    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    /**
     * @param setOnlines the setOnlines to set
     */
    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Set<String> getSetGrupo() {
        return setGrupo;
    }

    public void setSetGrupo(Set<String> setGrupo) {
        this.setGrupo = setGrupo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getGrupoReservado() {
        return grupoReservado;
    }

    public void setGrupoReservado(String grupoReservado) {
        this.grupoReservado = grupoReservado;
    }

    public enum Action {
        CONNECT, DISCONNECT, SEND_ONE,SEND_GROUP, SEND_ALL, USERS_ONLINE, FRONT
    }

}
