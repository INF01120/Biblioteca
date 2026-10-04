package model;

public enum TipoMaterial {
    LIVRO_DIDATICO(7),
    LITERATURA(14),
    PERIODICO(3);

    private final int prazoBase;

    TipoMaterial(int prazoBase) {
        this.prazoBase = prazoBase;
    }

    public int getPrazoBase() {
        return prazoBase;
    }
}