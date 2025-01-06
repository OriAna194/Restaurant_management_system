package org.example;

import java.util.List;
import java.util.Scanner;

public interface ChefActions {
    Recipe createRecipe(Scanner scanner) throws DuplicateEntryException;
    List<Recipe> getRecipes();
    List<Ingredient> getIngredients();
}
