package org.example.ServerAndClient;

import com.google.gson.Gson;
import com.sun.jna.NativeLibrary;
import lombok.Data;
import lombok.SneakyThrows;
import org.example.Model.DtoAidAgent;
import org.example.Model.DtoListAidAgent;
import org.pcap4j.core.*;

import java.util.*;

@Data
public class RawUdpSocketServer {
    private long timeStamp;
    private DtoAidAgent aidAgent;
    private boolean flagLiveAgent = true; //Флаг для остановки принятия пакетов
    private List<DtoListAidAgent> dtoAidAgents = new ArrayList<>(); //Тот самый волшебный лист, куда записываются агенты отправители

    static {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            NativeLibrary.addSearchPath("wpcap", "C:\\Windows\\System32\\Npcap");
        }
    }

    @SneakyThrows
    public void start(int port) {
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
        PcapNetworkInterface networkInterface = null;
        for (PcapNetworkInterface allDev : allDevs) {
            if (allDev.getName().equals("\\Device\\NPF_Loopback")) {
                networkInterface = allDev;
                break;
            }
        }
        PcapHandle pcapHandle = networkInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 50);
        pcapHandle.setFilter("ip proto \\udp && dst port " + port, BpfProgram.BpfCompileMode.NONOPTIMIZE);
        runInThread(pcapHandle);
    }

    protected void runInThread(PcapHandle pcapHandle) {
        new Thread(() -> {
            grabPackets(pcapHandle);
        }).start();
    }

    protected void grabPackets(PcapHandle pcapHandle) {
        try {
            pcapHandle.loop(0, (PacketListener) packet -> {
                byte[] rawData = packet.getRawData();
                byte[] data = new byte[rawData.length - 32];

                System.out.println(Arrays.toString(rawData));
                System.arraycopy(rawData, 32, data, 0, data.length);
                String strDat = new String(data);
                System.out.println(new String(data).replace("\000", ""));

                /**
                 * https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.8
                 * */

                Gson jsonDataAgent = new Gson();
                aidAgent = jsonDataAgent.fromJson(strDat, DtoAidAgent.class);

                long time = System.currentTimeMillis();
                if (aidAgent != null) {

                    DtoListAidAgent aidList = new DtoListAidAgent(aidAgent.getNameAid(), aidAgent.isGuid(), time); //Создаем экземпляр DtoListAidAgent для отправляющего агента
                    boolean flagFound = false;

                    for (int i = 0; i < dtoAidAgents.size(); i++) {
                        DtoListAidAgent agentAid = dtoAidAgents.get(i);
                        if (agentAid.getNameAgent().equals(aidList.getNameAgent())) {
                            flagFound = true;
                            if (time > agentAid.getTimeStamp()) {
                                dtoAidAgents.set(i, aidList);
                            }
                            break;
                        }
                    }
                    if (!flagFound) {
                        dtoAidAgents.add(aidList); //Если в листе нет данного агента (тот, кто отправляет сообщения), то добавляет его
                    }
                    checkingLiveAgents();
                }

                if (!flagLiveAgent) {
                    try {
                        pcapHandle.breakLoop();
                    } catch (NotOpenException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (PcapNativeException | InterruptedException | NotOpenException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkingLiveAgents() {
        long currentTime = System.currentTimeMillis();
        Iterator<DtoListAidAgent> iterator = dtoAidAgents.iterator();
        /**
         * Проверка жизни агента по периоду отправки сообщения.
         * Если последнее сообщение от агента было 5 секунд назад, то он мертв.
         * */
        while (iterator.hasNext()) {
            DtoListAidAgent aidList = iterator.next();
            if (currentTime - aidList.getTimeStamp() >= 5000) {
                iterator.remove();
            }
        }
    }
}
