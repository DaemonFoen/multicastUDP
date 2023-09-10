package org.nsu;

import java.net.InetAddress;

public record Args(InetAddress ip, boolean sendMode, int port) {}
