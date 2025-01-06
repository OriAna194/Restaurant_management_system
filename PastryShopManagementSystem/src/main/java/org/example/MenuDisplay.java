package org.example;

public class MenuDisplay {
    private static final String HORIZONTAL_LINE = "├" + "─".repeat(40) + "┤";
    private static final String TOP_LINE = "┌" + "─".repeat(40) + "┐";
    private static final String BOTTOM_LINE = "└" + "─".repeat(40) + "┘";

    public static void displayMenu(String title, String... options) {
        System.out.println(TOP_LINE);
        System.out.printf("│ %-38s │%n", title);
        System.out.println(HORIZONTAL_LINE);

        for (int i = 0; i < options.length; i++) {
            System.out.printf("│ %d. %-35s │%n", (i + 1), options[i]);
        }

        System.out.println(BOTTOM_LINE);
    }
}
