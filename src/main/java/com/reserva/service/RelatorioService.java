package com.reserva.service;

import com.reserva.model.Reserva;
import com.reserva.model.StatusReserva;
import com.reserva.repository.ReservaRepository;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioService {

    private final ReservaRepository reservaRepo;

    public RelatorioService(ReservaRepository reservaRepo) {
        this.reservaRepo = reservaRepo;
    }

    public void exibirIndicadores() {
        List<Reserva> todas = reservaRepo.listarTodas();
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("         INDICADORES GERAIS DO SISTEMA");
        System.out.println("══════════════════════════════════════════");
        System.out.println("Total de reservas     : " + todas.size());
        System.out.println("Aprovadas             : " + contar(todas, StatusReserva.APROVADA));
        System.out.println("Pendentes             : " + contar(todas, StatusReserva.PENDENTE));
        System.out.println("Canceladas            : " + contar(todas, StatusReserva.CANCELADA));
        System.out.println("Concluídas            : " + contar(todas, StatusReserva.CONCLUIDA));
        System.out.println("Com atraso            : " + contar(todas, StatusReserva.DEVOLVIDA_COM_ATRASO));
        System.out.println("Com avaria            : " + contar(todas, StatusReserva.DEVOLVIDA_COM_AVARIA));

        long ativas = contar(todas, StatusReserva.APROVADA) + contar(todas, StatusReserva.PENDENTE);
        double taxa = todas.isEmpty() ? 0 : (double) contar(todas, StatusReserva.APROVADA) / todas.size() * 100;
        System.out.printf("Taxa de ocupação      : %.1f%%%n", taxa);
        System.out.println("Reservas ativas agora : " + ativas);
        System.out.println("══════════════════════════════════════════");
    }

    public void exibirRelatorioMensal(int ano, int mes) {
        YearMonth ym = YearMonth.of(ano, mes);
        LocalDateTime inicio = ym.atDay(1).atStartOfDay();
        LocalDateTime fim    = ym.atEndOfMonth().atTime(23, 59, 59);
        List<Reserva> doMes  = reservaRepo.listarPorPeriodo(inicio, fim);

        System.out.println("\n══════════════════════════════════════════");
        System.out.printf("  RELATÓRIO MENSAL — %02d/%d%n", mes, ano);
        System.out.println("══════════════════════════════════════════");
        System.out.println("Total de reservas : " + doMes.size());
        System.out.println("Aprovadas         : " + contar(doMes, StatusReserva.APROVADA));
        System.out.println("Canceladas        : " + contar(doMes, StatusReserva.CANCELADA));
        System.out.println("Com atraso        : " + contar(doMes, StatusReserva.DEVOLVIDA_COM_ATRASO));
        System.out.println("Com avaria        : " + contar(doMes, StatusReserva.DEVOLVIDA_COM_AVARIA));

        // Recursos mais utilizados
        System.out.println("\n── Recursos mais utilizados ─────────────");
        doMes.stream()
             .collect(Collectors.groupingBy(r -> r.getRecurso().getCodigo(), Collectors.counting()))
             .entrySet().stream()
             .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
             .limit(5)
             .forEach(e -> System.out.printf("  %-15s → %d reserva(s)%n", e.getKey(), e.getValue()));

        // Horários de maior demanda
        System.out.println("\n── Horários de maior demanda ─────────────");
        doMes.stream()
             .collect(Collectors.groupingBy(r -> r.getInicio().getHour(), Collectors.counting()))
             .entrySet().stream()
             .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
             .limit(3)
             .forEach(e -> System.out.printf("  %02dh → %d reserva(s)%n", e.getKey(), e.getValue()));

        // Ambientes ociosos (sem nenhuma reserva no mês)
        System.out.println("\n── Ambientes sem reserva no período ──────");
        Set<Integer> usados = doMes.stream()
                .map(r -> r.getRecurso().getId()).collect(Collectors.toSet());
        // (listagem feita externamente via recursoRepo, aqui exibimos os IDs usados)
        if (usados.isEmpty()) System.out.println("  Nenhum recurso utilizado no período.");
        else System.out.println("  Recursos utilizados (IDs): " + usados);

        System.out.println("══════════════════════════════════════════");
    }

    private long contar(List<Reserva> lista, StatusReserva status) {
        return lista.stream().filter(r -> r.getStatus() == status).count();
    }
}
