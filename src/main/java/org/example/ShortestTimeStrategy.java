package org.example;

import java.util.List;

public class ShortestTimeStrategy implements QueueAllocationStrategy
{
    @Override
    public void allocateClientToQueue(Client client, SimulationManager simulationManager)
    {
        List<Server> queues = simulationManager.getQueues();
        Server shortestTimeServer = queues.get(0);

        for (Server server : queues) {
            if (server.getWaitingPeriod() < shortestTimeServer.getWaitingPeriod()) {
                shortestTimeServer = server;
            }
        }
        shortestTimeServer.addClient(client);
    }
}