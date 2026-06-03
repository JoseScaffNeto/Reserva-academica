package com.reserva.util;

import com.reserva.model.*;
import com.reserva.service.*;

import java.time.LocalDateTime;

/**
 * Popula o sistema com dados de exemplo para facilitar os testes manuais.
 */
public class DadosIniciais {

    public static void popular(UsuarioService usuarioSvc,
                                RecursoService recursoSvc,
                                ReservaService reservaSvc) {
        // ── Usuários ──────────────────────────────────────────────────────
        usuarioSvc.cadastrar("Ana Coordenadora", "ana@inst.br", "COORD001", TipoUsuario.COORDENADOR);
        usuarioSvc.cadastrar("Bruno Técnico",    "bruno@inst.br", "TEC001", TipoUsuario.TECNICO);
        usuarioSvc.cadastrar("Carla Professora", "carla@inst.br", "PROF001", TipoUsuario.PROFESSOR);
        usuarioSvc.cadastrar("Diego Aluno",      "diego@inst.br", "ALU001",  TipoUsuario.ALUNO_AUTORIZADO);

        // ── Recursos ──────────────────────────────────────────────────────
        recursoSvc.cadastrar(new Sala("LAB-01",  "Laboratório de Informática", 30, "Bloco A", true, true));
        recursoSvc.cadastrar(new Sala("SALA-05", "Sala de Aula 05",           40, "Bloco B", true, false));
        recursoSvc.cadastrar(new Sala("LAB-02",  "Laboratório de Química",    25, "Bloco C", false, true));
        recursoSvc.cadastrar(new Equipamento("PROJ-01", "Projetor Epson",  TipoRecurso.PROJETOR,          "Depósito A", false));
        recursoSvc.cadastrar(new Equipamento("NOTE-01", "Notebook Dell",   TipoRecurso.NOTEBOOK,          "Depósito A", false));
        recursoSvc.cadastrar(new Equipamento("KIT-01",  "Kit Robótica",    TipoRecurso.EQUIPAMENTO_FRAGIL, "Depósito B", true));

        // ── Reservas de exemplo ───────────────────────────────────────────
        LocalDateTime amanha9  = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime amanha11 = amanha9.withHour(11);
        LocalDateTime amanha14 = amanha9.withHour(14);
        LocalDateTime amanha16 = amanha9.withHour(16);

        // Coordenadora reserva LAB-01 (aprovada automaticamente)
        reservaSvc.criarReserva(1, 1, amanha9, amanha11, "Aula de POO", false);
        // Professor reserva SALA-05
        reservaSvc.criarReserva(3, 2, amanha14, amanha16, "Aula de Cálculo", false);
        // Aluno reserva PROJ-01
        reservaSvc.criarReserva(4, 4, amanha9, amanha11, "Apresentação TCC", false);

        System.out.println("✔ Dados iniciais carregados (4 usuários, 6 recursos, 3 reservas).");
    }
}
