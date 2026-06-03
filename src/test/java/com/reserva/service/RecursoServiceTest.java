package com.reserva.service;

import com.reserva.exception.ReservaException;
import com.reserva.model.*;
import com.reserva.repository.RecursoRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RecursoService - Testes Unitários")
class RecursoServiceTest {

    private RecursoRepository repo;
    private RecursoService service;

    @BeforeEach
    void setUp() {
        RecursoAcademico.resetContador();
        repo    = new RecursoRepository();
        service = new RecursoService(repo);
    }

    @Test
    @DisplayName("Deve cadastrar sala com dados válidos")
    void deveCadastrarSala() {
        Sala sala = new Sala("LAB-01", "Laboratório", 30, "Bloco A");
        RecursoAcademico salvo = service.cadastrar(sala);
        assertNotNull(salvo);
        assertEquals("LAB-01", salvo.getCodigo());
        assertTrue(salvo.isDisponivel());
    }

    @Test
    @DisplayName("Não deve cadastrar recurso com código duplicado")
    void naoDeveCadastrarCodigoDuplicado() {
        service.cadastrar(new Sala("LAB-01", "Lab 1", 30, "Bloco A"));
        assertThrows(ReservaException.class, () ->
                service.cadastrar(new Sala("LAB-01", "Lab Duplicado", 20, "Bloco B")));
    }

    @Test
    @DisplayName("Deve buscar recurso por ID existente")
    void deveBuscarPorId() {
        RecursoAcademico r = service.cadastrar(new Sala("SALA-05", "Sala 5", 40, "Bloco B"));
        RecursoAcademico encontrado = service.buscarPorId(r.getId());
        assertEquals("SALA-05", encontrado.getCodigo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoIdInexistente() {
        assertThrows(ReservaException.class, () -> service.buscarPorId(999));
    }

    @Test
    @DisplayName("Deve listar recursos por tipo")
    void deveListarPorTipo() {
        service.cadastrar(new Sala("SALA-01", "Sala 1", 30, "Bloco A"));
        service.cadastrar(new Sala("LAB-01",  "Lab 1",  25, "Bloco B"));
        service.cadastrar(new Equipamento("PROJ-01", "Projetor", TipoRecurso.PROJETOR, "Depósito", false));

        List<RecursoAcademico> salas = service.listarPorTipo(TipoRecurso.SALA);
        assertEquals(2, salas.size());
    }

    @Test
    @DisplayName("Deve desativar recurso corretamente")
    void deveDesativarRecurso() {
        RecursoAcademico r = service.cadastrar(new Sala("SALA-01", "Sala 1", 30, "Bloco A"));
        service.desativar(r.getId());
        assertFalse(service.buscarPorId(r.getId()).isDisponivel());
    }

    @Test
    @DisplayName("Recurso desativado não deve aparecer como disponível no período")
    void recursoDesativadoNaoAparece() {
        Sala sala = new Sala("SALA-01", "Sala 1", 30, "Bloco A");
        service.cadastrar(sala);
        service.desativar(sala.getId());

        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim    = inicio.plusHours(2);
        List<RecursoAcademico> disp = service.listarDisponiveisNoPeriodo(inicio, fim);
        assertTrue(disp.isEmpty());
    }

    @Test
    @DisplayName("Equipamento frágil deve exigir acompanhamento técnico")
    void equipamentoFragilRequerTecnico() {
        Equipamento equip = new Equipamento("KIT-01", "Kit Robótica",
                TipoRecurso.EQUIPAMENTO_FRAGIL, "Depósito", true);
        service.cadastrar(equip);
        assertTrue(equip.isRequerAcompanhamentoTecnico());
    }

    @Test
    @DisplayName("Sala deve calcular penalidade de atraso corretamente")
    void salaPenalidadeAtraso() {
        Sala sala = new Sala("SALA-01", "Sala", 30, "Bloco A");
        String pen = sala.calcularPenalidade(true, false);
        assertTrue(pen.contains("AVISO"));
    }

    @Test
    @DisplayName("Sala sem atraso e sem avaria não deve ter penalidade")
    void salaSemPenalidade() {
        Sala sala = new Sala("SALA-01", "Sala", 30, "Bloco A");
        assertEquals("SEM_PENALIDADE", sala.calcularPenalidade(false, false));
    }

    @Test
    @DisplayName("Equipamento frágil com avaria deve retornar BLOQUEIO_30_DIAS")
    void equipamentoFragilAvariaBloqueio30() {
        Equipamento equip = new Equipamento("KIT-01", "Kit",
                TipoRecurso.EQUIPAMENTO_FRAGIL, "Dep", true);
        String pen = equip.calcularPenalidade(false, true);
        assertTrue(pen.contains("BLOQUEIO_30_DIAS"));
    }

    @Test
    @DisplayName("Equipamento não-frágil com avaria deve gerar REGISTRO_OCORRENCIA")
    void equipamentoNaoFragilAvaria() {
        Equipamento equip = new Equipamento("NOTE-01", "Notebook",
                TipoRecurso.NOTEBOOK, "Dep", false);
        String pen = equip.calcularPenalidade(false, true);
        assertTrue(pen.contains("REGISTRO_OCORRENCIA"));
    }

    @Test
    @DisplayName("Atraso em equipamento deve gerar BLOQUEIO_7_DIAS")
    void equipamentoAtrasoBloqueia7Dias() {
        Equipamento equip = new Equipamento("NOTE-01", "Notebook",
                TipoRecurso.NOTEBOOK, "Dep", false);
        String pen = equip.calcularPenalidade(true, false);
        assertTrue(pen.contains("BLOQUEIO_7_DIAS"));
    }
}
