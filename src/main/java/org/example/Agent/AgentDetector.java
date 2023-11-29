package org.example.Agent;

import jade.core.AID;
import org.example.Model.DtoAidAgent;
import org.example.ServerAndClient.RawUdpSocketClient;
import org.example.ServerAndClient.RawUdpSocketServer;

import java.util.List;

public class AgentDetector implements AgentDetectorInterface {
    private RawUdpSocketServer rawUdpSocketServer;
    private RawUdpSocketClient rawUdpSocketClient;
    @Override
    public void startPublishing(AID aid, int port) {
        DtoAidAgent dtoAidAgent = new DtoAidAgent(aid.getLocalName(), false);

    }

    @Override
    public void startDiscovering(int port) {

    }

    @Override
    public List<AID> getActiveAgents() {
        return null;
    }

}
