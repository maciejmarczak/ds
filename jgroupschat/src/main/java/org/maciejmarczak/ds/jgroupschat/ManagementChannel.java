package org.maciejmarczak.ds.jgroupschat;

import org.jgroups.JChannel;
import org.jgroups.ReceiverAdapter;
import org.jgroups.demos.Chat;
import org.maciejmarczak.ds.jgroupschat.protos.ChatOperationProtos;
import org.maciejmarczak.ds.jgroupschat.protos.ChatOperationProtos.ChatState;

import java.io.InputStream;
import java.io.OutputStream;

final class ManagementChannel {
    private static final String CHANNEL = "ChatManagement321123";

    private final String nickname;
    private final StateView stateView;

    private JChannel managementChannel;

    ManagementChannel(String nickname, StateView stateView) {
        this.nickname = nickname;
        this.stateView = stateView;
        init();
    }

    private void init() {
        managementChannel = ChannelUtils.getChannel(null);
        managementChannel.setName(nickname);
        managementChannel.setReceiver(new ManagementReceiver());
        try {
            managementChannel.connect(CHANNEL);
            managementChannel.getState(null, 10000);
            System.out.println("After init");
        } catch (Exception e) {
            System.out.println("ManagementChannel init failure: " + e.getMessage());
            System.exit(-1);
        }
    }

    void sendJoinMsg(String nickname, String channel) {
        try {
            managementChannel.send(ChannelUtils.buildChatActionMessage(nickname,
                    channel, ChatOperationProtos.ChatAction.ActionType.JOIN));
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
        }
    }

    void sendLeaveMsg(String nickname, String channel) {
        try {
            managementChannel.send(ChannelUtils.buildChatActionMessage(nickname,
                    channel, ChatOperationProtos.ChatAction.ActionType.LEAVE));
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
        }
    }

    private class ManagementReceiver extends ReceiverAdapter {

        @Override
        public void getState(OutputStream output) throws Exception {
            ChatState.newBuilder()
                    .addAllState(stateView.getStateView())
                    .build()
                    .writeTo(output);

            System.out.println("getState: " + stateView.getStateView().size() + ": " + stateView.toString());
        }

        @Override
        public void setState(InputStream input) throws Exception {
            stateView.setStateView(ChatState.parseFrom(input).getStateList());
            System.out.println("setState: " + stateView.getStateView().size() + ": " + stateView.toString());
        }
    }

}
