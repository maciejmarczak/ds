package org.maciejmarczak.ds.jgroupschat;

import java.util.Scanner;

public class ChatClient {

    private final StateView stateView = new StateView();
    private String nickname;

    private void init() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        // before proceeding, set username
        nickname = TerminalUtils.readLine("Username");
        ManagementChannel managementChannel = new ManagementChannel(nickname, stateView);
    }

    public static void main(String[] args) throws Exception {
        new ChatClient().init();

        Thread t = new Thread(() -> {
            int num = 0;
            while (!(num == 50)) {
                num = (int)(Math.random() * 5000);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        });

        t.start();
        t.join();
    }
}
