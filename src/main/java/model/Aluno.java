package model;

public class Aluno extends Usuario {

    public Aluno(String nome, String matricula) {
        super(nome, matricula);
    }

    @Override
    public int calcularPrazoDevolucao(TipoMaterial tipo) {
        // Aluno usa apenas o prazo base do material
        return tipo.getPrazoBase();
    }
}