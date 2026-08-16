package br.com.william.autovistordesktop.session;

public class SessaoUsuario {

    private static SessaoUsuario instancia;

    private String token;
    private Long id;
    private String tipo; // "CLIENTE" ou "FUNCIONARIO"
    private String role; // "ROLE_CLIENTE", "ROLE_VISTORIADOR", "ROLE_GERENTE"

    private SessaoUsuario() {
    }

    public static SessaoUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SessaoUsuario();
        }
        return instancia;
    }

    public void iniciar(String token, Long id, String tipo, String role) {
        this.token = token;
        this.id = id;
        this.tipo = tipo;
        this.role = role;
    }

    public void encerrar() {
        this.token = null;
        this.id = null;
        this.tipo = null;
        this.role = null;
    }

    public boolean estaLogado() {
        return token != null;
    }

    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getRole() {
        return role;
    }
}