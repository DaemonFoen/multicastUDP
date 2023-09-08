package org.nsu;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import lombok.extern.log4j.Log4j2;

//FF00:BA98:7654:3210:FEDC:BA98:7654:3210
//
@Log4j2
public class Multicast {


    public static void cast(Args args) {
        Timer timer = new Timer();
        List<InetAddress> addresses = new ArrayList<>();
        byte[] buf = new byte[256];
        if (args.sendMode()){
            if (args.ip() instanceof Inet4Address){
                try (MulticastSocket socket = new MulticastSocket()){
                    socket.joinGroup(new InetSocketAddress(args.ip(),40000), NetworkInterface.getNetworkInterfaces().nextElement());
                    DatagramPacket packet = new DatagramPacket(new byte[0],0);
                    packet.setAddress(Inet4Address.getLocalHost());
                    packet.setPort(40000);
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("Пакет готовится");
                                socket.send(packet);
                                System.out.println("Пакет отправлен");
                            } catch (IOException e) {
                                log.error("Ошибка при отправке пакета");
                                throw new RuntimeException(e);
                            }
                        }
                    };
                    timer.schedule(task,0,1000);
                    while (true);
                } catch (IOException e){
                    log.error("Ошибка сокета при отправке пакета");
                    throw new RuntimeException(e);
                }
            }
        }else{
            if(args.ip() instanceof Inet4Address){
                try (MulticastSocket socket = new MulticastSocket()) {
                    socket.joinGroup(new InetSocketAddress(args.ip(),40000), NetworkInterface.getNetworkInterfaces().nextElement());
//                    Thread thread = new Thread(() -> {
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        try {
                            System.out.println("Ловлю пакет");
                            socket.receive(packet);
                            System.out.println("Поймал пакет");
                        } catch (IOException e) {
                            log.error("Ошибка при получении пакета");
                            throw new RuntimeException(e);
                        }
                        System.out.println("Добавляю адрес");
                        addresses.add(packet.getAddress());
                        try {
                            addresses.add(InetAddress.getByName("255.255.255.255"));
                        } catch (UnknownHostException e) {
                            log.error("Ошибка при добавлении нового адреса");
                            throw new RuntimeException(e);
                        }
//                    });
                    //TODO реализовать удаление;
                    //TODO Че за х происходит с тредом при сокете?
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println(addresses);
                        }
                    };
//                    thread.start();
                    timer.schedule(task,0,2000);
//                    thread.join();
                    System.out.println("join");
                } catch (IOException e) {
                    log.error("Ошибка сокета в режиме приема пакетов");
                    throw new RuntimeException(e);
                }
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
            }
        }
    }
}
