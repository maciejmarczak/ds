package org.maciejmarczak.ds.jgroupschat;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
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
