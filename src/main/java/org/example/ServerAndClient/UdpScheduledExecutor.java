package org.example.ServerAndClient;

import jade.core.Agent;
import org.example.Agent.AgentDetector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UdpScheduledExecutor {
    private Agent agent;
    private AgentDetector agentDetector;

    public UdpScheduledExecutor(Agent agent, AgentDetector agentDetector) {
        this.agent = agent;
        this.agentDetector = agentDetector;
    }

    public void serverExecutorRunuble() {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(3);

        /**
         * ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
         *
         * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html
         *
         * Creates and executes a periodic action that becomes enabled first after the given initial delay,
         * and subsequently with the given delay between the termination of one execution and the commencement of the next.
         * If any execution of the task encounters an exception, subsequent executions are suppressed. Otherwise,
         * the task will only terminate via cancellation or termination of the executor.
         * */

        service.scheduleWithFixedDelay(() -> {
            this.agentDetector.startPublishing(agent.getAID(), 1200);
        }, 100, 500, TimeUnit.MILLISECONDS);
        service.scheduleWithFixedDelay(() -> this.agentDetector.getActiveAgents(),
                1000, 2000, TimeUnit.MILLISECONDS);

        this.agentDetector.startDiscovering(1200);
    }
}
