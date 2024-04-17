package org.example;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable
{
    private final List<Server> queues=new ArrayList<>();
    private final int totalClients;
    private final int totalQueues;
    private final int simulationTime;
    private final int minArrivalTime;
    private final int maxArrivalTime;
    private final int minServiceTime;
    private final int maxServiceTime;
    private final QueueDisplayGUI queueDisplayGUI;
    private final QueueAllocationStrategy queueAllocationStrategy;
    private final PriorityQueue<Client> clients;
    private final ExecutorService executorService;
    int currentTime=0;
    private AtomicBoolean running = new AtomicBoolean(false);
    private BufferedWriter logWriter;
    private AtomicInteger peakHourClients = new AtomicInteger(0);
    private int peakHour = 0;
    private AtomicInteger totalWaitingTime = new AtomicInteger(0);
    private AtomicInteger totalServiceTime = new AtomicInteger(0);
    private AtomicInteger totalServedClients = new AtomicInteger(0);

    public SimulationManager(int totalClients, int totalQueues, int simulationTime, QueueAllocationStrategy queueAllocationStrategy, int minArrivalTime,int maxArrivalTime, int minServiceTime,int maxServiceTime, QueueDisplayGUI queueDisplayGUI)
    {
        this.totalClients=totalClients;
        this.totalQueues=totalQueues;
        this.simulationTime=simulationTime;
        this.queueAllocationStrategy = queueAllocationStrategy;
        this.minArrivalTime=minArrivalTime;
        this.maxArrivalTime=maxArrivalTime;
        this.minServiceTime=minServiceTime;
        this.maxServiceTime=maxServiceTime;
        this.clients=new PriorityQueue<>(Comparator.comparingInt(Client::getArrivalTime));
        RandomClientGenerator generator = new RandomClientGenerator(totalClients, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime);
        clients.addAll(generator.generateClients());
        for(int i=0;i<totalQueues;i++)
        {
            Server queue=new Server(i+1, this);
            queues.add(queue);
        }
        this.executorService = Executors.newFixedThreadPool(totalQueues);
        queues.forEach(executorService::execute);
        this.queueDisplayGUI = queueDisplayGUI;
        try
        {
            logWriter = new BufferedWriter(new FileWriter("log.txt"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void distributeClients()
    {
        while (!clients.isEmpty() && clients.peek().getArrivalTime() <= currentTime) {
            Client client = clients.poll();
            queueAllocationStrategy.allocateClientToQueue(client, this);
        }
    }
    private boolean anyQueueActive()
    {
        return queues.stream().anyMatch(Server::isOpen);
    }
    private void logState() throws IOException
    {
        try {
            StringBuilder logEntry = new StringBuilder();
            logEntry.append("\n");
            logEntry.append("Time ").append(currentTime).append("\n");
            clients.forEach(client -> logEntry.append(getClientInfo(client)).append(" "));
            logEntry.append("\n");
            for (Server queue : queues) {
                logEntry.append("Queue ").append(queue.getId()).append(": ");
                queue.getQueue().forEach(client -> logEntry.append(getClientInfo(client)).append("; "));
                logEntry.append("\n");
            }
            logWriter.write(logEntry.toString());
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String getClientInfo(Client client) {
        return "(" + client.getId() + "," + client.getArrivalTime() + "," + client.getServiceTime() + ")";
    }
    public void updateServiceTime(int serviceTime) {
        totalServiceTime.addAndGet(serviceTime);
    }
    public void updateWaitingTime(int waitingTime) {
        totalWaitingTime.addAndGet(waitingTime);
    }
    public void clientsServed() {
        totalServedClients.incrementAndGet();
    }
    public void checkAndUpdatePeakHour()
    {
        int currentClients = queues.stream().mapToInt(Server::getQueueSize).sum();
        if (currentClients > peakHourClients.get()) {
            peakHourClients.set(currentClients);
            peakHour = currentTime;
        }
    }

    private void calculateAndDisplayStatistics()
    {
        double averageWaitingTime = (double) totalWaitingTime.get() / totalServedClients.get();
        double averageServiceTime = (double) totalServiceTime.get() / totalServedClients.get();

        try
        {
            logWriter.write("Average waiting time: " + averageWaitingTime + "\n");
            logWriter.write("Average service time: " + averageServiceTime + "\n");
            logWriter.write("Peak hour: " + peakHour + "\n");
            logWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        queueDisplayGUI.displayStatistics(averageWaitingTime, averageServiceTime, peakHour);
    }
    public void run() {
        running.set(true);
        try {
            while (running.get() && currentTime <= simulationTime && (!clients.isEmpty() || anyQueueActive()))
            {
                distributeClients();//uses strategy pattern to allocate clients to queues
                logState(); //prints the state of the simulation

                queueDisplayGUI.updateTimer(currentTime);//real time timer display
                StringBuilder queueInfo = new StringBuilder();//will display the real time queue status
                for (Server queue : this.queues) {
                    queueInfo.append("Queue ").append(queue.getId()).append(": ").append(queue.getQueueSize()).append(" clients\n");
                }
                String displayText = queueInfo.toString();
                queueDisplayGUI.updateQueueDisplay(displayText);//adding the status to the GUI
                //SwingUtilities.invokeLater(() -> queueDisplayGUI.updateQueueDisplay(displayText));

                TimeUnit.SECONDS.sleep(1); // simulate time passing
                currentTime++;
            }
            calculateAndDisplayStatistics();//after the simulation is doen displaying the averages and peak hour
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            shutdownAndAwaitTermination(executorService);//shutting down the executor service
            SwingUtilities.invokeLater(() -> queueDisplayGUI.closeDisplay()); // close the display when the simulation is complete
            try
            {
                if(logWriter!=null)
                {
                    logWriter.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        running.set(false);
    }
    private void shutdownAndAwaitTermination(ExecutorService pool)
    {
        pool.shutdown(); // can't add new tasks
        try {
            // waiting a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // cancel currently executing tasks
                // waiting a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            //cancel again if current thread is also interrupted
            pool.shutdownNow();
            // keep interrupt status
            Thread.currentThread().interrupt();
        }
    }
    public List<Server> getQueues()
    {
        return queues;
    }
}