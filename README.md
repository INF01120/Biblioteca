# Sistema de Biblioteca (MVC) — Projeto de Exemplo

Projeto de exemplo da disciplina **INF01120 – Desenvolvimento de Software (UFRGS)**.
Ele mostra a estrutura esperada para o trabalho: um programa Java em camadas (MVC),
construído com **Maven**, testado com **JUnit 5** e com cobertura medida pelo **JaCoCo**.

Este guia leva você do zero (nada instalado) até:

1. instalar as ferramentas necessárias;
2. baixar o projeto;
3. compilar e executar o programa;
4. executar os testes;
5. ver o relatório de cobertura do JaCoCo;
6. criar os seus próprios testes.

---

## Sumário

1. [O que o programa faz](#1-o-que-o-programa-faz)
2. [Estrutura do projeto](#2-estrutura-do-projeto)
3. [Pré-requisitos e instalação](#3-pré-requisitos-e-instalação)
4. [Baixando o projeto](#4-baixando-o-projeto)
5. [Compilando o projeto](#5-compilando-o-projeto)
6. [Executando o programa](#6-executando-o-programa) (terminal e [versão web/HTTP](#61-executando-a-versão-web-http))
7. [Executando os testes (JUnit)](#7-executando-os-testes-junit)
8. [Relatório de cobertura (JaCoCo)](#8-relatório-de-cobertura-jacoco)
9. [Usando uma IDE](#9-usando-uma-ide)
10. [Como escrever um teste](#10-como-escrever-um-teste)
11. [Referência rápida de comandos Maven](#11-referência-rápida-de-comandos-maven)
12. [Solução de problemas](#12-solução-de-problemas)

---

## 1. O que o programa faz

É um sistema de biblioteca de linha de comando. Ao executar, aparece um menu:

```
=== SISTEMA DE BIBLIOTECA (MVC) ===
1. Listar Acervo
2. Novo Empréstimo
0. Sair
```

- **Listar Acervo**: mostra os exemplares e se estão disponíveis ou emprestados.
- **Novo Empréstimo**: pede a matrícula do leitor e o ISBN do exemplar, registra o
  empréstimo e imprime um recibo com a data de devolução prevista.

O prazo de devolução depende **do tipo de leitor** e **do tipo de material**
(polimorfismo em `Usuario.calcularPrazoDevolucao`):

| Tipo de material | Prazo base |
|------------------|-----------:|
| `LIVRO_DIDATICO` | 7 dias     |
| `LITERATURA`     | 14 dias    |
| `PERIODICO`      | 3 dias     |

| Leitor                       | Prazo                          |
|------------------------------|--------------------------------|
| `Aluno`                      | prazo base do material         |
| `Professor` `TITULAR`        | prazo base + 15 dias           |
| `Professor` `ADJUNTO`        | prazo base + 10 dias           |
| `Professor` `VISITANTE`      | prazo base + 5 dias            |

### Dados de exemplo já cadastrados

| Leitores          | Matrícula |
|-------------------|-----------|
| João da Silva (Aluno)                 | `111` |
| Maria Oliveira (Professora Titular)   | `222` |

| Exemplares              | ISBN     | Tipo             |
|-------------------------|----------|------------------|
| Java Como Programar     | `978-01` | `LIVRO_DIDATICO` |
| O Senhor dos Anéis      | `978-02` | `LITERATURA`     |
| Revista da SBC          | `978-03` | `PERIODICO`      |

Os dados ficam apenas em memória: ao fechar o programa, tudo volta ao estado inicial.

---

## 2. Estrutura do projeto

```
Biblioteca/
├── pom.xml                         # configuração do Maven (dependências e plugins)
├── README.md                       # este arquivo
├── src/
│   ├── main/java/                  # código de produção
│   │   ├── model/                  #   regras de negócio e entidades
│   │   │   ├── Usuario.java            (classe abstrata)
│   │   │   ├── Aluno.java
│   │   │   ├── Professor.java
│   │   │   ├── Exemplar.java
│   │   │   ├── Emprestimo.java
│   │   │   ├── BibliotecaService.java
│   │   │   ├── TipoMaterial.java       (enum)
│   │   │   └── TipoVinculo.java        (enum)
│   │   ├── controller/
│   │   │   └── ControleBiblioteca.java #   intermedia View ↔ Model
│   │   └── view/
│   │       ├── SistemaBibliotecaCLI.java  # view 1: menu no terminal (main)
│   │       └── SistemaBibliotecaHTTP.java # view 2: páginas web via servidor do JDK (main)
│   └── test/java/                  # testes automatizados
│       ├── SmokeTest.java              (teste mínimo: confirma que o JUnit funciona)
│       └── model/
│           └── ProfessorTest.java      (exemplo de teste de uma classe do model)
└── target/                         # GERADO pelo Maven (não edite, não envie ao Git)
    ├── classes/                    #   código compilado
    ├── surefire-reports/           #   resultado dos testes
    ├── jacoco.exec                 #   dados brutos da cobertura
    └── site/jacoco/index.html      #   relatório de cobertura (abra no navegador)
```

Pontos importantes:

- **`src/main/java`** contém o programa; **`src/test/java`** contém os testes.
  Esta separação é o padrão do Maven — não mude os nomes dessas pastas.
- Os testes usam o **mesmo pacote** da classe testada (`model`, por exemplo),
  mas ficam em outra pasta.
- A pasta **`target/`** é recriada a cada build. Pode apagá-la a qualquer momento
  (`mvn clean`).

---

## 3. Pré-requisitos e instalação

Você precisa de três coisas:

| Ferramenta | Versão mínima | Para quê                      |
|------------|---------------|-------------------------------|
| **JDK**    | 25+           | compilar e executar Java      |
| **Maven**  | 3.9+          | build, testes e relatório     |
| **Git**    | qualquer      | baixar o projeto (opcional)   |

> **JDK, não JRE.** Você precisa do *Development Kit* (que inclui o compilador `javac`).
> Qualquer distribuição serve (Temurin, OpenJDK, Oracle...). 

JUnit e JaCoCo **não precisam ser instalados manualmente**: o Maven os baixa
sozinho na primeira execução, a partir do `pom.xml` (precisa de internet nessa primeira vez).

### 3.1 macOS

Usando o [Homebrew](https://brew.sh):

```bash
brew install openjdk@25 maven git
```

Depois de instalar o JDK, o Homebrew mostra uma instrução para que o sistema o encontre.
Normalmente é:

```bash
sudo ln -sfn "$(brew --prefix openjdk@21)/libexec/openjdk.jdk" /Library/Java/JavaVirtualMachines/openjdk-25.jdk
```

### 3.2 Windows

Opção A — **winget** (PowerShell, Windows 10/11):

```powershell
winget install EclipseAdoptium.Temurin.21.JDK
winget install Git.Git
```

O Maven não tem pacote oficial no winget. Instale-o manualmente:

1. Baixe o arquivo `apache-maven-3.9.x-bin.zip` em <https://maven.apache.org/download.cgi>.
2. Extraia em, por exemplo, `C:\Program Files\Maven\apache-maven-3.9.x`.
3. Adicione a pasta `bin` ao **PATH**:
   *Iniciar → "Editar as variáveis de ambiente do sistema" → Variáveis de Ambiente →
   `Path` → Novo →* `C:\Program Files\Maven\apache-maven-3.9.x\bin`.
4. Crie a variável `JAVA_HOME` apontando para a pasta do JDK
   (ex.: `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot`).
5. **Feche e reabra** o terminal.

Opção B — instaladores gráficos: JDK em <https://adoptium.net>, Git em <https://git-scm.com>.

Alternativa mais simples: se você usar o **IntelliJ IDEA**, ele já traz um Maven embutido
(veja a [seção 9](#9-usando-uma-ide)).

### 3.3 Linux (Debian/Ubuntu)

```bash
sudo apt update
sudo apt install openjdk-21-jdk maven git
```

Em Fedora: `sudo dnf install java-21-openjdk-devel maven git`.

### 3.4 Verificando a instalação

Abra um **novo** terminal e execute:

```bash
java -version
javac -version
mvn -version
git --version
```

Todos devem imprimir uma versão. Exemplo esperado para o Maven:

```
Apache Maven 3.9.x
Maven home: ...
Java version: 25.x.x, vendor: ...
```

Se algum comando der "não encontrado", veja a [seção 12](#12-solução-de-problemas).

---

## 4. Baixando o projeto

### Opção A — com Git

```bash
git clone <URL-DO-REPOSITORIO>
cd Biblioteca
```

### Opção B — arquivo ZIP

Baixe o `.zip` fornecido pelo professor, extraia e abra um terminal dentro da
pasta `Biblioteca` (a que contém o arquivo `pom.xml`).

> **Todos os comandos Maven abaixo devem ser executados na pasta que contém o `pom.xml`.**
> Se aparecer `there is no POM in this directory`, você está na pasta errada.

> **Dica:** evite caminhos com acentos ou espaços muito longos quando possível;
> em alguns ambientes Windows isso causa problemas.
> Se o seu caminho tem espaços, coloque-o entre aspas ao usar `cd "meu caminho"`.

---

## 5. Compilando o projeto

```bash
mvn compile
```

Na primeira vez o Maven baixa vários arquivos (plugins e bibliotecas) — pode levar
alguns minutos. Nas próximas execuções é rápido.

Resultado esperado no final:

```
[INFO] BUILD SUCCESS
```

Os arquivos `.class` ficam em `target/classes/`.

---

## 6. Executando o programa

Primeiro compile (seção 5) e depois execute a classe principal
`view.SistemaBibliotecaCLI`:

```bash
java -cp target/classes view.SistemaBibliotecaCLI
```

> No Windows (PowerShell ou CMD) o comando é o mesmo.

### Exemplo de uso

1. Digite `1` e Enter para listar o acervo.
2. Digite `2` e Enter para fazer um empréstimo.
3. Informe a matrícula `111` e Enter.
4. Informe o ISBN `978-01` e Enter.
5. O recibo é impresso:

```
--- RECIBO DE EMPRÉSTIMO ---
Leitor: João da Silva (Matrícula: 111)
Exemplar: Java Como Programar (ISBN: 978-01)
Data Retirada: 04/10/2026
Data Devolução Prevista: 11/10/2026
----------------------------
```

6. Digite `0` para sair.

Experimente também os casos de erro:

- tentar emprestar `978-01` de novo → `Este exemplar já está emprestado!`
- usar a matrícula `999` → `Usuário não encontrado!`
- usar o ISBN `000` → `Exemplar não encontrado!`
- digitar letras no menu → `Entrada inválida! Digite um número.`

Compare o recibo da matrícula `111` (aluno) com o da `222` (professora titular)
para o mesmo tipo de material: o prazo da professora é maior.

### 6.1 Executando a versão web (HTTP)

O projeto tem uma **segunda view**, `view.SistemaBibliotecaHTTP`, que oferece as mesmas
funções pelo navegador. Ela usa o servidor HTTP que já vem no JDK
(`com.sun.net.httpserver`), então **não exige nenhuma instalação ou dependência extra**.
O Model e o Controller são exatamente os mesmos da versão de terminal — só a View muda,
que é justamente a vantagem do padrão MVC.

Compile (seção 5) e inicie o servidor:

```bash
java -cp target/classes view.SistemaBibliotecaHTTP
```

Saída esperada:

```
Servidor em http://localhost:8080/  (Ctrl+C para encerrar)
```

Agora abra <http://localhost:8080/> no navegador:

1. A página mostra a tabela do **acervo** com o status de cada exemplar.
2. No formulário **Novo Empréstimo**, informe a matrícula (`111` ou `222`) e o ISBN
   (`978-01`, `978-02` ou `978-03`) e clique em *Emprestar*.
3. O recibo é exibido na tela. Use o link *Voltar* e veja o exemplar como **Emprestado**.
4. Tente emprestar o mesmo exemplar de novo, ou usar uma matrícula inexistente: a página
   mostra a mesma mensagem de erro da versão de terminal.

Para encerrar o servidor, volte ao terminal e pressione **Ctrl+C**.

**Usar outra porta** (por exemplo, se a 8080 estiver ocupada):

```bash
java -cp target/classes view.SistemaBibliotecaHTTP 9000
```

e acesse <http://localhost:9000/>.

**Também dá para testar pelo terminal**, sem navegador, usando o `curl`:

```bash
# ver a página do acervo
curl http://localhost:8080/

# fazer um empréstimo (formulário via POST)
curl -d "matricula=222&isbn=978-02" http://localhost:8080/emprestimo
```

Observações:

- Os dados ficam **em memória no servidor**: ao encerrá-lo (Ctrl+C), tudo volta ao estado inicial.
- O servidor só atende o seu computador no endereço `localhost`; ele é apenas um exemplo
  didático, sem autenticação ou persistência.
- Se aparecer `Address already in use`, outra aplicação está usando a porta — escolha outra
  (veja acima) ou encerre a que está rodando.
- Na IDE, basta clicar no ▶ ao lado do `main` de `SistemaBibliotecaHTTP.java`
  (para mudar a porta, informe-a em *Program arguments* na configuração de execução).

---

## 7. Executando os testes (JUnit)

```bash
mvn test
```

O Maven compila o código, compila os testes, executa todos e mostra o resumo:

```
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

> O número exato muda conforme você adiciona testes.

### Onde ver os resultados em detalhe

- No terminal (resumo por classe de teste).
- Em `target/surefire-reports/` — um `.txt` e um `.xml` por classe de teste.

### Executando só uma classe de teste ou um único teste

```bash
# só a classe ProfessorTest
mvn test -Dtest=ProfessorTest

# só um método de teste
mvn test -Dtest=ProfessorTest#guardaNomeEMatricula
```

### Quando um teste falha

O Maven mostra qual teste falhou, o valor esperado e o valor obtido:

```
[ERROR] ProfessorTest.prazoEhPrazoBaseMaisDiasExtrasDoVinculo
        expected: <22> but was: <20>
[INFO] BUILD FAILURE
```

Leia a mensagem: ela diz qual método falhou e por quê. Corrija o código (ou o teste, se o
erro estava nele) e rode novamente.

---

## 8. Relatório de cobertura (JaCoCo)

**Cobertura de código** mede qual porcentagem do seu código foi executada pelos testes.
O JaCoCo está configurado no `pom.xml` para gerar o relatório automaticamente
sempre que você roda `mvn test`.

### Gerando o relatório

```bash
mvn clean test
```

(O `clean` apaga o `target/` antigo, garantindo um relatório novo.)

### Abrindo o relatório

O relatório é um site HTML local:

```
target/site/jacoco/index.html
```

Abra pelo gerenciador de arquivos (duplo clique) ou pelo terminal:

```bash
# macOS
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html

# Windows (PowerShell)
start target/site/jacoco/index.html
```

### Como ler o relatório

A página inicial lista os pacotes (`model`, `controller`, `view`) com colunas como:

| Coluna                | Significado                                                        |
|-----------------------|--------------------------------------------------------------------|
| **Missed Instructions / Cov.** | % de instruções de bytecode executadas pelos testes       |
| **Missed Branches / Cov.**     | % de ramos (`if`, `switch`, `?:`) exercitados nos dois sentidos |
| **Missed / Lines**    | linhas de código não executadas / total                            |
| **Missed / Methods**  | métodos não executados / total                                     |
| **Missed / Classes**  | classes não executadas / total                                     |

Clique em um pacote → uma classe → um método para ver o **código-fonte colorido**:

- 🟩 **verde**: linha totalmente coberta;
- 🟨 **amarelo**: ramo parcialmente coberto (ex.: o `if` só foi testado com resultado verdadeiro);
- 🟥 **vermelho**: linha nunca executada pelos testes.

**Use o vermelho e o amarelo como lista de tarefas**: cada um indica um teste que ainda falta escrever.

### Outros formatos gerados

Na mesma pasta `target/site/jacoco/` também há `jacoco.csv` (planilha) e `jacoco.xml`
(para ferramentas de integração contínua).

### Observações

- Ao rodar apenas `mvn compile`, **nenhum relatório é gerado** — ele depende da execução dos testes.
- Com o projeto recém-baixado (só um teste de exemplo), a cobertura é baixa de propósito.
  Parte do seu trabalho é aumentá-la escrevendo bons testes.
- 100% de cobertura **não** garante ausência de bugs: um teste sem `assert` também
  "cobre" código. O que importa é testar **comportamento**, não apenas executar linhas.

---

## 9. Usando uma IDE

Você não é obrigado a usar o terminal. As IDEs abaixo entendem projetos Maven
diretamente a partir do `pom.xml`.

### IntelliJ IDEA (Community ou Ultimate)

1. *File → Open…* e selecione a **pasta `Biblioteca`** (ou o `pom.xml`).
   Se perguntar, escolha *Open as Project* e *Trust Project*.
2. Aguarde o IntelliJ importar o projeto Maven (barra de progresso no canto inferior).
3. Confirme o JDK em *File → Project Structure → Project → SDK* (17 ou superior).
4. **Executar o programa:** abra `SistemaBibliotecaCLI.java` e clique no ▶ ao lado do `main`.
5. **Executar os testes:** clique com o botão direito em `src/test/java` →
   *Run 'All Tests'*, ou no ▶ ao lado de um teste.
6. **Cobertura:** botão direito em `src/test/java` → *Run 'All Tests' with Coverage*.
   O IntelliJ mostra a cobertura direto no código, ao lado dos números de linha.
7. **Comandos Maven:** painel *Maven* (lado direito) → *Lifecycle* → duplo clique em `test`.

### Visual Studio Code

1. Instale o **Extension Pack for Java** (da Microsoft) na aba de extensões.
2. *File → Open Folder…* e selecione a pasta `Biblioteca`.
3. Aguarde a importação do projeto (ícone de carregamento na barra inferior).
4. **Executar:** botão *Run* acima do método `main`.
5. **Testes:** aba *Testing* (ícone de béquer) → ▶ para rodar todos ou um por um.
6. Para o relatório JaCoCo, use o terminal integrado: `mvn clean test`.

### Eclipse

1. *File → Import… → Maven → Existing Maven Projects* e selecione a pasta `Biblioteca`.
2. Executar: botão direito em `SistemaBibliotecaCLI.java` → *Run As → Java Application*.
3. Testes: botão direito no projeto → *Run As → Maven test*.

> Seja qual for a IDE, o resultado "oficial" é o do Maven (`mvn clean test`):
> é assim que o professor poderá avaliar o seu trabalho.

---

## 10. Como escrever um teste

### Onde colocar

Um arquivo de teste por classe, com o mesmo pacote e o sufixo `Test`:

| Classe testada                          | Arquivo de teste                              |
|-----------------------------------------|-----------------------------------------------|
| `src/main/java/model/Professor.java`    | `src/test/java/model/ProfessorTest.java`      |
| `src/main/java/model/Exemplar.java`     | `src/test/java/model/ExemplarTest.java`       |

O sufixo `Test` é importante: é por ele que o Maven descobre quais classes executar.

### Anatomia de um teste (veja `ProfessorTest.java`)

```java
package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ProfessorTest {

    @Test                                              // marca o método como teste
    void guardaNomeEMatricula() {
        Professor p = new Professor("Ana", "123", TipoVinculo.TITULAR);   // Arrange (preparar)

        assertEquals("Ana", p.getNome());              // Assert (verificar)
        assertEquals("123", p.getMatricula());
    }
}
```

Padrão recomendado: **Arrange – Act – Assert** (preparar, agir, verificar).
Dê ao método um nome que descreva o comportamento esperado
(`prazoEhPrazoBaseMaisDiasExtrasDoVinculo`, e não `teste1`).

### Asserções mais usadas

```java
assertEquals(esperado, obtido);
assertTrue(condicao);
assertFalse(condicao);
assertNull(valor);
assertNotNull(valor);
assertThrows(Exception.class, () -> servico.registrarEmprestimo("999", "978-01"));
```

### Testes parametrizados

Quando o mesmo teste vale para vários valores, use `@ParameterizedTest` com `@CsvSource`
(exemplo completo em `ProfessorTest`): cada linha da tabela vira uma execução separada.

### Exemplo: testando uma exceção

```java
@Test
void emprestimoComMatriculaInexistenteLancaExcecao() {
    BibliotecaService service = new BibliotecaService();

    Exception e = assertThrows(Exception.class,
            () -> service.registrarEmprestimo("999", "978-01"));

    assertEquals("Usuário não encontrado!", e.getMessage());
}
```

### Ciclo de trabalho sugerido

1. Escreva (ou altere) um teste.
2. `mvn test` — veja passar ou falhar.
3. `mvn clean test` e abra o relatório JaCoCo.
4. Procure as linhas **vermelhas/amarelas** e escreva testes para elas.
5. Repita.

---

## 11. Referência rápida de comandos Maven

| Comando                       | O que faz                                                              |
|-------------------------------|------------------------------------------------------------------------|
| `mvn compile`                 | compila o código de `src/main/java`                                    |
| `mvn test`                    | compila, executa os testes e gera o relatório JaCoCo                   |
| `mvn clean`                   | apaga a pasta `target/`                                                |
| `mvn clean test`              | build limpo + testes + relatório (**o mais usado**)                    |
| `mvn test -Dtest=NomeDoTeste` | executa apenas uma classe de teste                                     |
| `mvn package`                 | gera `target/biblioteca-1.0-SNAPSHOT.jar`                              |
| `mvn -o test`                 | executa em modo offline (depois que tudo já foi baixado)               |
| `mvn dependency:tree`         | lista as dependências do projeto                                       |

---

## 12. Solução de problemas

### `mvn: command not found` / `'mvn' não é reconhecido`

O Maven não está instalado ou não está no `PATH`. Refaça a [seção 3](#3-pré-requisitos-e-instalação)
e **abra um novo terminal** depois de instalar.

### `javac: command not found` ou `No compiler is provided in this environment`

Você tem apenas o JRE, ou o `JAVA_HOME` aponta para ele. Instale um **JDK**
e confira com `javac -version`.

### `The JAVA_HOME environment variable is not defined correctly`

Defina `JAVA_HOME` apontando para a pasta do JDK.

- macOS/Linux (adicione ao `~/.zshrc` ou `~/.bashrc`):
  ```bash
  export JAVA_HOME=$(/usr/libexec/java_home)   # macOS
  # ou, no Linux:
  export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
  ```
- Windows: *Variáveis de Ambiente → Nova variável de sistema* `JAVA_HOME`,
  valor `C:\Program Files\Eclipse Adoptium\jdk-21...`

### `invalid target release: 17` / `release version 17 not supported`

Seu JDK é anterior ao 17. Instale o JDK 17 ou superior.
(Se a sua disciplina exigir outra versão, altere `maven.compiler.release` no `pom.xml`.)

### `there is no POM in this directory`

Você está na pasta errada. Use `cd` até a pasta onde está o `pom.xml`
(confira com `ls pom.xml` no macOS/Linux ou `dir pom.xml` no Windows).

### Erros de download (`Could not transfer artifact`, `Connection timed out`)

- Verifique sua conexão com a internet (a primeira execução precisa baixar as dependências).
- Em rede da universidade com proxy, configure o proxy em `~/.m2/settings.xml`.
- Tente novamente; se o download ficou corrompido, apague `~/.m2/repository` e repita.

### `Could not find or load main class view.SistemaBibliotecaCLI`

- Execute `mvn compile` antes.
- Execute o `java -cp target/classes ...` **a partir da pasta do `pom.xml`**.
- Para a versão web, o nome é `view.SistemaBibliotecaHTTP`.
- Escreva o nome exatamente como está, incluindo maiúsculas: `view.SistemaBibliotecaCLI`.

### Caracteres estranhos (`EmprÃ©stimo`) no terminal

É um problema de codificação do terminal.

- Windows (CMD/PowerShell): execute `chcp 65001` antes de rodar o programa, ou use
  `java -Dfile.encoding=UTF-8 -cp target/classes view.SistemaBibliotecaCLI`.
- O build já usa UTF-8 (`project.build.sourceEncoding` no `pom.xml`).

### O relatório JaCoCo não aparece

- Rode `mvn clean test` (não apenas `compile`).
- Confira se o arquivo existe: `target/site/jacoco/index.html`.
- Se a pasta `target/site/jacoco` não existe, veja se algum teste **falhou** ou deu erro
  de compilação — corrija e rode de novo.

### Os testes não são executados (`Tests run: 0`)

- O arquivo precisa estar em `src/test/java`.
- O nome da classe deve terminar em `Test` (ex.: `ExemplarTest`).
- Os métodos precisam da anotação `@Test` importada de `org.junit.jupiter.api.Test`
  (**JUnit 5**, e não `org.junit.Test`, que é do JUnit 4).

### A IDE mostra erros em `import org.junit...`

A IDE ainda não importou as dependências do Maven. Recarregue o projeto:
IntelliJ → painel *Maven* → ícone de recarregar; VS Code → *Java: Clean Java Language Server Workspace*;
Eclipse → botão direito no projeto → *Maven → Update Project*.

---

## Tecnologias e versões

| Componente                | Versão |
|---------------------------|--------|
| Java (release alvo)       | 17     |
| Maven                     | 3.9+   |
| JUnit Jupiter (JUnit 5)   | 5.11.4 |
| Maven Surefire Plugin     | 3.5.2  |
| JaCoCo Maven Plugin       | 0.8.13 |

As versões são definidas no [pom.xml](pom.xml).
