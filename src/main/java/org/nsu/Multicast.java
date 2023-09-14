package org.nsu;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import lombok.extern.log4j.Log4j2;


//FF00:BA98:7654:3210:FEDC:BA98:7654:3210
//224.0.0.1
@Log4j2
public class Multicast {
    public static void cast(Args args) {
        if (args.sendMode()){
            send(args);
        }else {
            receive(args);
        }
    }



    private static void send(Args args){
        Timer timer = new Timer();
        try (DatagramSocket socket = new DatagramSocket(null)) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        byte[] key = args.key().getBytes(StandardCharsets.UTF_8);
                        DatagramPacket packet = new DatagramPacket(key, key.length, args.ip(), args.port());
                        socket.send(packet);
                    } catch (IOException e) {
                        log.error("Ошибка при отправке пакета");
                        throw new RuntimeException(e);
                    }
                }
            };
            timer.schedule(task, 0, 500);
            while (true);
        } catch (SocketException e){
            throw new RuntimeException(e);
        }
    }

    private static void receive(Args args) {
        Set<InetAddress> addresses = new HashSet<>();
        Set<InetAddress> tmp = new HashSet<>();
        byte[] key = args.key().getBytes(StandardCharsets.UTF_8);
        byte[] buf = new byte[key.length];
        Timer timer = new Timer();
        try (MulticastSocket socket = new MulticastSocket(args.port())) {
            socket.joinGroup(new InetSocketAddress(args.ip(), 0), null);

            Thread thread = new Thread(() -> {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(packet);
                    } catch (IOException e) {
                        log.error("Ошибка при получении пакета");
                        throw new RuntimeException(e);
                    }
                    if (Arrays.equals(buf, key)){
                        tmp.add(packet.getAddress());
                    }
                }
            });
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if (!tmp.equals(addresses)){
                        System.out.println(tmp);
                        addresses.clear();
                        addresses.addAll(tmp);
                    }
                    tmp.clear();
                }
            };
            thread.start();
            timer.schedule(task, 0, 2000);
            thread.join();
        } catch (IOException | InterruptedException e) {
            log.error("Ошибка сокета в режиме приема пакетов");
            throw new RuntimeException(e);
        }
    }
}
