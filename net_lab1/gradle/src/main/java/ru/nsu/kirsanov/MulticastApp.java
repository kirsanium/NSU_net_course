package ru.nsu.kirsanov;

import java.io.Console;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalDateTime;
import java.util.LinkedList;

public class MulticastApp {

    public static final int MULTICAST_PORT_NUMBER = 6789;
    private final String HELLO_MESSAGE = "hello";
    private final String BYE_MESSAGE = "bye";
    private final String CHECK_MESSAGE = "check";
    private final String OK_MESSAGE = "ok";
    private final long SLEEP_TIME = 1000;
    private MulticastSocket multicastSocket = null;
    private String multicastAddress;
    private InetAddress groupAddress;
    private byte[] buffer = new byte[256];
    private LinkedList<ClientInfo> clientInfoList = new LinkedList<>();
    private Boolean toExit = false;

    public MulticastApp(String multicastAddress) {
        this.multicastAddress = multicastAddress;
    }

    private void init() throws IOException {
        multicastSocket = new MulticastSocket(MULTICAST_PORT_NUMBER);
        multicastSocket.setLoopbackMode(true);
        groupAddress = InetAddress.getByName(multicastAddress);
    }

    private void printIPAddressesList() {
        System.out.println("Clients online at " + LocalDateTime.now());
        for (ClientInfo clientInfo : clientInfoList) {
            System.out.println(clientInfo.getInetAddress().getHostAddress());
        }
        System.out.println();
    }

    class MulticastChecker extends Thread {

        private void multicastSendMessage(String message) throws IOException {
            DatagramPacket toSend = new DatagramPacket(message.getBytes(), message.length(), groupAddress, MULTICAST_PORT_NUMBER);
            multicastSocket.send(toSend);
        }

        private void incrementAllTimeoutCounters() {
            for (ClientInfo clientInfo : clientInfoList) {
                clientInfo.incrementTimeoutCounter();
            }
        }

        private LinkedList<ClientInfo> getListOfTimeoutedClients() {
            LinkedList<ClientInfo> timeoutedClientsList = new LinkedList<>();
            for (ClientInfo clientInfo : clientInfoList) {
                if (clientInfo.getTimeoutCounter() >= 10)
                    timeoutedClientsList.add(clientInfo);
            }
            return timeoutedClientsList;
        }

        @Override
        public void run() {
            try {
                multicastSocket.joinGroup(groupAddress);
                multicastSendMessage(HELLO_MESSAGE);
                while (!toExit) {
                    sleep(SLEEP_TIME);
                    multicastSendMessage(CHECK_MESSAGE);
                    incrementAllTimeoutCounters();
                    for (ClientInfo timeoutedClient : getListOfTimeoutedClients()) {
                        ClientInfo existingSenderInfo = clientInfoList.get(clientInfoList.indexOf(timeoutedClient));
                        clientInfoList.remove(existingSenderInfo);
                        printIPAddressesList();
                    }
                }
                multicastSendMessage(BYE_MESSAGE);
            }
            catch (Exception e) {
                e.printStackTrace();
                toExit = true;
            }
        }
    }

    class MulticastResponder extends Thread {

        private DatagramPacket multicastReceivePacket() {
            DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
            try {
                multicastSocket.receive(incomingPacket);
            } catch (IOException e) {
                return null;
            }
            return incomingPacket;
        }

        private void multicastSendMessage(String message) throws IOException {
            DatagramPacket toSend = new DatagramPacket(message.getBytes(), message.length(), groupAddress, MULTICAST_PORT_NUMBER);
            multicastSocket.send(toSend);
        }

        @Override
        public void run() {
            while (!toExit) {
                try {
                    DatagramPacket incomingPacket = multicastReceivePacket();
                    if (incomingPacket == null) break;
                    String received = new String(incomingPacket.getData(), 0, incomingPacket.getLength());
                    ClientInfo senderInfo = new ClientInfo(incomingPacket.getAddress());
                    switch (received) {
                        case HELLO_MESSAGE:
                            if (!clientInfoList.contains(senderInfo)) {
                                clientInfoList.add(senderInfo);
                                printIPAddressesList();
                            }
                            multicastSendMessage(OK_MESSAGE);
                            break;

                        case CHECK_MESSAGE:
                            multicastSendMessage(OK_MESSAGE);
                            break;

                        case OK_MESSAGE:
                            if (clientInfoList.indexOf(senderInfo) == -1) {
                                clientInfoList.add(senderInfo);
                                printIPAddressesList();
                            }
                            ClientInfo existingSenderInfo = clientInfoList.get(clientInfoList.indexOf(senderInfo));
                            existingSenderInfo.resetTimeoutCounter();
                            break;

                        case BYE_MESSAGE:
                            if (clientInfoList.contains(senderInfo)) {
                                clientInfoList.remove(senderInfo);
                                printIPAddressesList();
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    toExit = true;
                    return;
                }
            }
        }
    }

    public void start() throws IOException {
        init();
        MulticastChecker multicastChecker = new MulticastChecker();
        MulticastResponder multicastResponder = new MulticastResponder();
        multicastChecker.start();
        multicastResponder.start();
        Console console = System.console();
        while (true) {
            String input = console.readLine();
            if (input.compareToIgnoreCase("exit") == 0) {
                toExit = true;
                break;
            }
        }
        try {
            multicastChecker.join();
            multicastSocket.close();
            multicastResponder.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
