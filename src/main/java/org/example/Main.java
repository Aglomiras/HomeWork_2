package org.example;

import lombok.SneakyThrows;
import org.example.Model.DtoAidAgent;
import org.example.ServerAndClient.RawUdpSocketClient;
import org.example.ServerAndClient.RawUdpSocketServer;
import org.example.SupportPack.JsonUtils;

public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        RawUdpSocketServer server = new RawUdpSocketServer();
        server.start(1200);

        RawUdpSocketClient client = new RawUdpSocketClient();
        client.initialize(1200);

        DtoAidAgent dtoAidAgent = new DtoAidAgent("agent1", false);
        String strDto = JsonUtils.code(dtoAidAgent);

        PacketCreator packetCreator = new PacketCreator();
        byte[] data = packetCreator.collectPacket(strDto, 1200);

        while (true) {
            client.send(data);
            Thread.sleep(10000);
        }
    }
}