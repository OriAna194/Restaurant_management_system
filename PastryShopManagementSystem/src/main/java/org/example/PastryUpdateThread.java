package org.example;
import java.util.ArrayList;
import java.util.List;

public class PastryUpdateThread extends Thread {
    private final Pastry pastry;
    private final PreparationStatusEnum newStatus;
    private static final List<String> results = new ArrayList<>();

    public PastryUpdateThread(Pastry pastry, PreparationStatusEnum newStatus) {
        this.pastry = pastry;
        this.newStatus = newStatus;
    }

    @Override
    public void run() {
        synchronized (pastry) {  // Synchronize to ensure thread-safe access to the pastry object
            try {
                // Simulate updating the pastry's status (e.g., making it sleep for some time)
                Thread.sleep(1000);  // Simulating some work done by the thread
                pastry.updatePreparationStatus(newStatus);

                // Only add result, do not print inside the thread
                addResult("Updated " + pastry.getName() + " to " + newStatus);
            } catch (InterruptedException e) {
                addResult("Failed to update " + pastry.getName() + " due to interruption.");
            }
        }
    }

    private synchronized void addResult(String result) {
        results.add(result);
    }

    // This method can be called from the main thread to collect the results.
    public static List<String> getResults() {
        return results;
    }
}
