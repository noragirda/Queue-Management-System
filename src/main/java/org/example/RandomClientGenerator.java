package org.example;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
public class RandomClientGenerator
{
    private final int numberOfClients;
    private final int minArrivalTime;
    private final int maxArrivalTime;
    private final int minServiceTime;
    private final int maxServiceTime;
    private final Random random = new Random();

    public RandomClientGenerator(int numberOfClients, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime) {
        this.numberOfClients = numberOfClients;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
    }
    public List<Client> generateClients() {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = minArrivalTime + random.nextInt(maxArrivalTime - minArrivalTime + 1);
            int serviceTime = minServiceTime + random.nextInt(maxServiceTime - minServiceTime + 1);
            clients.add(new Client(i + 1, arrivalTime, serviceTime));
        }
        return clients;
    }
}
