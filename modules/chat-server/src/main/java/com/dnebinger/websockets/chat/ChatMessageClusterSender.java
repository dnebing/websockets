package com.dnebinger.websockets.chat;

/**
 * class ChatMessageClusterSender: Sends the given chat message to the cluster nodes.
 *
 * @author dnebinger
 */
public interface ChatMessageClusterSender {

    /**
     * sendChatMessage: Sends the chat message details to the cluster.
     * @param username User who sent the message.
     * @param message Message that was sent.
     */
    void sendChatMessage(final String username, final String message);
}
