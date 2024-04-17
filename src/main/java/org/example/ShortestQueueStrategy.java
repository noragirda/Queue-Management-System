package org.example;

public class ShortestQueueStrategy implements QueueAllocationStrategy
{
    @Override
    public void allocateClientToQueue(Client client, SimulationManager simulationManager)
    {
        Server shortestQueueServer = simulationManager.getQueues().get(0);
        for(Server server : simulationManager.getQueues())
        {
            if (server.getQueueSize() < shortestQueueServer.getQueueSize())
            {
                shortestQueueServer = server;
            }
        }
        shortestQueueServer.addClient(client);
    }
}
