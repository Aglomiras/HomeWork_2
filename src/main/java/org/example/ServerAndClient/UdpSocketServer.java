package org.example.ServerAndClient;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpSocketServer {
    private byte[] packetData = new byte[500];
    private Thread recThread;

    @SneakyThrows
    public void start(int port) { //Класс, для принятия номера порта, который необходимо прослушивать
        recThread = new Thread(() -> { //Создание потока ,чтбы не занимать главный поток
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(port);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            DatagramPacket packet = new DatagramPacket(packetData, packetData.length); //Определяем размер пакета (закладываем максимальный объем)

            while (true) { //Цикл нужен, чтобы принимать пакеты постоянно (все время, всегда, а не принять только один и остановиться)
                try {
                    socket.receive(packet); //Использование метода по началу получения данных
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                byte[] data = packet.getData();
                String s = new String(data).replace("\000", ""); //Уменьшение длины пакета до количества передаваемых значений
                System.out.println("Server received: " + s);
            }
        });
        recThread.start();
    }

    public void stop() {
    }
}
