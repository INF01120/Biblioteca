package model;

public enum TipoVinculo {
    TITULAR(15),
    ADJUNTO(10),
    VISITANTE(5);

    private final int diasExtras;

    TipoVinculo(int diasExtras) {
        this.diasExtras = diasExtras;
    }

    public int getDiasExtras() {
        return diasExtras;
    }
}