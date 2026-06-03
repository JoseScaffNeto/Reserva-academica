package com.reserva.service;

import com.reserva.exception.*;
import com.reserva.model.*;
import com.reserva.repository.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReservaService - Testes Unitários")
class ReservaServiceTest {

    private ReservaRepository reservaRepo;
    private UsuarioRepository usuarioRepo;
    private RecursoRepository recursoRepo;
    private ReservaService service;

    private Usuario professor;
    private Usuario coordenador;
    private Usuario aluno;
    private Sala sala;
    private Equipamento equipFragil;

    private final LocalDateTime AMANHA_9  = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
    private final LocalDateTime AMANHA_11 = AMANHA_9.withHour(11);
    private final LocalDateTime AMANHA_14 = AMANHA_9.withHour(14);
    private final LocalDateTime AMANHA_16 = AMANHA_9.withHour(16);

    @BeforeEach
    void setUp() {
        Usuario.resetContador();
        RecursoAcademico.resetContador();
        Reserva.resetContador();

        reservaRepo = new ReservaRepository();
        usuarioRepo = new UsuarioRepository();
        recursoRepo = new RecursoRepository();
        service     = new ReservaService(reservaRepo, usuarioRepo, recursoRepo);

        professor    = new Usuario("Carlos Professor", "carlos@test.br", "PROF001", TipoUsuario.PROFESSOR);
        coordenador  = new Usuario("Ana Coordenadora", "ana@test.br",    "COORD001", TipoUsuario.COORDENADOR);
        aluno        = new Usuario("Diego Aluno",      "diego@test.br",  "ALU001",  TipoUsuario.ALUNO_AUTORIZADO);
        sala         = new Sala("LAB-01", "Laboratório", 30, "Bloco A");
        equipFragil  = new Equipamento("KIT-01", "Kit Robótica", TipoRecurso.EQUIPAMENTO_FRAGIL, "Depósito", true);

        usuarioRepo.salvar(professor);
        usuarioRepo.salvar(coordenador);
        usuarioRepo.salvar(aluno);
        recursoRepo.salvar(sala);
        recursoRepo.salvar(equipFragil);
    }

