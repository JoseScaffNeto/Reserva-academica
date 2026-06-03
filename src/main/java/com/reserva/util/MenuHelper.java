package com.reserva.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MenuHelper {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Scanner sc;

    public MenuHelper(Scanner sc) {
        this.sc = sc;
    }

    public int lerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  ✗ Digite um número inteiro válido.");
            }
        }
    }

    public String lerString(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    public boolean lerSimNao(String prompt) {
        while (true) {
            System.out.print(prompt + " (s/n): ");
            String r = sc.nextLine().trim().toLowerCase();
            if (r.equals("s")) return true;
            if (r.equals("n")) return false;
            System.out.println("  ✗ Digite 's' ou 'n'.");
        }
    }

    public LocalDateTime lerDataHora(String prompt) {
        while (true) {
            System.out.print(prompt + " (dd/MM/yyyy HH:mm): ");
            try {
                return LocalDateTime.parse(sc.nextLine().trim(), FMT);
            } catch (DateTimeParseException e) {
                System.out.println("  ✗ Formato inválido. Use dd/MM/yyyy HH:mm");
            }
        }
    }

    public void pausar() {
        System.out.print("\nPressione Enter para continuar...");
        sc.nextLine();
    }

    public static void titulo(String texto) {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("  " + texto);
        System.out.println("══════════════════════════════════════════");
    }

    public static void separador() {
        System.out.println("──────────────────────────────────────────");
    }
}
