package org.nsu;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) throws IOException {
        Args arg = CLI.parse(args);
        Multicast.cast(arg);
    }
}