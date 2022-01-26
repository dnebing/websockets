package com.dnebinger.websockets.chat;

/**
 * class ChatMessageClusterConstants: Constants for the cluster messaging.
 *
 * @author dnebinger
 */
public class ChatMessageClusterConstants {

    /**
     * The Liferay Message Bus destination that the chat messages will be published to
     */
    public static final String DESTINATION = "websocket/chatMessage";
}
