package model;

public abstract class Usuario {
    protected String nome;
    protected String matricula;

    public Usuario(String nome, String matricula) {
        this.nome = nome;
        this.matricula = matricula;
    }

    public String getNome() { return nome; }
    public String getMatricula() { return matricula; }

    // Método que será resolvido via Polimorfismo
    public abstract int calcularPrazoDevolucao(TipoMaterial tipo);
}