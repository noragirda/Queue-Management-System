package org.example;

import java.util.concurrent.atomic.AtomicInteger;

public class Client
{
    private final int id;
    private final int arrivalTime;
    private final AtomicInteger serviceTime;
    private final AtomicInteger waitingTime;
    public Client(int id, int arrivalTime, int serviceTime)
    {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = new AtomicInteger(serviceTime);
        this.waitingTime = new AtomicInteger(0);
    }
    public int getId()
    {
        return id;
    }
    public int getArrivalTime()
    {
        return arrivalTime;
    }
    public int getServiceTime()
    {
        return serviceTime.get();
    }
    public int getWaitingTime()
    {
        return waitingTime.get();
    }
    public void incrementWaitingTime() {
        this.waitingTime.incrementAndGet();
    }
    public void decrementServiceTime() {
        this.serviceTime.decrementAndGet();
    }
}