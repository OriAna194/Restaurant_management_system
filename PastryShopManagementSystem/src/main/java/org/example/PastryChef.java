package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PastryChef implements ChefActions{

    private String name;
    private List<Recipe> recipes;
    private List<Ingredient> ingredients;

    public PastryChef(String name) {
        this.name = name;
        this.recipes = new ArrayList<>();
        this.ingredients = new ArrayList<>();
    }

    @Override
    public Recipe createRecipe(Scanner scanner) throws DuplicateEntryException {
        String pastryName = Main.getNonEmptyString("Enter pastry name: ");

        // Check for duplicate recipe names
        for (Recipe recipe : recipes) {
            if (recipe.getPastry().getName().equalsIgnoreCase(pastryName)) {
                throw new DuplicateEntryException("A recipe with the name '" + pastryName + "' already exists.");
            }
        }

        String pastryType = Main.getNonEmptyString("Enter pastry type: ");
        Pastry pastry = new Pastry(pastryName, pastryType);

        List<Ingredient> recipeIngredients = new ArrayList<>();
        boolean addingIngredients = true;

        while (addingIngredients) {
            String ingredientName = Main.getNonEmptyString("Enter ingredient name (or 'done' to finish): ");
            if (ingredientName.equalsIgnoreCase("done")) {
                addingIngredients = false;
            } else {
                double quantity = Main.getValidDouble(scanner, "Enter quantity for " + ingredientName + ": ");
                String unit;

                do {
                    unit = Main.getNonEmptyString("Enter the unit (e.g., grams, cups): ").trim();

                    if (unit.matches(".*\\d.*")) {//Check if contains numbers
                        System.out.println("Please enter a valid unit.");
                        unit = "";//Reset the unit to continue the loop
                    }
                } while (unit.isEmpty());

                Ingredient ingredient = new Ingredient(ingredientName, quantity, unit);
                recipeIngredients.add(ingredient);
                ingredients.add(ingredient);
            }
        }

        String instructions = Main.getNonEmptyString("Enter instructions for the recipe: ");

        Recipe recipe = new Recipe(pastry, recipeIngredients, instructions);
        recipes.add(recipe);
        return recipe;
    }


    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    @Override
    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @Override
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
