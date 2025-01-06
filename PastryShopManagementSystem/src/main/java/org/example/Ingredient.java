package org.example;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class Ingredient implements Comparable<Ingredient>{
    private String name;
    private double quantity;
    private String unit;

    public Ingredient() {
    }

    public Ingredient(String name, double quantity, String unit){
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName(){
        return name;
    }

    public double getQuantity(){
        return quantity;
    }

    public String getUnit(){
        return unit;
    }

    //for baking multiple sets of the same pastry
    public void scale(double multiplier){
        this.quantity *= multiplier;
    }

    @Override
    public int compareTo(Ingredient other) {
        return Double.compare(this.quantity, other.quantity);
    }

    public static Map<String, List<Ingredient>> groupIngredientsByUnit(List<Ingredient> ingredients) {
        Map<String, List<Ingredient>> groupedIngredients = new HashMap<>();

        for (Ingredient ingredient : ingredients) {
            String unit = ingredient.getUnit();
            groupedIngredients.putIfAbsent(unit, new ArrayList<>());
            groupedIngredients.get(unit).add(ingredient);
        }

        System.out.println("Ingredients grouped by unit:");
        for (String unit : groupedIngredients.keySet()) {
            System.out.println(unit + ":");
            for (Ingredient ingredient : groupedIngredients.get(unit)) {
                System.out.println(" - " + ingredient.getName() + ": " + ingredient.getQuantity());
            }
        }

        return groupedIngredients;
    }



    public static void sortIngredientsByQuantity(List<Ingredient> ingredients) {
        Collections.sort(ingredients);
        System.out.println("Ingredients sorted by quantity:");
        for (Ingredient ingredient : ingredients) {
            System.out.println(ingredient.getName() + ": " + ingredient.getQuantity() + " " + ingredient.getUnit());
        }
    }

}
