package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Client> queue = new LinkedBlockingQueue<>();
    private int id;
    private boolean open;
    private final AtomicInteger waitingPeriod = new AtomicInteger(0);
    private SimulationManager simulationManager;

    public Server(int id, SimulationManager simulationManager) {
        this.id = id;
        this.simulationManager = simulationManager;
        this.open = true;
    }

    public void run()
    {
        while (open || !queue.isEmpty()) {
            try {
                Client client = queue.peek(); // does not remove the head of the queue
                if (client != null)
                {
                    if (client.getServiceTime() > 0)
                    {
                        simulationManager.updateServiceTime(client.getServiceTime());
                        simulationManager.updateWaitingTime(waitingPeriod.getAndDecrement()); // Update waiting time as countdown
                        client.decrementServiceTime();
                        simulationManager.clientsServed();
                    }
                    if (client.getServiceTime() <= 0) {
                        queue.poll(); //remove the head of after service is done
                    }
                }
                TimeUnit.SECONDS.sleep(1); // simulate one second of service
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void addClient(Client client) {
        queue.add(client);
        waitingPeriod.addAndGet(client.getServiceTime()); //adding each service time to waiting period
        simulationManager.checkAndUpdatePeakHour();
    }

    public boolean isOpen() {
        return open || !queue.isEmpty();
    }

    public int getQueueSize() {
        return queue.size();
    }

    public int getId()
    {
        return id;
    }
    public BlockingQueue<Client> getQueue()
    {
        return queue;
    }
    public int getWaitingPeriod()
    {
        return waitingPeriod.get();
    }
}