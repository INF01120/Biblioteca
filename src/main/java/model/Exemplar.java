package model;

public class Exemplar {
    private String titulo;
    private String isbn;
    private TipoMaterial tipo;
    private boolean disponivel;

    public Exemplar(String titulo, String isbn, TipoMaterial tipo) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.tipo = tipo;
        this.disponivel = true;
    }

    public String getTitulo() { return titulo; }
    public String getIsbn() { return isbn; }
    public TipoMaterial getTipo() { return tipo; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
}