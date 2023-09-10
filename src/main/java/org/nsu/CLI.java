package org.nsu;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {

    private static final Options options = new Options();

    static {
        options.addOption("m", "mode", true, "Режим отправки пакетов (s для отправки, r для получения)");
        options.addOption("i", "ip", true, "IP адресс multicast группы");
        options.addOption("p", "port", true, "Порт сервера");
        options.addOption("h", "help", false, "...");
    }

    public static Args parse(String[] args) {
        CommandLine cmd;
        try {
            cmd = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String modeOption;
        InetAddress ipOption;
        int port;
        if (cmd.hasOption("help")) {
            System.out.println(usage());
            System.exit(0);
        }
        if (cmd.hasOption("m")) {
            modeOption = cmd.getOptionValue("m");
            if (!modeOption.equals("s") && !modeOption.equals("r")) {
                throw new RuntimeException("Неизвестный аргумент для опции m");
            }
        } else {
            throw new RuntimeException("Нет обязательной опции m");
        }
        if (cmd.hasOption("p")) {
            port = Integer.parseInt(cmd.getOptionValue("p"));
            if (port <= 0) {
                throw new RuntimeException("Неверный номер порта");
            }
        }else {
            throw new RuntimeException("Нет обязательной опции p");
        }
        if (cmd.hasOption("i")) {
            try {
                ipOption = InetAddress.getByName(cmd.getOptionValue("i"));
                if (!ipOption.isMulticastAddress()) {
                    throw new RuntimeException("Указанный ip адресс не является адресом multicast группы");
                }
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Нет обязательной опции i");
        }
        return new Args(ipOption, modeOption.equals("s"), port);
    }

    public static String usage() {
        HelpFormatter formatter = new HelpFormatter();
        StringWriter stringWriter = new StringWriter();
        formatter.printHelp(new PrintWriter(stringWriter), 250, "multicast", null,
                options, formatter.getLeftPadding(), formatter.getDescPadding(), null, true);
        return stringWriter.toString();
    }
}