package org.example.Agent;

import jade.core.AID;
import jade.core.Agent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.Model.DtoAidAgent;
import org.example.Model.DtoListAidAgent;
import org.example.ServerAndClient.RawUdpSocketClient;
import org.example.ServerAndClient.RawUdpSocketServer;
import org.example.SupportPack.JsonUtils;
import org.example.SupportPack.PacketCreator;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class AgentDetector implements AgentDetectorInterface {
    private Agent agent;
    private RawUdpSocketServer rawUdpSocketServer;
    private RawUdpSocketClient rawUdpSocketClient;
    private int port;
    private boolean flagLiveAgent = true;
    Map<AID, Date> agents;
    private final ScheduledExecutorService serverEx;
    private ScheduledFuture<?> serverPublished;
    private ScheduledFuture<?> serverDiscover;

    public AgentDetector(int port, Agent agent) {
        this.port = port;
        this.rawUdpSocketClient = new RawUdpSocketClient();
        this.rawUdpSocketServer = new RawUdpSocketServer();
        this.agent = agent;
        this.serverEx = Executors.newScheduledThreadPool(3);
        this.agents = new HashMap<>();
    }

    @Override
    public void startPublishing(AID aid, int port) {
        DtoAidAgent agentDTO = new DtoAidAgent(aid.getLocalName(), true); //Создает объект DtoAidAgent
        String code = JsonUtils.code(agentDTO); //Кодируем объект через JsonUtils

        PacketCreator packetCreator = new PacketCreator(); //Создаем объект создания пакета
        byte[] bytes = packetCreator.create(code); //Формируем пакет (массив байт), через метод create

        rawUdpSocketClient.initialize(port); //Инициализация
        System.out.println(agent.getLocalName()); //Смотрим отправляемый пакет

        /**
         * ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
         *
         * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ScheduledExecutorService.html
         *
         * Создает и выполняет периодическое действие, которое становится включенным сначала после заданной
         * начальной задержки, а затем с заданной задержкой между завершением одного выполнения и началом следующего.
         * Если какое-либо выполнение задачи сталкивается с исключением, последующие выполнения подавляются.
         * В противном случае выполнение задачи будет завершено только путем отмены или прекращения деятельности исполнителя.
         * */

        log.info("Начало отправки пакетов - " + agent.getLocalName());
        /**
         * Поток по отправке пакетов
         * */
        serverPublished = serverEx.scheduleWithFixedDelay(() -> {
                    if (!agent.isAlive()) { //Жив ли агент?
                        flagLiveAgent = false;
                        /**
                         * Для правильного завершения работы ExecutorService у нас есть API-интерфейсы shutdown() и shutdownNow().
                         * Метод shutdown() не приводит к немедленному уничтожению ExecutorService.
                         * Это заставит ExecutorService прекратить прием новых задач и завершит работу после того, как все запущенные потоки
                         * завершат свою текущую работу
                         * */
                        serverEx.shutdown();
                        stopPublishing(); //Останавливаем поток
                    }
                    rawUdpSocketClient.send(bytes);
                },
                1000, 2000, TimeUnit.MILLISECONDS);
    }

    public void stopPublishing() {
        /**
         * Пытается отменить выполнение этой задачи. Этот метод не имеет эффекта, если задача уже завершена или отменена,
         * или не может быть отменена по какой-либо другой причине. В противном случае, если эта задача не была запущена при вызове cancel,
         * эта задача никогда не должна запускаться.
         * */
        serverPublished.cancel(true);
        flagLiveAgent = false; //В этом случае агент не убит, а просто остановил отправку сообщений
        log.info("Остановка отправки пакетов - " + agent.getLocalName());
    }

    public void startDiscovering(int port) {
        log.info("Открытие сервера для приема сообщений - " + agent.getLocalName());
        serverDiscover = serverEx.schedule(() -> {
            if (!agent.isAlive()) { //Жив ли агент?
                flagLiveAgent = false;
                /**
                 * Метод shutdown() не приводит к немедленному уничтожению ExecutorService.
                 * */
                serverEx.shutdown();
                stopDiscovering(); //Останавливаем поток
            }
            this.rawUdpSocketServer.start(port);
        }, 0, TimeUnit.MILLISECONDS);
    }

    public void stopDiscovering() {
        /**
         * Пытается отменить выполнение этой задачи.
         * */
        serverDiscover.cancel(true);
        flagLiveAgent = false; //В этом случае агент не убит, а просто остановил принятие сообщений
        log.info("Остановка принятия пакетов - " + agent.getLocalName());
    }

    public List<AID> getActiveAgents() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        List<AID> result = new ArrayList<>();
        executorService.scheduleAtFixedRate(() -> {
                    if (!agent.isAlive()) { //Жив ли агент?
                        flagLiveAgent = false;
                        /**
                         * Метод shutdown() не приводит к немедленному уничтожению ExecutorService.
                         * */
                        executorService.shutdown();
                    }
                    List<DtoListAidAgent> AIDList = rawUdpSocketServer.getDtoAidAgents(); //Возврат листа из сервера
                    result.clear(); //Удаляем лист для перезаписи
                    for (DtoListAidAgent aidDataList : AIDList) { //Перезапись
                        result.add(new AID(aidDataList.getNameAgent(), aidDataList.isGuid()));
                    }
                    System.out.println(result + " " + agent.getLocalName());
                },
                1000, 5000, TimeUnit.MILLISECONDS);
        return result;
    }

}
