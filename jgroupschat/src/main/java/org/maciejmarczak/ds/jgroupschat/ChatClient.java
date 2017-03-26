package org.maciejmarczak.ds.jgroupschat;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.maciejmarczak.ds.jgroupschat.protos.ChatOperationProtos;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClient {

    private final StateView stateView = new StateView();
    private final Map<String, JChannel> channels = new HashMap<>();

    private String nickname;
    private ManagementChannel managementChannel;
    private boolean disconnect = false;

    private void init() throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        // before proceeding, set username
        nickname = TerminalUtils.readLine("Username");
        managementChannel = new ManagementChannel(nickname, stateView);

        while (!disconnect) {
            String line = TerminalUtils.readLine();

            if (line == null || line.split(" ", 2).length < 2) {
                continue;
            }

            String[] tokens = line.split(" ", 2);
            String cmd = tokens[0], arg = tokens[1];

            switch (cmd) {
                case "j":
                    joinChannel(arg);
                    break;
                case "l":
                    leaveChannel(arg);
                    break;
                case "s":
                    sendMessage(arg);
                    break;
                case "c":
                    listChannels();
                    break;
                case "n":
                    listChannelsWithClients();
                    break;
                case "q":
                    return;
            }
        }
    }

    private void listChannelsWithClients() {
        List<String> channels = stateView.getAllChannels();

        for (String channel : channels) {
            List<String> nicknames = stateView.getNicknamesForChannel(channel);

            System.out.println("Channel " + channel + ":");
            for (String nickname: nicknames) {
                System.out.println(nickname);
            }
            System.out.println();
        }
        System.out.println();
    }

    private void listChannels() {
        System.out.println("All channels:");
        for (String channel : stateView.getAllChannels()) {
            System.out.println(channel);
        }
        System.out.println();
    }

    private void joinChannel(String channelName) {
        if (channels.containsKey(channelName)) {
            System.out.println("Already connected to " + channelName);
            return;
        }

        try {
            InetAddress address = InetAddress.getByName(channelName);

            JChannel channel = ChannelUtils.getChannel(address);
            channel.setReceiver(new ChannelUtils.ChannelReceiver(channelName));
            channel.setName(nickname);
            channel.connect(channelName);

            managementChannel.sendJoinMsg(nickname, channelName);
            channels.put(channelName, channel);
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
            System.exit(-1);
        }
    }

    private void leaveChannel(String channelName) {
        if (!channels.containsKey(channelName)) {
            System.out.println("Cannot leave from channel " + channelName);
            return;
        }

        managementChannel.sendLeaveMsg(nickname, channelName);
        channels.get(channelName).close();
        channels.remove(channelName);
    }

    private void sendMessage(String msg) {
        ChatOperationProtos.ChatMessage chatMessage = ChatOperationProtos.ChatMessage
                .newBuilder().setMessage(msg).build();

        Message message = new Message(null, null, chatMessage.toByteArray());

        try {
            for (JChannel channel : channels.values()) {
                channel.send(message);
            }
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) throws Exception {
        new ChatClient().init();
    }
}
