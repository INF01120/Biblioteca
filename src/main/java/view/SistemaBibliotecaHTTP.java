package view;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import controller.ControleBiblioteca;
import model.Emprestimo;
import model.Exemplar;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * View alternativa: mesma regra de negócio (Controller/Model), mas servida
 * como páginas HTML pelo servidor embutido do JDK (com.sun.net.httpserver).
 */
public class SistemaBibliotecaHTTP {
    private final ControleBiblioteca controller = new ControleBiblioteca();

    public void iniciar(int porta) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);
        server.createContext("/", this::paginaInicial);
        server.createContext("/emprestimo", this::novoEmprestimo);
        server.start();
        System.out.println("Servidor em http://localhost:" + porta + "/  (Ctrl+C para encerrar)");
    }

    private void paginaInicial(HttpExchange ex) throws IOException {
        if (!ex.getRequestURI().getPath().equals("/")) {
            responder(ex, 404, pagina("Não encontrado", "<p>Página não encontrada.</p>"));
            return;
        }
        responder(ex, 200, pagina("Sistema de Biblioteca", montarAcervo() + formulario()));
    }

    private void novoEmprestimo(HttpExchange ex) throws IOException {
        if (!ex.getRequestMethod().equals("POST")) {
            ex.getResponseHeaders().set("Location", "/");
            ex.sendResponseHeaders(303, -1);
            ex.close();
            return;
        }

        Map<String, String> campos = lerFormulario(ex);
        String corpo;
        int status = 200;
        try {
            Emprestimo emp = controller.realizarEmprestimo(
                    campos.getOrDefault("matricula", ""), campos.getOrDefault("isbn", ""));
            corpo = "<pre>" + escapar(emp.gerarRecibo()) + "</pre>";
        } catch (Exception e) {
            status = 400;
            corpo = "<p class=\"erro\">ERRO NA OPERAÇÃO: " + escapar(e.getMessage()) + "</p>";
        }
        responder(ex, status, pagina("Empréstimo", corpo + "<p><a href=\"/\">Voltar</a></p>"));
    }

    private String montarAcervo() {
        StringBuilder sb = new StringBuilder("<h2>Acervo</h2><table><tr><th>ISBN</th><th>Título</th><th>Categoria</th><th>Status</th></tr>");
        for (Exemplar e : controller.getAcervo()) {
            sb.append("<tr><td>").append(escapar(e.getIsbn()))
              .append("</td><td>").append(escapar(e.getTitulo()))
              .append("</td><td>").append(e.getTipo())
              .append("</td><td>").append(e.isDisponivel() ? "Disponível" : "Emprestado")
              .append("</td></tr>");
        }
        return sb.append("</table>").toString();
    }

    private String formulario() {
        return "<h2>Novo Empréstimo</h2>"
             + "<form method=\"post\" action=\"/emprestimo\">"
             + "<label>Matrícula (ex: 111 ou 222) <input name=\"matricula\" required></label>"
             + "<label>ISBN (ex: 978-01) <input name=\"isbn\" required></label>"
             + "<button>Emprestar</button></form>";
    }

    private String pagina(String titulo, String conteudo) {
        return "<!doctype html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\">"
             + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
             + "<title>" + escapar(titulo) + "</title><style>"
             + "body{font-family:sans-serif;max-width:40rem;margin:2rem auto;padding:0 1rem}"
             + "table{border-collapse:collapse;width:100%}th,td{border:1px solid #999;padding:.4rem;text-align:left}"
             + "label{display:block;margin:.5rem 0}.erro{color:#b00020}</style></head><body>"
             + "<h1>" + escapar(titulo) + "</h1>" + conteudo + "</body></html>";
    }

    private Map<String, String> lerFormulario(HttpExchange ex) throws IOException {
        String corpo = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> campos = new HashMap<>();
        for (String par : corpo.split("&")) {
            String[] kv = par.split("=", 2);
            if (kv.length == 2) {
                campos.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                           URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return campos;
    }

    private void responder(HttpExchange ex, int status, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String escapar(String s) {
        return String.valueOf(s).replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    // Ponto de entrada alternativo. Porta opcional: args[0] (padrão 8080)
    public static void main(String[] args) throws IOException {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        new SistemaBibliotecaHTTP().iniciar(porta);
    }
}
