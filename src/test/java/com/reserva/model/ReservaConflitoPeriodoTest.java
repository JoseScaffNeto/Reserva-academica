package com.reserva.model;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reserva - Testes de Conflito de Horário")
class ReservaConflitoPeriodoTest {

    private Usuario usuario;
    private Sala sala;

    private final LocalDateTime DIA1_8  = LocalDateTime.of(2026, 7, 1, 8, 0);
    private final LocalDateTime DIA1_10 = LocalDateTime.of(2026, 7, 1, 10, 0);
    private final LocalDateTime DIA1_9  = LocalDateTime.of(2026, 7, 1, 9, 0);
    private final LocalDateTime DIA1_11 = LocalDateTime.of(2026, 7, 1, 11, 0);
    private final LocalDateTime DIA1_12 = LocalDateTime.of(2026, 7, 1, 12, 0);

    @BeforeEach
    void setUp() {
        Usuario.resetContador();
        RecursoAcademico.resetContador();
        Reserva.resetContador();
        usuario = new Usuario("Prof", "prof@test.br", "M001", TipoUsuario.PROFESSOR);
        sala    = new Sala("SALA-01", "Sala", 30, "Bloco A");
    }

    @Test
    @DisplayName("Reservas no mesmo horário exato conflitam")
    void mesmoHorarioConflita() {
        Reserva r = new Reserva(sala, usuario, DIA1_8, DIA1_10, "Aula", false);
        assertTrue(r.conflitaCom(DIA1_8, DIA1_10));
    }

    @Test
    @DisplayName("Reservas com sobreposição parcial conflitam")
    void sobreposicaoParcialConflita() {
        Reserva r = new Reserva(sala, usuario, DIA1_8, DIA1_10, "Aula", false);
        assertTrue(r.conflitaCom(DIA1_9, DIA1_11));
    }

    @Test
    @DisplayName("Reservas com uma contida dentro da outra conflitam")
    void reservaContidaConflita() {
        Reserva r = new Reserva(sala, usuario, DIA1_8, DIA1_12, "Aula", false);
        assertTrue(r.conflitaCom(DIA1_9, DIA1_10));
    }

    @Test
    @DisplayName("Reservas adjacentes não conflitam (fim == início da próxima)")
    void reservasAdjacentesNaoConflitam() {
        Reserva r = new Reserva(sala, usuario, DIA1_8, DIA1_10, "Aula", false);
        assertFalse(r.conflitaCom(DIA1_10, DIA1_12));
    }

    @Test
    @DisplayName("Reservas em horários completamente distintos nɼo conflitam")
    void horariosDistintosNaoConflitam() {
        Reserva r = new Reserva(sala, usuario, DIA1_8, DIA1_10, "Aula", false);
        assertFalse(r.conflitaCom(DIA1_11, DIA1_12));
    }

    @Test
    @DisplayName("Reserva deve detectar atraso na devolução corretamente")
    void detectaAtrasoNaDevolucao() {
        Reserva r = new Reserva(sala, usuario, DIA1_8, DIA1_10, "Aula", false);
        r.setStatus(StatusReserva.APROVADA);
        r.setDevolvidaEm(DIA1_11);
        assertTrue(r.estaAtrasada());
    }

    @Test
    @DisplayName("Devolução antes do fim não é atraso")
    void devolucaoDentroDoHorarioNaoEhAtraso() {
        Reserva r = new Reserva(sala, usuario, DIA1_8, DIA1_10, "Aula", false);
        r.setDevolvidaEm(DIA1_9;
        assertFalse(r.estaAtrasada());
    }
}
