package view;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import controller.ControleBiblioteca;
import model.Emprestimo;
import model.Exemplar;

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
                    campos.getOrDefault("matricula", ""), 
                    campos.getOrDefault("isbn", "")
            );
            corpo = """
                <h2>✅ Sucesso!</h2>
                <div class="recibo">%s</div>
                """.formatted(escapar(emp.gerarRecibo()));
        } catch (Exception e) {
            status = 400;
            corpo = """
                <h2>⚠️ Atenção</h2>
                <div class="erro"><strong>ERRO NA OPERAÇÃO:</strong> %s</div>
                """.formatted(escapar(e.getMessage()));
        }
        
        String htmlFinal = corpo + """
            <br><br>
            <a class="btn" href="/">← Voltar ao Início</a>
            """;
            
        responder(ex, status, pagina("Resultado do Empréstimo", htmlFinal));
    }

    private String montarAcervo() {
        StringBuilder sb = new StringBuilder("""
            <h2>📚 Acervo da Biblioteca</h2>
            <table>
                <thead>
                    <tr>
                        <th>ISBN</th>
                        <th>Título</th>
                        <th>Categoria</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
            """);
            
        for (Exemplar e : controller.getAcervo()) {
            String badgeClass = e.isDisponivel() ? "badge disp" : "badge emp";
            String statusText = e.isDisponivel() ? "Disponível" : "Emprestado";
            
            String linha = """
                <tr>
                    <td>%s</td>
                    <td><strong>%s</strong></td>
                    <td>%s</td>
                    <td><span class="%s">%s</span></td>
                </tr>
                """.formatted(
                    escapar(e.getIsbn()), 
                    escapar(e.getTitulo()), 
                    e.getTipo(), 
                    badgeClass, 
                    statusText
                );
            sb.append(linha);
        }
        
        sb.append("""
                </tbody>
            </table>
            """);
        return sb.toString();
    }

    private String formulario() {
        return """
            <h2>🔄 Novo Empréstimo</h2>
            <div class="card">
                <form method="post" action="/emprestimo">
                    <div>
                        <label>Matrícula do Aluno</label>
                        <input name="matricula" placeholder="Ex: 111 ou 222" required>
                    </div>
                    <div>
                        <label>Código ISBN</label>
                        <input name="isbn" placeholder="Ex: 978-01" required>
                    </div>
                    <button type="submit">Confirmar Empréstimo</button>
                </form>
            </div>
            """;
    }

    private String pagina(String titulo, String conteudo) {
        String template = """
            <!doctype html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width,initial-scale=1">
                <title>%s</title>
                <style>
                    :root { --primary: #2563eb; --bg: #f8fafc; --surface: #ffffff; --text: #1e293b; --border: #e2e8f0; }
                    body { font-family: 'Segoe UI', system-ui, sans-serif; background-color: var(--bg); color: var(--text); max-width: 800px; margin: 0 auto; padding: 2rem; line-height: 1.6; }
                    h1 { color: var(--primary); text-align: center; margin-bottom: 2rem; font-size: 2.5rem; }
                    h2 { border-bottom: 2px solid var(--border); padding-bottom: 0.5rem; margin-top: 2rem; color: #334155; }
                    .card { background: var(--surface); padding: 2rem; border-radius: 12px; box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); margin-top: 1rem; }
                    table { border-collapse: collapse; width: 100%%; background: var(--surface); border-radius: 8px; overflow: hidden; box-shadow: 0 1px 3px rgb(0 0 0 / 0.1); }
                    thead { background-color: var(--primary); color: white; }
                    th, td { padding: 1rem; border-bottom: 1px solid var(--border); text-align: left; }
                    tr:last-child td { border-bottom: none; }
                    tbody tr:hover { background-color: #f1f5f9; }
                    form { display: flex; flex-direction: column; gap: 1.2rem; }
                    label { font-weight: 600; font-size: 0.95rem; display: block; margin-bottom: 0.3rem; }
                    input { padding: 0.75rem; border: 1px solid var(--border); border-radius: 6px; font-size: 1rem; width: 100%%; box-sizing: border-box; transition: border-color 0.2s; }
                    input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px rgba(37,99,235,0.2); }
                    button, .btn { background-color: var(--primary); color: white; border: none; padding: 0.75rem 1.5rem; border-radius: 6px; font-size: 1rem; font-weight: bold; cursor: pointer; transition: background 0.2s; text-decoration: none; display: inline-block; text-align: center; }
                    button:hover, .btn:hover { background-color: #1d4ed8; }
                    .erro { background-color: #fef2f2; color: #991b1b; padding: 1rem; border-left: 4px solid #ef4444; border-radius: 6px; margin-bottom: 1rem; }
                    .recibo { background-color: #f0fdf4; color: #166534; padding: 1rem; border-left: 4px solid #22c55e; border-radius: 6px; font-family: monospace; white-space: pre-wrap; font-size: 1.1rem; }
                    .badge { padding: 0.3rem 0.8rem; border-radius: 999px; font-size: 0.85rem; font-weight: 600; }
                    .badge.disp { background: #dcfce7; color: #166534; }
                    .badge.emp { background: #fee2e2; color: #991b1b; }
                </style>
            </head>
            <body>
                <h1>%s</h1>
                %s
            </body>
            </html>
            """;
            
        return template.formatted(escapar(titulo), escapar(titulo), conteudo);
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
