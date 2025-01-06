package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.FileNotFoundException;

import java.util.InputMismatchException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ChefActions chef = new PastryChef("Gordon Ramsay");
    private static final List<Order> orders = new ArrayList<>();
    private static final String PASTRIES_FILE = "pastries.json";
    private static final String INGREDIENTS_FILE = "ingredients.json";
    private static final String ORDERS_FILE = "orders.json";

    public static void main(String[] args) {
        loadData();
        boolean running = true;

        while (running) {
            MenuDisplay.displayMenu("Main Menu",
                    "Client Scene",
                    "Chef Scene",
                    "Save and Exit"
            );
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    clientsScene();
                    break;
                case 2:
                    chefScene();
                    break;
                case 3:
                    saveData();
                    System.out.println("Exiting application.");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void clientsScene() {
        boolean running = true;

        while (running) {
            MenuDisplay.displayMenu("Client Scene",
                    "Create a new order",
                    "Add pastry to an order",
                    "View orders status",
                    "Back to Main Menu"
            );

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    createNewOrder();
                    break;
                case 2:
                    addPastryToOrder();
                    break;

                case 3:
                    viewAllOrdersStatus();
                    break;
                case 4:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void chefScene() {
        boolean running = true;

        while (running) {
            MenuDisplay.displayMenu("Chef Scene",
                    "Create a new recipe",
                    "View stored recipes",
                    "View all orders",
                    "Update the orders status",
                    "Back to Main Menu"
            );

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    try {
                        Recipe newRecipe = chef.createRecipe(scanner); // This line throws DuplicateEntryException
                        newRecipe.displayRecipe();
                    } catch (DuplicateEntryException e) {
                        System.out.println("Error: " + e.getMessage());  // Handle the exception
                    }
                    break;
                case 2:
                    viewAndSelectRecipe();
                    break;
                case 3:
                    viewAllOrdersStatus();
                    break;
                case 4:
                    updateIndividualPastryStatus();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }



    private static void updateIndividualPastryStatus() {
        if (orders.isEmpty()) {
            System.out.println("No orders available. Please create an order first.");
            return;
        }

        // Step 1: Display All Orders
        System.out.println("\nAll Orders:");
        int orderCount = 1;
        for (Order order : orders) {
            System.out.println(orderCount + ". Order ID: " + order.getOrderId() + " - Status: " + order.getPreparationStatus());
            orderCount++;
        }

        // Step 2: Ask the user to select an Order to Update
        System.out.print("Enter the order ID to update: ");
        String orderInput = scanner.nextLine();  // Assuming scanner is used to get user input
        int orderId = Integer.parseInt(orderInput);

        if (orderId < 1 || orderId > orders.size()) {
            System.out.println("Invalid order ID. Please try again.");
            return; // Exit the method if invalid order ID
        }

        Order selectedOrder = orders.get(orderId - 1);

        // Step 3: Display Pastries in Selected Order
        System.out.println("\nPastries in Order ID " + selectedOrder.getOrderId() + ":");
        int pastryCount = 1;
        for (Pastry pastry : selectedOrder.getPastries()) {
            System.out.println(pastryCount + ". " + pastry.getName() + " - Status: " + pastry.getPreparationStatus());
            pastryCount++;
        }

        // Step 4: Ask the user to select pastries to update
        List<Pastry> pastriesToUpdate = new ArrayList<>();
        String pastryName;
        do {
            System.out.print("Enter the name of the pastry to update (or 'done' to finish): ");
            pastryName = scanner.nextLine();

            if (!pastryName.equalsIgnoreCase("done")) {
                Pastry pastryToUpdate = null;
                for (Pastry pastry : selectedOrder.getPastries()) {
                    if (pastry.getName().equalsIgnoreCase(pastryName)) {
                        pastryToUpdate = pastry;
                        break;
                    }
                }
                if (pastryToUpdate != null) {
                    pastriesToUpdate.add(pastryToUpdate);
                    System.out.println(pastryToUpdate.getName() + " has been added to the update list.");
                } else {
                    System.out.println("Pastry not found in this order.");
                }
            }
        } while (!pastryName.equalsIgnoreCase("done"));

        // Step 5: Ask for New Status for Each Selected Pastry
        // First, collect the statuses before starting any threads
        List<PreparationStatusEnum> statusesToUpdate = new ArrayList<>();
        for (Pastry pastry : pastriesToUpdate) {
            System.out.print("Enter new status for " + pastry.getName() + " (NOT_STARTED, IN_PROGRESS, READY): ");
            String statusInput = scanner.nextLine();
            PreparationStatusEnum status = PreparationStatusEnum.valueOf(statusInput.toUpperCase());

            if (status == null) {
                System.out.println("Invalid status. Please try again.");
                return;
            }
            statusesToUpdate.add(status);
        }

        // Step 6: Create threads to update the pastries only after status input
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < pastriesToUpdate.size(); i++) {
            Pastry pastry = pastriesToUpdate.get(i);
            PreparationStatusEnum status = statusesToUpdate.get(i);

            // Create a new thread for each pastry update
            PastryUpdateThread updateThread = new PastryUpdateThread(pastry, status);
            updateThread.start();
            threads.add(updateThread);
        }

        // Step 7: Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();  // Wait until each thread finishes execution
            } catch (InterruptedException e) {
                System.out.println("Error: Thread interrupted.");
            }
        }

        // After all threads have completed, collect and display the intermediate results
        List<String> updateResults = PastryUpdateThread.getResults();
        for (String result : updateResults) {
            System.out.println(result); // This is where you print the status updates
        }

        // Step 8: Recalculate the order status
        selectedOrder.checkOrderStatus();  // Recalculate the order status after updating the pastries' statuses

        // Step 9: Display the updated order status
        System.out.println("Updated Order Status: " + selectedOrder.getPreparationStatus());
        System.out.println("Pastry status updates completed for the selected pastries.");
    }






    private static void createNewOrder() {
        // Create a new order and assign it an ID
        Order newOrder = new Order();
        orders.add(newOrder);
        System.out.println("New order created. Order ID: " + orders.size());

        boolean addingItems = true;

        // Show the available pastries menu
        while (addingItems) {
            System.out.println("Available Pastries:");
            int pastryIndex = 1;
            for (Recipe recipe : chef.getRecipes()) {
                System.out.println(pastryIndex + ". " + recipe.getPastry().getName() + " (" + recipe.getPastry().getType() + ")");
                pastryIndex++;
            }

            // Ask the client to enter the name of the pastry they want to add
            String pastryName = getNonEmptyString("Enter the name of the pastry you want to add to the order (or type 'cancel' to cancel the order): ").trim();

            if (pastryName.equalsIgnoreCase("cancel")) {
                // If the client types 'cancel', discard the order and return to the main menu
                System.out.println("Order has been canceled.");
                orders.remove(newOrder);  // Remove the order from the list as it was canceled
                addingItems = false;  // Stop adding items, effectively canceling the order
            } else {
                // Search for the pastry by name
                try {
                    Pastry pastry = searchForPastry(pastryName);
                    newOrder.addPastry(pastry);
                    System.out.println(pastry.getName() + " has been added to the order.");

                    // Ask the client if they want to add another item
                    String response = getNonEmptyString("Do you want to add another item? (yes/no): ").trim().toLowerCase();
                    if (!response.equals("yes")) {
                        addingItems = false;  // Stop adding items if the response is not 'yes'
                        // Display order confirmation message
                        System.out.println("Your order has been successfully sent! Order ID: " + orders.size());
                    }
                } catch (PastryNotFoundException e) {
                    System.out.println("Error: " + e.getMessage());  // Handle pastry not found
                }
            }
        }
    }

    private static void addPastryToOrder() {
        if (orders.isEmpty()) {
            System.out.println("No orders available. Please create an order first.");
            return;
        }


        int orderId = getIntInput("Enter the order ID to add a pastry to: ", 1, orders.size());
        String pastryName = getNonEmptyString("Enter the name of the pastry to add to the order: ");


        try {
            Pastry pastry = searchForPastry(pastryName);
            orders.get(orderId - 1).addPastry(pastry);
            System.out.println("Pastry added to Order " + orderId + ".");
        } catch (PastryNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static Pastry searchForPastry(String name) throws PastryNotFoundException {
        for (Recipe recipe : chef.getRecipes()) {
            if (recipe.getPastry().getName().equalsIgnoreCase(name)) {
                return recipe.getPastry();
            }
        }
        throw new PastryNotFoundException("Pastry with name " + name + " not found.");
    }

//    private static void updateIndividualPastryStatus() {
//        if (orders.isEmpty()) {
//            System.out.println("No orders available. Please create an order first.");
//            return;
//        }
//
//        //validating order ID input
//        int orderId = getIntInput("Enter the order ID to update a pastry in: ", 1, orders.size());
//        Order selectedOrder = orders.get(orderId - 1);
//
//        //validating pastry name by checking if it exists in the order
//        Pastry pastryToUpdate = null;
//        while (pastryToUpdate == null) {
//            String pastryName = getNonEmptyString("Enter the name of the pastry to update: ");
//
//
//            for (Pastry pastry : selectedOrder.getPastries()) {
//                if (pastry.getName().equalsIgnoreCase(pastryName)) {
//                    pastryToUpdate = pastry;
//                    break;
//                }
//            }
//
//
//            if (pastryToUpdate == null) {
//                System.out.println("Pastry not found in this order. Please enter a valid pastry name.");
//            }
//        }
//
//        //validating the preparation status and update the pastry status
//        PreparationStatusEnum status = getValidStatus("Enter new status for the pastry (NOT_STARTED, IN_PROGRESS, READY): ");
//        pastryToUpdate.updatePreparationStatus(status);
//        System.out.println("Status updated successfully.");
//    }


    private static void viewAllOrdersStatus() {
        if (orders.isEmpty()) {
            System.out.println("No orders available.");
            return;
        }

        int orderCount = 1;
        for (Order order : orders) {
            System.out.println("Order " + orderCount + ":");
            for (Pastry pastry : order.getPastries()) {
                System.out.println("Status for " + pastry.getName() + ": " + pastry.getPreparationStatus());
            }
            orderCount++;
        }
    }


    private static void viewAndSelectRecipe() {

        if (chef.getRecipes().isEmpty()) {
            System.out.println("No recipes available.");
            return;
        }

        System.out.println("Stored Recipes:");
        int index = 1;
        for (Recipe recipe : chef.getRecipes()) {
            System.out.println(index + ". " + recipe.getPastry().getName() + " (" + recipe.getPastry().getType() + ")");
            index++;
        }

        int choice = getIntInput("Enter the recipe number to view its full details or '0' to go back: ", 0, chef.getRecipes().size());

        if (choice == 0) {
            return;
        }


        Recipe selectedRecipe = chef.getRecipes().get(choice - 1);
        selectedRecipe.displayRecipe();
    }


















    // INPUT VALIDATION METHODS

    // input validation for integer within a specified range
    private static int getIntInput(String prompt, int min, int max) {
        int input;
        while (true) {
            try {
                System.out.print(prompt);
                input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    // input validation for a non-empty string
    public static String getNonEmptyString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }

    // input validation for valid PreparationStatusEnum value
    private static PreparationStatusEnum getValidStatus(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return PreparationStatusEnum.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Please choose from: NOT_STARTED, IN_PROGRESS, READY.");
            }
        }
    }

    public static double getValidDouble(Scanner scanner, String prompt) {
        double input;
        while (true) {
            System.out.print(prompt);
            try {
                input = Double.parseDouble(scanner.nextLine().trim());
                if (input > 0) {
                    return input;
                } else {
                    System.out.println("Quantity must be a positive number. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }





    private static void loadData() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            //load Recipes
            File pastriesFile = new File(PASTRIES_FILE);
            if (pastriesFile.exists()) {
                System.out.println("Loading previous pastries...");
                List<Recipe> recipes = mapper.readValue(pastriesFile, new TypeReference<List<Recipe>>() {});
                ((PastryChef) chef).setRecipes(recipes);
            } else {
                System.out.println("Pastries file not found. Loading default or empty recipe list.");
            }

            //load Ingredients
            File ingredientsFile = new File(INGREDIENTS_FILE);
            if (ingredientsFile.exists()) {
                System.out.println("Loading previous ingredients...");
                List<Ingredient> ingredients = mapper.readValue(ingredientsFile, new TypeReference<List<Ingredient>>() {});
                ((PastryChef) chef).setIngredients(ingredients);
            } else {
                System.out.println("Ingredients file not found. Proceeding without ingredient data.");
            }

            //load Orders
            File ordersFile = new File(ORDERS_FILE);
            if (ordersFile.exists()) {
                System.out.println("Loading previous orders...");
                List<Order> loadedOrders = mapper.readValue(ordersFile, new TypeReference<List<Order>>() {});
                orders.addAll(loadedOrders);
            } else {
                System.out.println("Orders file not found. Starting with no existing orders.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("A required data file could not be found: " + e.getMessage());
        } catch (JsonMappingException e) {
            System.out.println("Error in data format: Please check the file contents and try again.");
        } catch (IOException e) {
            System.out.println("Failed to load previous session due to an unexpected error: " + e.getMessage());
        }
    }

    private static void saveData() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            //save Recipes
            mapper.writeValue(new File(PASTRIES_FILE), chef.getRecipes());  //interface method
            System.out.println("Recipes saved to " + PASTRIES_FILE);

            //save Ingredients
            mapper.writeValue(new File(INGREDIENTS_FILE), chef.getIngredients());  //interface method
            System.out.println("Ingredients saved to " + INGREDIENTS_FILE);

            //save Orders
            mapper.writeValue(new File(ORDERS_FILE), orders);
            System.out.println("Orders saved to " + ORDERS_FILE);

            System.out.println("Session saved.");
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file to save data: " + e.getMessage());
        } catch (JsonMappingException e) {
            System.out.println("Data format error: Could not save session.");
        } catch (IOException e) {
            System.out.println("Failed to save data due to an unexpected error: " + e.getMessage()); //failed input/output operation
        }
    }
}
