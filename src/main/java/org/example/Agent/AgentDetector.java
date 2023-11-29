package org.example.Agent;

import jade.core.AID;
import jade.core.Agent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.Model.DtoAidAgent;
import org.example.SupportPack.PacketCreator;
import org.example.ServerAndClient.RawUdpSocketClient;
import org.example.ServerAndClient.RawUdpSocketServer;
import org.example.SupportPack.JsonUtils;

import java.util.List;

@Slf4j
@Data
public class AgentDetector implements AgentDetectorInterface {
    private Agent agent;
    private RawUdpSocketServer rawUdpSocketServer;
    private RawUdpSocketClient rawUdpSocketClient;
    private int port;

    public AgentDetector(int port) {
        this.port = port;
        this.rawUdpSocketClient = new RawUdpSocketClient();
        rawUdpSocketClient.initialize(this.port);
        this.rawUdpSocketServer = new RawUdpSocketServer();
    }

    @Override
    public void startPublishing(AID aid, int port) {
        System.out.println("88888");
        System.out.println("88888" + agent.isAlive());
        if (agent.isAlive()) {
            System.out.println("77777");
            DtoAidAgent dtoAidAgent = new DtoAidAgent(aid.getLocalName(), false);
            String strDto = JsonUtils.code(dtoAidAgent);

            PacketCreator packetCreator = new PacketCreator();
            byte[] data = packetCreator.collectPacket(strDto, port);
            this.rawUdpSocketClient.send(data);
        } else {
            this.rawUdpSocketServer.setFlagLiveAgent(false);
            log.info("Сервер упал: " + agent.getLocalName());
        }
    }

    @Override
    public void startDiscovering(int port) {
        this.rawUdpSocketServer.start(this.port);
    }

    @Override
    public List<AID> getActiveAgents() {


        return null;
    }

//    public void checkLiveAgent() {
//        System.out.println("09090");
//    }
}
