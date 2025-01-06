package org.example;
import java.util.Collections;
import java.util.List;


public class Pastry implements PreparationStatus, Comparable<Pastry> {
    private String name;
    private String type;
    private PreparationStatusEnum preparationStatus;

    public Pastry() {
    }

    public Pastry(String name, String type) {
        this.name = name;
        this.type = type;
        this.preparationStatus = PreparationStatusEnum.NOT_STARTED;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public PreparationStatusEnum getPreparationStatus() {
        return preparationStatus;
    }

    public void updatePreparationStatus(PreparationStatusEnum status) {
        this.preparationStatus = status;
//        System.out.println("Pastry " + name + " status has been updated to: " + preparationStatus);
    }

    @Override
    public int compareTo(Pastry other) {
        return this.name.compareToIgnoreCase(other.name);//alphabetically
    }

    public static void sortPastriesByName(List<Pastry> pastries) {
        Collections.sort(pastries);
        System.out.println("Pastries sorted by name:");
        for (Pastry pastry : pastries) {
            System.out.println("Pastry: " + pastry.getName() + ", Type: " + pastry.getType());
        }
    }

}
