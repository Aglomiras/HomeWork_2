package org.example.ServerAndClient;

import lombok.SneakyThrows;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpSocketClient {
    private DatagramSocket client;
    private int port;
    @SneakyThrows
    public void initialize(int port) {
        client = new DatagramSocket();
        this.port = port;
    }

    @SneakyThrows
    public void send(String data) {
        DatagramPacket p = new DatagramPacket(
                data.getBytes(),
                data.getBytes().length,
                InetAddress.getLocalHost(),
                port); //Формируем пакет
        client.send(p); //Отправка пакета
    }
}
