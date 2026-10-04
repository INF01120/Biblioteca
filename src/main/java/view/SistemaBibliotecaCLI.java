package view;

import controller.ControleBiblioteca;
import model.Emprestimo;
import model.Exemplar;
import java.util.Scanner;

public class SistemaBibliotecaCLI {
    private ControleBiblioteca controller;
    private Scanner scanner;

    public SistemaBibliotecaCLI() {
        this.controller = new ControleBiblioteca();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcao;
        do {
            System.out.println("\n=== SISTEMA DE BIBLIOTECA (MVC) ===");
            System.out.println("1. Listar Acervo");
            System.out.println("2. Novo Empréstimo");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            // Tratamento básico para evitar crash caso o usuário digite letras no menu
            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // limpar buffer
            } catch (Exception e) {
                System.out.println("Entrada inválida! Digite um número.");
                scanner.nextLine(); // limpar buffer do erro
                opcao = -1;
                continue;
            }

            switch (opcao) {
                case 1: listarAcervo(); break;
                case 2: fluxoEmprestimo(); break;
                case 0: System.out.println("Encerrando o sistema..."); break;
                default: System.out.println("Opção inválida!");
            }
        } while (opcao != 0);

        scanner.close();
    }

    private void listarAcervo() {
        System.out.println("\n--- ACERVO ATUAL ---");
        for (Exemplar e : controller.getAcervo()) {
            String status = e.isDisponivel() ? "Disponível" : "Emprestado";
            System.out.printf("[%s] %s - Categoria: %s (%s)\n",
                    e.getIsbn(), e.getTitulo(), e.getTipo(), status);
        }
    }

    private void fluxoEmprestimo() {
        System.out.println("\n--- REALIZAR EMPRÉSTIMO ---");
        System.out.print("Digite a Matrícula do Leitor (ex: 111 ou 222): ");
        String matricula = scanner.nextLine();

        System.out.print("Digite o ISBN do Exemplar (ex: 978-01): ");
        String isbn = scanner.nextLine();

        try {
            // A view delega a lógica e recebe o recibo pronto
            Emprestimo emp = controller.realizarEmprestimo(matricula, isbn);
            System.out.println("\n" + emp.gerarRecibo());

        } catch (Exception e) {
            // Se algo der errado na regra de negócio (Controller/Model), a exceção é impressa aqui
            System.out.println("ERRO NA OPERAÇÃO: " + e.getMessage());
        }
    }

    // Ponto de entrada do Java
    public static void main(String[] args) {
        SistemaBibliotecaCLI app = new SistemaBibliotecaCLI();
        app.iniciar();
    }
}