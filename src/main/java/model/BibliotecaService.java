package model;

import java.util.ArrayList;
import java.util.List;

public class BibliotecaService {
    private List<Usuario> repositorioUsuarios;
    private List<Exemplar> acervo;
    private List<Emprestimo> emprestimosAtivos;

    public BibliotecaService() {
        this.repositorioUsuarios = new ArrayList<>();
        this.acervo = new ArrayList<>();
        this.emprestimosAtivos = new ArrayList<>();
        popularDadosIniciaisParaAula();
    }

    private void popularDadosIniciaisParaAula() {
        // Mock de dados transferidos para o Service
        repositorioUsuarios.add(new Aluno("João da Silva", "111"));
        repositorioUsuarios.add(new Professor("Maria Oliveira", "222", TipoVinculo.TITULAR));

        acervo.add(new Exemplar("Java Como Programar", "978-01", TipoMaterial.LIVRO_DIDATICO));
        acervo.add(new Exemplar("O Senhor dos Anéis", "978-02", TipoMaterial.LITERATURA));
        acervo.add(new Exemplar("Revista da SBC", "978-03", TipoMaterial.PERIODICO));
    }

    public Emprestimo registrarEmprestimo(String matricula, String isbn) throws Exception {
        Usuario u = buscarUsuario(matricula);
        if (u == null) throw new Exception("Usuário não encontrado!");

        Exemplar ex = buscarExemplar(isbn);
        if (ex == null) throw new Exception("Exemplar não encontrado!");
        if (!ex.isDisponivel()) throw new Exception("Este exemplar já está emprestado!");

        Emprestimo emp = new Emprestimo(u, ex);
        ex.setDisponivel(false);
        emprestimosAtivos.add(emp);

        return emp;
    }

    // Aqui estão os métodos que o compilador não estava encontrando
    private Usuario buscarUsuario(String matricula) {
        for (Usuario u : repositorioUsuarios) {
            if (u.getMatricula().equals(matricula)) return u;
        }
        return null;
    }

    private Exemplar buscarExemplar(String isbn) {
        for (Exemplar e : acervo) {
            if (e.getIsbn().equals(isbn)) return e;
        }
        return null;
    }

    public List<Exemplar> getAcervo() {
        return acervo;
    }
}