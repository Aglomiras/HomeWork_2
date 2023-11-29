package org.example;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class PacketCreator {
//    [2, 0, 0, 0, 69, 0, 0, (byte)35, (byte)117, 57, (byte)159, 0, 0, 80, 11, 0, 0, 3, 2, 72, 10, 3, 2, 72, -12, 85, 4, -80, 0, 16, 121, 9, 104, 101, 108, 108, 111, 32, 49, 55]
//hello 17
//    {2, 0, 0, 0, 69, 0, 0, 36, 117, 91, 0, 0, -128, 17, 0, 0, 10, 3, 2, 72, 10, 3, 2, 72, -12, 85, 4, -80, 0, 16, 121, 9, 104, 101, 108, 108, 111, 32, 49, 55}
    //[2, 0, 0, 0, 69, 0, 0, 36, 117, 91, 0, 0, -128, 17, 0, 0, 10, 3, 2, 72, 10, 3, 2, 72, -12, 85, 4, -80, 0, 16, 121, 9]

    public byte[] creator(String data) {
        byte[] dataCreat = new byte[500];
        byte[] dataUdp = new byte[]{2, 0, 0, 0, 69, 0, 0, 36, 117, 91, 0, 0, -128, 17, 0, 0, 10, 3, 2, 72, 10, 3, 2, 72, -12, 85, 4, -80, 0, 16, 121, 9};
        byte[] strData = data.getBytes();

        return dataUdp;
    }

    public byte[] create(String data) {
        int len = data.getBytes().length + 20 + 8;
        byte len1 = (byte) (len >> 8 & 255);
        byte len2 = (byte) (len & 255);

        List<Byte> bytes = new ArrayList<>();
        byte[] start = {2, 0, 0, 0, 69, 0, len1, len2, (byte) 195, (byte) 164, 0, 0, (byte) 128, 17, 0, 0,
                (byte) 172, (byte) 20, (byte) 10, (byte) 9,
                (byte) 172, (byte) 20, (byte) 10, (byte) 9};
        for (byte b : start) {
            bytes.add(b);
        }
        System.out.println("bytes: " + bytes);

        List<Byte> UDPbytes = new ArrayList<>();
        int dPort = 56878;
        byte dPortb1 = (byte) (dPort >> 8 & 255);
        bytes.add(dPortb1);


        byte dPortb2 = (byte) (dPort & 255);
        bytes.add(dPortb2);
        System.out.println("dPortb2: " + dPortb2);

        System.out.println("dPortb1: " + dPort + " " + (((dPortb1 & 0xFF) << 8) + (dPortb2 & 0xFF)));

        int sPort = 1200;
        byte sPortb1 = (byte) (sPort >> 8 & 255);
        bytes.add(sPortb1);
        int val = ((sPortb1 & 0xFF));
        System.out.println("sPortb1: " + val);

        byte sPortb2 = (byte) (sPort & 255);
        bytes.add(sPortb2);
        System.out.println("sPortb2:" + sPortb2);

        int length = data.getBytes().length + 8;
        System.out.println("length: " + length);

        byte length1 = (byte) (length >> 8 & 255);
        bytes.add(length1);
        System.out.println("length1: " + length1);

        byte length2 = (byte) (length & 255);
        bytes.add(length2);
        System.out.println("length2: " + length2);

        bytes.add((byte) 0);
        bytes.add((byte) 0);

        System.out.println("bytes: " + bytes);

        for (byte aByte : data.getBytes()) {
            bytes.add(aByte);
        }
        System.out.println("bytes: " + bytes);

        byte[] massByte = new byte[bytes.size()];
        int i = 0;
        for (Byte aByte : bytes) {
            massByte[i] = aByte;
            i++;
        }
        System.out.println("massByte: " + massByte);

        return massByte;
    }

    private String iface = "";

    @SneakyThrows
    public byte[] collectPacket(String data, int portToSend) {
        byte[] platformDescBytes = data.getBytes();
        int dataLength = platformDescBytes.length;
        int totalLength = dataLength + 28;

        byte[] packet = new byte[totalLength + (iface.equals("\\Device\\NPF_Loopback") ? 4 : 14)];
        /* send to all*/
        byte[] ipDestinationBytes = InetAddress.getByName("255.255.255.255").getAddress();
        byte[] ipSourceBytes = InetAddress.getByName("127.0.0.1").getAddress();

        /* set NPF_Loopback as iface to use. WORKS ONLY FOR WINDOWS*/
        for (int i = 0, j = 7; i < 1; i++, j++) {
            packet[i] = longToBytes(0x02)[j];
        }

        //Header Length = 20 bytes
        for (int i = 4, j = 7; i < 5; i++, j++) {
            packet[i] = longToBytes(69)[j];
        }
        //Differentiated Services Field
        for (int i = 5, j = 7; i < 6; i++, j++) {
            packet[i] = longToBytes(0x00)[j];
        }
        //Total Length
        for (int i = 6, j = 6; i < 8; i++, j++) {
            packet[i] = longToBytes(totalLength)[j];
        }
        //Identification - for fragmented packages
        for (int i = 8, j = 6; i < 10; i++, j++) {
            packet[i] = longToBytes(33500)[j];
        }
        //Flag, Fragment Offset - for fragmented packages
        for (int i = 10, j = 6; i < 12; i++, j++) {
            packet[i] = longToBytes(0x00)[j];
        }
        //Time to Live - max limit for moving through the network
        for (int i = 12, j = 7; i < 13; i++, j++) {
            packet[i] = longToBytes(128)[j];
        }
        //Protocol - UDP
        for (int i = 13, j = 7; i < 14; i++, j++) {
            packet[i] = longToBytes(17)[j];
        }
        //Header Checksum, can be 0x00 if it is not calculated
        for (int i = 14, j = 6; i < 16; i++, j++) {
            packet[i] = longToBytes(0x00)[j];
        }

        for (int i = 16, j = 0; i < 20; i++, j++) {
            packet[i] = ipSourceBytes[j];
        }
        for (int i = 20, j = 0; i < 24; i++, j++) {
            packet[i] = ipDestinationBytes[j];
        }
        //Source port
        for (int i = 24, j = 6; i < 26; i++, j++) {
            packet[i] = longToBytes(portToSend)[j];
        }
        //Destination port
        for (int i = 26, j = 6; i < 28; i++, j++) {
            packet[i] = longToBytes(portToSend)[j];
        }
        //Length
        int length = totalLength - 20;

        for (int i = 28, j = 6; i < 30; i++, j++) {
            packet[i] = longToBytes(length)[j];
        }
        //Checksum, can be 0x00 if it is not calculated
        for (int i = 30, j = 6; i < 32; i++, j++) {
            packet[i] = longToBytes(0x0000)[j];
        }
        //Data
        System.arraycopy(platformDescBytes, 0, packet, 32, platformDescBytes.length);
        return packet;
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
}

