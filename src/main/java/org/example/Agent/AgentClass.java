package org.example.Agent;

import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentClass extends Agent {
    @Override
    protected void setup() {
        log.info("Я родился - " + this.getLocalName());

        AgentDetector agentDetector = new AgentDetector(1200,this);
        agentDetector.startPublishing(getAID(), 1200);
        agentDetector.startDiscovering(1200);
        agentDetector.getActiveAgents();
    }
}
