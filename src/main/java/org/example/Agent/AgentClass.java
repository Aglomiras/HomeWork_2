package org.example.Agent;

import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentClass extends Agent {
    @Override
    protected void setup() {
        log.info("Was born " + this.getLocalName());
        AgentDetector agentDetector = new AgentDetector(); //Создание экземпляра класса
    }
}
