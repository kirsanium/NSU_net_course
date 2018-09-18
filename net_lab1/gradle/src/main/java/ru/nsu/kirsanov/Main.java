package ru.nsu.kirsanov;


import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: MulticastApp multicastAddress");
            return;
        }

        String multicastAddress = args[0];
        MulticastApp multicastApp = new MulticastApp(multicastAddress);
        try {
            multicastApp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
