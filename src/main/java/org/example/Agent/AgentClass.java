package org.example.Agent;

import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;
import org.example.ServerAndClient.UdpScheduledExecutor;

@Slf4j
public class AgentClass extends Agent {
    @Override
    protected void setup() {
        log.info("Was born " + this.getLocalName());
        AgentDetector agentDetector = new AgentDetector(1200); //Создание экземпляра класса агента
        UdpScheduledExecutor udpScheduledExecutor = new UdpScheduledExecutor(this, agentDetector); //Создание экземпляра класса эксикьютора
        udpScheduledExecutor.serverExecutorRunuble();
    }

    @Override
    public boolean isAlive() {
        return super.isAlive();
    }
}
