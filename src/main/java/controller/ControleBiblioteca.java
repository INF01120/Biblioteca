package controller;

import model.BibliotecaService;
import model.Emprestimo;
import model.Exemplar;
import java.util.List;

public class ControleBiblioteca {

    private BibliotecaService bibliotecaService;

    public ControleBiblioteca() {
        // O Controller agora apenas inicializa o Service
        this.bibliotecaService = new BibliotecaService();
    }

    // O método realizarEmprestimo delega a execução para o Service
    public Emprestimo realizarEmprestimo(String matricula, String isbn) throws Exception {
        return bibliotecaService.registrarEmprestimo(matricula, isbn);
    }

    // A view ainda precisa da lista de acervo para exibir o menu 1
    public List<Exemplar> getAcervo() {
        return bibliotecaService.getAcervo();
    }
}