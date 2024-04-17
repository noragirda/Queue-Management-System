package org.example;

public interface QueueAllocationStrategy
{
    void allocateClientToQueue(Client client, SimulationManager simulationManager);
}
