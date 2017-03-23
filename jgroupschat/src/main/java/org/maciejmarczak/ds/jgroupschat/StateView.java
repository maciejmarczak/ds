package org.maciejmarczak.ds.jgroupschat;

import org.maciejmarczak.ds.jgroupschat.protos.ChatOperationProtos.ChatAction;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class StateView {

    private List<ChatAction> stateView = new LinkedList<>();

    synchronized void addClient(ChatAction action) {
        if (action.getAction() == ChatAction.ActionType.LEAVE)
            throw new IllegalArgumentException("addClient invoked with a LEAVE action");
        stateView.add(action);
    }

    synchronized void delClient(ChatAction action) {
        if (action.getAction() == ChatAction.ActionType.JOIN)
            throw new IllegalArgumentException("delClient invoked with a JOIN action");
        stateView.removeIf(a ->
                a.getChannel().equals(action.getChannel()) && a.getNickname().equals(action.getNickname())
        );
    }

    synchronized List<String> getAllChannels() {
        return stateView.stream()
                .map(ChatAction::getChannel)
                .collect(Collectors.toList());
    }

    synchronized List<String> getNicknamesForChannel(String channel) {
        return stateView.stream()
                .filter(a -> a.getChannel().equals(channel))
                .map(ChatAction::getNickname)
                .collect(Collectors.toList());
    }

    synchronized void setStateView(List<ChatAction> newStateView) {
        List<ChatAction> copiedView = new LinkedList<>();
        for (ChatAction action : newStateView) {
            copiedView.add(StateViewUtils.chatActionDeepCopy(action));
        }
        stateView = copiedView;
    }

    synchronized List<ChatAction> getStateView() {
        List<ChatAction> copiedView = new LinkedList<>();
        for (ChatAction action : stateView) {
            copiedView.add(StateViewUtils.chatActionDeepCopy(action));
        }
        return copiedView;
    }

    static class StateViewUtils {
        static ChatAction chatActionDeepCopy(ChatAction chatAction) {
            return ChatAction.newBuilder()
                    .setAction(chatAction.getAction())
                    .setChannel(chatAction.getChannel())
                    .setNickname(chatAction.getNickname())
                    .build();
        }
    }

    // USE ONLY FOR DEBUGGING !!!
    @Override
    synchronized public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ChatAction action : stateView) {
            sb
                    .append("ACTION: ")
                    .append(action.getNickname())
                    .append(" JOINED\n");
        }
        return sb.toString();
    }
}
