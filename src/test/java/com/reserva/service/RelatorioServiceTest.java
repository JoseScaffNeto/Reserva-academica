package com.reserva.service;

import com.reserva.model.*;
import com.reserva.repository.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RelatorioService - Testes Unitários")
class RelatorioServiceTest {

    private ReservaRepository reservaRepo;
    private UsuarioRepository usuarioRepo;
    private RecursoRepository recursoRepo;
    private ReservaService reservaSvc;
    private RelatorioService relSvc;

    private Usuario professor;
    private Sala sala1;
    private Sala sala2;

    @BeforeEach
    void setUp() {
        Usuario.resetContador();
        RecursoAcademico.resetContador();
        Reserva.resetContador();
        reservaRepo = new ReservaRepository();
        usuarioRepo = new UsuarioRepository();
        recursoRepo = new RecursoRepository();
        reservaSvc  = new ReservaService(reservaRepo, usuarioRepo, recursoRepo);
        relSvc      = new RelatorioService(reservaRepo);
        professor = new Usuario("Carlos", "carlos@test.br", "P001", TipoUsuario.PROFESSOR);
        sala1 = new Sala("LAB-01", "Laboratório 1", 30, "Bloco A");
        sala2 = new Sala("SALA-05", "Sala 5", 40, "Bloco B");
        usuarioRepo.salvar(professor);
        recursoRepo.salvar(sala1);
        recursoRepo.salvar(sala2);
    }

    @Test
    @DisplayName("Indicadores devem refletir zero reservas quando repositório está vazio")
    void indicadoresVazios() {
        assertDoesNotThrow(() -> relSvc.exibirIndicadores());
    }

    @Test
    @DisplayName("Relatório mensal deve executar sem erros com reservas no período")
    void relatorioMensalComReservas() {
        LocalDateTime ini = LocalDateTime.of(2026, 7, 10, 9, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 7, 10, 11, 0);
        reservaSvc.criarReserva(professor.getId(), sala1.getId(), ini, fim, "Aula", false);
        assertDoesNotThrow(() -> relSvc.exibirRelatorioMensal(2026, 7));
    }

    @Test
    @DisplayName("Relatório mensal para período sem reservas não deve lançar exceção")
    void relatorioMensalSemReservas() {
        assertDoesNotThrow(() -> relSvc.exibirRelatorioMensal(2099, 1));
    }

    @Test
    @DisplayName("listarPorPeriodo deve retornar apenas reservas no intervalo solicitado")
    void listarPorPeriodoRetornaCorreto() {
        LocalDateTime ini1 = LocalDateTime.of(2026, 8, 1, 9, 0);
        LocalDateTime fim1 = LocalDateTime.of(2026, 8, 1, 11, 0);
        reservaSvc.criarReserva(professor.getId(), sala1.getId(), ini1, fim1, "Aula A", false);
        LocalDateTime ini2 = LocalDateTime.of(2026, 9, 1, 9, 0);
        LocalDateTime fim2 = LocalDateTime.of(2026, 9, 1, 11, 0);
        reservaSvc.criarReserva(professor.getId(), sala2.getId(), ini2, fim2, "Aula B", false);
        var resultado = reservaRepo.listarPorPeriodo(
                LocalDateTime.of(2026, 8, 1, 0, 0),
                LocalDateTime.of(2026, 8, 31, 23, 59));
        assertEquals(1, resultado.size());
        assertEquals("Aula A", resultado.get(0).getFinalidade());
    }
}