    @Test
    @DisplayName("Deve criar reserva com status APROVADA para reserva não-prioritária")
    void deveCriarReservaNaoPrioritariaAprovada() {
        Reserva r = service.criarReserva(professor.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Aula", false);
        assertNotNull(r);
        assertEquals(StatusReserva.APROVADA, r.getStatus());
    }

    @Test
    @DisplayName("Reserva prioritária de coordenador deve ser APROVADA automaticamente")
    void coordenadorAprovaPrioritariaAutomaticamente() {
        Reserva r = service.criarReserva(coordenador.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Reunião", true);
        assertEquals(StatusReserva.APROVADA, r.getStatus());
    }

    @Test
    @DisplayName("Reserva prioritária de professor deve ficar PENDENTE")
    void professorPrioritariaFicaPendente() {
        Reserva r = service.criarReserva(professor.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Aula", true);
        assertEquals(StatusReserva.PENDENTE, r.getStatus());
    }

    @Test
    @DisplayName("Deve lançar ConflitoPeriodoException quando houver sobreposição de horário")
    void deveLancarConflitoPeriodo() {
        service.criarReserva(professor.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Aula 1", false);
        assertThrows(ConflitoPeriodoException.class, () ->
                service.criarReserva(coordenador.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Aula 2", false));
    }

    @Test
    @DisplayName("Deve permitir reserva no mesmo recurso em horários diferentes")
    void devePermitirReservaEmHorarioDiferente() {
        service.criarReserva(professor.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Aula manhã", false);
        assertDoesNotThrow(() ->
                service.criarReserva(coordenador.getId(), sala.getId(), AMANHA_14, AMANHA_16, "Aula tarde", false));
    }

    @Test
    @DisplayName("Deve lançar UsuarioBloqueadoException para usuário bloqueado")
    void deveLancarExcecaoParaUsuarioBloqueado() {
        aluno.bloquear(7);
        usuarioRepo.salvar(aluno);
        assertThrows(UsuarioBloqueadoException.class, () ->
                service.criarReserva(aluno.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Apresentação", false));
    }

    @Test
    @DisplayName("Deve lançar LimiteReservasException ao atingir 10 reservas ativas")
    void deveLancarLimiteReservas() {
        for (int i = 0; i < 10; i++) {
            Sala s = new Sala("SALA-" + (10 + i), "Sala " + i, 30, "Bloco");
            recursoRepo.salvar(s);
            LocalDateTime ini = AMANHA_9.plusDays(i);
            service.criarReserva(professor.getId(), s.getId(), ini, ini.plusHours(1), "Aula", false);
        }
        Sala salaExtra = new Sala("SALA-EXTRA", "Extra", 30, "Bloco");
        recursoRepo.salvar(salaExtra);
        assertThrows(LimiteReservasException.class, () ->
                service.criarReserva(professor.getId(), salaExtra.getId(),
                        AMANHA_9.plusDays(20), AMANHA_9.plusDays(20).plusHours(1), "Extra", false));
    }

    @Test
    @DisplayName("Técnico deve conseguir aprovar reserva pendente")
    void tecnicoDeveAprovarReserva() {
        Usuario tecnico = new Usuario("Bruno", "bruno@test.br", "TEC001", TipoUsuario.TECNICO);
        usuarioRepo.salvar(tecnico);
        Reserva r = service.criarReserva(professor.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Aula", true);
        service.aprovar(r.getId(), tecnico.getId());
        assertEquals(StatusReserva.APROVADA, r.getStatus());
    }

    @Test
    @DisplayName("Aluno não pode aprovar reservas")
    void alunoNaoPodeAprovar() {
        Reserva r = service.criarReserva(professor.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Aula", true);
        assertThrows(ReservaException.class, () -> service.aprovar(r.getId(), aluno.getId()));
    }

    @Test
    @DisplayName("Deve cancelar reserva com status APROVADA")
    void deveCancelarReserva() {
        Reserva r = service.criarReserva(professor.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Aula", false);
        service.cancelar(r.getId());
        assertEquals(StatusReserva.CANCELADA, r.getStatus());
    }

    @Test
    @DisplayName("Devolução sem avaria e sem atraso deve concluir sem penalidade")
    void devolucaoLimpaSemPenalidade() {
        LocalDateTime ini = LocalDateTime.now().minusHours(2);
        LocalDateTime fim = LocalDateTime.now().plusHours(2);
        Sala salaLocal = new Sala("SALA-DEV", "Sala Dev", 30, "Bloco");
        recursoRepo.salvar(salaLocal);
        Reserva r = service.criarReserva(professor.getId(), salaLocal.getId(), ini, fim, "Aula", false);
        String pen = service.registrarDevolucao(r.getId(), false, "Tudo ok");
        assertEquals("SEM_PENALIDADE", pen);
        assertEquals(StatusReserva.CONCLUIDA, r.getStatus());
    }

    @Test
    @DisplayName("Avaria em equipamento frágil deve bloquear usuário por 30 dias")
    void avariaEquipamentoFragilBloqueiaUsuario() {
        LocalDateTime ini = LocalDateTime.now().minusHours(2);
        LocalDateTime fim = LocalDateTime.now().plusHours(2);
        Reserva r = service.criarReserva(professor.getId(), equipFragil.getId(), ini, fim, "Lab", false);
        String pen = service.registrarDevolucao(r.getId(), true, "Quebrou");
        assertTrue(pen.contains("BLOQUEIO_30_DIAS"));
        assertTrue(professor.isBloqueado());
    }

    @Test
    @DisplayName("Devolução de reserva não-aprovada deve lançar exceção")
    void devolucaoDeReservaNaoAprovadaLancaExcecao() {
        Reserva r = service.criarReserva(professor.getId(), sala.getId(), AMANHA_9, AMANHA_11, "Aula", true);
        assertEquals(StatusReserva.PENDENTE, r.getStatus());
        assertThrows(ReservaException.class, () -> service.registrarDevolucao(r.getId(), false, "ok"));
    }
}
