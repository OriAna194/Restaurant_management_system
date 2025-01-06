package org.example;
import java.util.List;

public class Recipe {
    private Pastry pastry;
    private List<Ingredient> ingredients;
    private String instructions;

    public Recipe() {}

    public Recipe(Pastry pastry, List<Ingredient> ingredients, String instructions) {
        this.pastry = pastry;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Pastry getPastry() {
        return pastry;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void displayRecipe() {
        System.out.println("Recipe for " + pastry.getName() + " (" + pastry.getType() + ")");
        Ingredient.groupIngredientsByUnit(ingredients);

        System.out.println("Instructions: " + instructions);
    }

    public void scaleRecipe(double multiplier) {
        for (Ingredient ingredient : ingredients) {
            ingredient.scale(multiplier);
        }
    }
}
