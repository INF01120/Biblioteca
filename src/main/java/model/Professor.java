package model;

public class Professor extends Usuario {
    private TipoVinculo vinculo;

    public Professor(String nome, String matricula, TipoVinculo vinculo) {
        super(nome, matricula);
        this.vinculo = vinculo;
    }

    @Override
    public int calcularPrazoDevolucao(TipoMaterial tipo) {
        // Professor tem o prazo base somado aos dias extras do seu vínculo
        return tipo.getPrazoBase() + vinculo.getDiasExtras();
    }
}