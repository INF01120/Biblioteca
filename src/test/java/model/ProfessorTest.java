package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProfessorTest {

    @Test
    void guardaNomeEMatricula() {
        Professor p = new Professor("Ana", "123", TipoVinculo.TITULAR);

        assertEquals("Ana", p.getNome());
        assertEquals("123", p.getMatricula());
    }

    @ParameterizedTest
    @CsvSource({
        "TITULAR,   LIVRO_DIDATICO, 22",
        "TITULAR,   LITERATURA,     29",
        "TITULAR,   PERIODICO,      18",
        "ADJUNTO,   LIVRO_DIDATICO, 17",
        "ADJUNTO,   LITERATURA,     24",
        "ADJUNTO,   PERIODICO,      13",
        "VISITANTE, LIVRO_DIDATICO, 12",
        "VISITANTE, LITERATURA,     19",
        "VISITANTE, PERIODICO,      8"
    })
    void prazoEhPrazoBaseMaisDiasExtrasDoVinculo(TipoVinculo vinculo, TipoMaterial material, int esperado) {
        Professor p = new Professor("Ana", "123", vinculo);

        assertEquals(esperado, p.calcularPrazoDevolucao(material));
    }

    @Test
    void professorTemPrazoMaiorQueAlunoParaMesmoMaterial() {
        Usuario professor = new Professor("Ana", "123", TipoVinculo.VISITANTE);
        Usuario aluno = new Aluno("Beto", "456");

        for (TipoMaterial m : TipoMaterial.values()) {
            assertEquals(aluno.calcularPrazoDevolucao(m) + 5, professor.calcularPrazoDevolucao(m));
        }
    }
}
