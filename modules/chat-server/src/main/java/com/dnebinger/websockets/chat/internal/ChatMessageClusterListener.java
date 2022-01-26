package com.dnebinger.websockets.chat.internal;

import com.dnebinger.websockets.chat.ChatEndpoint;
import com.dnebinger.websockets.chat.ChatMessage;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import org.osgi.service.component.annotations.Component;

/**
 * class ChatMessageClusterListener: This is the liferay message bus listener for messages on the chat cluster
 * message destination. When a message is received, it is passed off to the ChatEndpoint's broadcast() method
 * to forward to all sessions this node has open.
 *
 * @author dnebinger
 */
@Component(
        immediate = true,
        service = MessageListener.class
)
public class ChatMessageClusterListener extends BaseMessageListener {
    @Override
    protected void doReceive(Message message) throws Exception {
        // convert to a chat message
        ChatMessage chatMessage = new ChatMessage(message.getString("from"), message.getString("chatMessage"));

        // broadcast it
        ChatEndpoint.broadcast(chatMessage);
    }
}
