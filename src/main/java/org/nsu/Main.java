package org.nsu;

public class Main {

    public static void main(String[] args) {
        Args arg = CLI.parse(args);
        Multicast.cast(arg);
    }

}