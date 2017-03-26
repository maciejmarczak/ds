package org.maciejmarczak.ds.jgroupschat;

import com.google.protobuf.InvalidProtocolBufferException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import org.maciejmarczak.ds.jgroupschat.protos.ChatOperationProtos;
import org.maciejmarczak.ds.jgroupschat.protos.ChatOperationProtos.ChatAction;
import org.maciejmarczak.ds.jgroupschat.protos.ChatOperationProtos.ChatAction.ActionType;

import java.net.InetAddress;

class ChannelUtils {

    static JChannel getChannel(InetAddress address) {
        JChannel channel = new JChannel(address == null);
        ProtocolStack stack = getProtocolStack(address);
        channel.setProtocolStack(stack);

        try {
            stack.init();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return channel;
    }

    static Message buildChatActionMessage(String nickname, String channel, ActionType actionType) {
        ChatAction action = ChatAction.newBuilder()
                .setNickname(nickname)
                .setChannel(channel)
                .setAction(actionType)
                .build();

        return new Message(null, null, action.toByteArray());
    }

    static class ChannelReceiver extends ReceiverAdapter {
        private final String channelName;

        ChannelReceiver(String channelName) {
            this.channelName = channelName;
        }

        @Override
        public void receive(Message msg) {

            String messageText = null;
            try {
                messageText = ChatOperationProtos.ChatMessage
                        .parseFrom(msg.getBuffer()).getMessage();
            } catch (InvalidProtocolBufferException ipbe) {
                System.out.println("Fatal error: " + ipbe.getMessage());
                System.exit(-1);
            }

            String sender = msg.getSrc().toString();

            System.out.printf("%s at %s: %s\n", sender, channelName, messageText);
        }
    }

    private static ProtocolStack getProtocolStack(InetAddress address) {
        ProtocolStack stack = new ProtocolStack();

        UDP udp = new UDP();
        if (address != null) {
            udp.setValue("mcast_group_addr", address);
        }

        stack
                .addProtocol(udp)
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL()
                        .setValue("timeout", 12000)
                        .setValue("interval", 3000)
                )
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new FLUSH());

        return stack;
    }
}
