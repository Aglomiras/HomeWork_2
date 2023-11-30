package org.example.ServerAndClient;

import lombok.Data;
import lombok.SneakyThrows;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.util.List;

@Data
public class RawUdpSocketClient {
    private PcapHandle pcapHandle;
    private boolean flagLiveAgent = true; //Флаг для остановки отпраки пакетов

    @SneakyThrows
    public void send(byte[] data) {
        if (flagLiveAgent) {
            pcapHandle.sendPacket(data);
        }
    }

    @SneakyThrows
    public void initialize(int port) {
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
        PcapNetworkInterface networkInterface = null;
        for (PcapNetworkInterface allDev : allDevs) {
            if (allDev.getName().equals("\\Device\\NPF_Loopback")) {
                networkInterface = allDev;
                break;
            }
        }
        pcapHandle = networkInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
    }
}
