package com.dnebinger.websockets.chat.internal;

import com.dnebinger.websockets.chat.ChatMessageClusterConstants;
import com.dnebinger.websockets.chat.ChatMessageClusterSender;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.Portal;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.net.InetAddress;

/**
 * class ChatMessageClusterSenderImpl: This is a class used to send the chat message off to the cluster for notification
 * on the remaining nodes.
 *
 * @author dnebinger
 */
@Component(
        immediate = true,
        service = ChatMessageClusterSender.class
)
public class ChatMessageClusterSenderImpl implements ChatMessageClusterSender {
    /**
     * sendChatMessage: Sends the chat message details to the cluster.
     *
     * @param username User who sent the message.
     * @param chatMessage  Message that was sent.
     */
    @Override
    public void sendChatMessage(String username, String chatMessage) {
        // we need a message to populate
        Message message = new Message();

        // populate with the fields we expect
        message.put("username", username);
        message.put("chatMessage", chatMessage);

        // add some other fields that might be useful
        message.put("timestamp", System.currentTimeMillis());

        try {
            InetAddress address = InetAddressUtil.getLocalInetAddress();
            message.put("origin", address.getHostName());
        } catch (Exception e) {
            // we do not need this, so we'll just discard the exception
        }

        // ready to send the message
        messageBus.sendMessage(ChatMessageClusterConstants.DESTINATION, message);
    }

    @Reference(unbind = "-")
    private MessageBus messageBus;

    @Reference(unbind = "-")
    private Portal portal;
}
