package org.example;
import java.util.ArrayList;
import java.util.List;

public class Order implements PreparationStatus {
    private static int orderCount = 0; // Static counter for generating unique order IDs
    private int orderId; // The unique order ID
    private List<Pastry> pastries;
    private PreparationStatusEnum preparationStatus;

    public Order() {
        this.orderId = ++orderCount; // Increment the counter and assign a unique order ID
        this.pastries = new ArrayList<>();
        this.preparationStatus = PreparationStatusEnum.NOT_STARTED;
    }

    // Getter for orderId
    public int getOrderId() {
        return orderId;
    }

    public void addPastry(Pastry pastry) {
        pastries.add(pastry);
    }

    @Override
    public PreparationStatusEnum getPreparationStatus() {
        return preparationStatus;
    }

    @Override
    public void updatePreparationStatus(PreparationStatusEnum status) {
        this.preparationStatus = status;
    }

    public void checkOrderStatus() {
        boolean allReady = pastries.stream().allMatch(p -> p.getPreparationStatus() == PreparationStatusEnum.READY);
        boolean allNotStarted = pastries.stream().allMatch(p -> p.getPreparationStatus() == PreparationStatusEnum.NOT_STARTED);

        if (allReady) {
            updatePreparationStatus(PreparationStatusEnum.COMPLETED);
        } else if (allNotStarted) {
            updatePreparationStatus(PreparationStatusEnum.NOT_STARTED);
        } else {
            updatePreparationStatus(PreparationStatusEnum.IN_PROGRESS);
        }
    }


    public List<Pastry> getPastries() {
        return pastries;
    }

//    public void updatePastryStatus(String pastryName, PreparationStatusEnum status) {
//        for (Pastry pastry : pastries) {
//            if (pastry.getName().equalsIgnoreCase(pastryName)) {
//                pastry.updatePreparationStatus(status);
//                System.out.println("Updated " + pastryName + " to status: " + status);
//                checkOrderStatus();  // Recalculate the overall order status after updating the pastry status
//                return;
//            }
//        }
//        System.out.println("Pastry " + pastryName + " not found in order.");
//    }

}
