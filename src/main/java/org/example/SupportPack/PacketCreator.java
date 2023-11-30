package org.example.SupportPack;

import lombok.SneakyThrows;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class PacketCreator {
    @SneakyThrows
    public byte[] create(String data) {
        byte[] createByte = data.getBytes();
        /**Total Length*/
        int fullPacketLength = createByte.length + 28;
        byte[] packCreat = new byte[fullPacketLength + 4];

        /**
         * Выделение необходимой информации:
         * создадим переменные, которые должны будут содержать необходимую информацию, для передеачи в пакет;
         * */

        /**Family: IP(2) + Version + Differentiated Services*/
        byte[] oneByte = {2, 0, 0, 0, 69, 0};

        /**Source Address*/
        byte[] ipSourceBytes = InetAddress.getByName("192.168.56.1").getAddress();

        /**Destination Address*/
        byte[] ipDestinationBytes = InetAddress.getByName("255.255.255.255").getAddress();

        /**Source Port*/
        int sourcePort = 57742;
        byte sourcePortB = (byte) (sourcePort >> 8 & 255);
        byte sourcePortBc = (byte) (sourcePort & 255);

        /**Destination Port*/
        int destPort = 1200;
        byte destPortB = (byte) (destPort >> 8 & 255);
        byte destPortBc = (byte) (destPort & 255);

        /**Identification*/
        int indent = 30139;
        byte indentB = (byte) (indent >> 8 & 255);
        byte indentBc = (byte) (indent & 255);

        /**Time to Live + Protocol: UDP*/
        byte[] twoByte = {(byte) 128, 17};

        /**
         * Формирование пакета:
         * вносим байты в свои позиции в пакете;
         * */

        /**Add Family: IP(2) + Version + Differentiated Services*/
        for (int i = 0; i < oneByte.length; i++) {
            packCreat[i] = oneByte[i];
        }

        /**Add Total Length*/
        for (int i = 6, j = 6; i < 8; i++, j++) {
            packCreat[i] = longToBytes(fullPacketLength)[j];
        }

        /**Add Identification*/
        packCreat[8] = indentB;
        packCreat[9] = indentBc;

        /**Add Flag, Fragment Offset*/
        for (int i = 10, j = 6; i < 12; i++, j++) {
            packCreat[i] = longToBytes(0x00)[j];
        }

        /**Add Time to Live + Protocol: UDP*/
        for (int i = 12; i < twoByte.length + 12; i++) {
            packCreat[i] = twoByte[i - 12];
        }

        /**Add Header Checksum*/
        for (int i = 14, j = 6; i < 16; i++, j++) {
            packCreat[i] = longToBytes(0x00)[j];
        }

        /**Add Source Address*/
        for (int i = 16, j = 0; i < 20; i++, j++) {
            packCreat[i] = ipSourceBytes[j];
        }
        /**Add Destination Address*/
        for (int i = 20, j = 0; i < 24; i++, j++) {
            packCreat[i] = ipDestinationBytes[j];
        }

        /**Add Source Port*/
        packCreat[24] = sourcePortB;
        packCreat[25] = sourcePortBc;

        /**Add Destination Port*/
        packCreat[26] = destPortB;
        packCreat[27] = destPortBc;

        /**Add Length*/
        for (int i = 28, j = 6; i < 30; i++, j++) {
            packCreat[i] = longToBytes(fullPacketLength - 20)[j];
        }

        /**Add Checksum*/
        for (int i = 30, j = 6; i < 32; i++, j++) {
            packCreat[i] = longToBytes(0x0000)[j];
        }

        /**Итоговое формирование пакета*/
        System.arraycopy(createByte, 0, packCreat, 32, createByte.length);
        return packCreat;
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
}

