package com.reserva.service;

import com.reserva.exception.ReservaException;
import com.reserva.model.TipoUsuario;
import com.reserva.model.Usuario;
import com.reserva.repository.UsuarioRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UsuarioService - Testes Unitários")
class UsuarioServiceTest {

    private UsuarioRepository repo;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        Usuario.resetContador();
        repo    = new UsuarioRepository();
        service = new UsuarioService(repo);
    }

    @Test
    @DisplayName("Deve cadastrar usuário com dados válidos")
    void deveCadastrarUsuario() {
        Usuario u = service.cadastrar("Ana", "ana@test.br", "MAT001", TipoUsuario.PROFESSOR);
        assertNotNull(u);
        assertEquals("Ana", u.getNome());
        assertFalse(u.isBloqueado());
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com e-mail duplicado")
    void naoDeveCadastrarEmailDuplicado() {
        service.cadastrar("Ana", "ana@test.br", "MAT001", TipoUsuario.PROFESSOR);
        assertThrows(ReservaException.class, () ->
                service.cadastrar("Ana 2", "ana@test.br", "MAT002", TipoUsuario.PROFESSOR));
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com matrícula duplicada")
    void naoDeveCadastrarMatriculaDuplicada() {
        service.cadastrar("Ana", "ana@test.br", "MAT001", TipoUsuario.PROFESSOR);
        assertThrows(ReservaException.class, () ->
                service.cadastrar("Carlos", "carlos@test.br", "MAT001", TipoUsuario.TECNICO));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID existente")
    void deveBuscarPorId() {
        Usuario u = service.cadastrar("Bruno", "bruno@test.br", "MAT002", TipoUsuario.TECNICO);
        assertEquals(u.getEmail(), service.buscarPorId(u.getId()).getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void deveLancarExcecaoIdInexistente() {
        assertThrows(ReservaException.class, () -> service.buscarPorId(999));
    }

    @Test
    @DisplayName("Deve listar todos os usuários cadastrados")
    void deveListarTodos() {
        service.cadastrar("Ana", "ana@test.br", "MAT001", TipoUsuario.PROFESSOR);
        service.cadastrar("Bruno", "bruno@test.br", "MAT002", TipoUsuario.TECNICO);
        assertEquals(2, service.listarTodos().size());
    }

    @Test
    @DisplayName("Deve listar usuários filtrados por tipo")
    void deveListarPorTipo() {
        service.cadastrar("Ana", "ana@test.br", "MAT001", TipoUsuario.PROFESSOR);
        service.cadastrar("Bruno", "bruno@test.br", "MAT002", TipoUsuario.TECNICO);
        service.cadastrar("Carla", "carla@test.br", "MAT003", TipoUsuario.PROFESSOR);
        assertEquals(2, service.listarPorTipo(TipoUsuario.PROFESSOR).size());
    }

    @Test
    @DisplayName("Usuario bloqueado deve retornar estaAtivo() = false")
    void usuarioBloqueadoNaoEstaAtivo() {
        Usuario u = service.cadastrar("Diego", "diego@test.br", "MAT004", TipoUsuario.ALUNO_AUTORIZADO);
        u.bloquear(7);
        assertFalse(u.estaAtivo());
    }

    @Test
    @DisplayName("Coordenador pode aprovar reservas")
    void coordenadorPodeAprovar() {
        Usuario u = service.cadastrar("Ana", "ana@test.br", "MAT001", TipoUsuario.COORDENADOR);
        assertTrue(u.podeAprovarReservas());
    }

    @Test
    @DisplayName("Aluno não pode aprovar reservas")
    void alunoNaoPodeAprovar() {
        Usuario u = service.cadastrar("Diego", "diego@test.br", "MAT001", TipoUsuario.ALUNO_AUTORIZADO);
        assertFalse(u.podeAprovarReservas());
    }

    @Test
    @DisplayName("Deve remover usuário existente")
    void deveRemoverUsuario() {
        Usuario u = service.cadastrar("Ana", "ana@test.br", "MAT001", TipoUsuario.PROFESSOR);
        service.remover(u.getId());
        assertThrows(ReservaException.class, () -> service.buscarPorId(u.getId()));
    }
}
