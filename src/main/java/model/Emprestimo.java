package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Emprestimo {
    private Exemplar exemplar;
    private Usuario leitor;
    private LocalDate dataRetirada;
    private LocalDate dataDevolucao;

    public Emprestimo(Usuario leitor, Exemplar exemplar) {
        this.leitor = leitor;
        this.exemplar = exemplar;
        this.dataRetirada = LocalDate.now();

        // POLIMORFISMO EM AÇÃO:
        // O método chamado depende se 'leitor' é Aluno ou Professor
        int diasPrazo = leitor.calcularPrazoDevolucao(exemplar.getTipo());
        this.dataDevolucao = dataRetirada.plusDays(diasPrazo);
    }

    public String gerarRecibo() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return String.format(
                "--- RECIBO DE EMPRÉSTIMO ---\nLeitor: %s (Matrícula: %s)\nExemplar: %s (ISBN: %s)\nData Retirada: %s\nData Devolução Prevista: %s\n----------------------------",
                leitor.getNome(), leitor.getMatricula(), exemplar.getTitulo(), exemplar.getIsbn(),
                dataRetirada.format(fmt), dataDevolucao.format(fmt)
        );
    }
}