package com.dnebinger.websockets.chat;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * class ChatEndpoint: Implementation of our endpoint for the chat websocket example.
 *
 * @author dnebinger
 */
@Component(
        immediate = true,
        property = {
                "org.osgi.http.websocket.endpoint.path=/o/websocket/chat/{username}",
                "org.osgi.http.websocket.endpoint.decoders=com.dnebinger.websockets.chat.ChatMessageDecoder",
                "org.osgi.http.websocket.endpoint.encoders=com.dnebinger.websockets.chat.ChatMessageEncoder"

                /* NOTE: These two properties cannot be set this way because of the type Liferay expects the
                properties to be in (basically a List of Classes, not a List of class names like in other
                properties usage.

                When the bug is fixed, these properties can be restored and used.
                */
        },
        service = Endpoint.class
)
public class ChatEndpoint extends Endpoint {
    private static Set<Session> activeSessions = new CopyOnWriteArraySet<>();
    private static Map<String, String> userSessions = new HashMap();

    /**
     * onOpen: We must implement this method to be a real endpoint.
     *
     * @param session the session that has just been activated.
     * @param config  the configuration used to configure this endpoint.
     */
    @Override
    public void onOpen(final Session session, EndpointConfig config) {
        if (_log.isDebugEnabled()) {
            Map<String, Object> props = config.getUserProperties();

            _log.debug("Endpoint properties:");

            for (String key : props.keySet()) {
                _log.debug("  {}: {}", key, props.get(key));
            }

            Map<String, String> pathParms = session.getPathParameters();
            _log.debug("Path params:");
            for (String key : pathParms.keySet()) {
                _log.debug("  {}: {}", key, pathParms.get(key));
            }
        }

        final String username = session.getPathParameters().get("username");

        if (_log.isDebugEnabled()) {
            _log.debug("New session {} for user {}", session.getId(), username);
        }

        userSessions.put(session.getId(), username);
        activeSessions.add(session);

        ChatMessage connected = new ChatMessage(username, "Connected!");
        broadcast(connected);

        /**
         * The code below will not be called because the ChatMessage decoder is not registered,
         * see the comment above in the component annotation.
         */
        // add a message handler for new chat messages
        session.addMessageHandler(new MessageHandler.Whole<ChatMessage>() {
            /**
             * onMessage: Called when the message has been fully received.
             *
             * @param message the message data.
             */
            @Override
            public void onMessage(ChatMessage message) {
                message.setFrom(username);

                if (_log.isDebugEnabled()) {
                    _log.debug("Received from {}: {}", message.getFrom(), message.getMessage());
                }

                // send to all active sessions on the current node
                broadcast(message);

                // broadcast to the cluster so they also will broadcast to their websocket sessions
                chatMessageClusterSender.sendChatMessage(message.getFrom(), message.getMessage());
            }
        });

        // add a message handler for raw chat messages
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            /**
             * onMessage: Called when the message has been fully received.
             *
             * @param message the message data.
             */
            @Override
            public void onMessage(String message) {
                ChatMessage chatMessage;

                try {
                    JSONObject json = JSONFactoryUtil.createJSONObject(message);

                    chatMessage = new ChatMessage(username, json.getString("message"));
                } catch (JSONException e) {
                    _log.warn("Failed to parse json {}: {}", message, e.getMessage(),e);

                    chatMessage = new ChatMessage(username, message);
                }

                if (_log.isDebugEnabled()) {
                    _log.debug("Received from {}: {} raw: {}", chatMessage.getFrom(), chatMessage.getMessage(), message);
                }

                // send to all active sessions on the current node
                broadcast(chatMessage);

                // broadcast to the cluster so they also will broadcast to their websocket sessions
                chatMessageClusterSender.sendChatMessage(chatMessage.getFrom(), chatMessage.getMessage());
            }
        });
    }

    /**
     * broadcast: Sends the given message to all active sessions.
     * @param chatMessage
     * @throws EncodeException
     * @throws IOException
     */
    public static void broadcast(final ChatMessage chatMessage) {
        // for each session
        for (Session session : activeSessions) {
            synchronized (session) {
                // send the message to all sessions
                try {
                    JSONObject json = JSONFactoryUtil.createJSONObject();

                    json.put("from", chatMessage.getFrom());
                    json.put("message", chatMessage.getMessage());

                    session.getBasicRemote().sendText(json.toString());

                    /**
                     * If the encoder could be properly declared (see the comment above in the component annotation),
                     * this code block would have been simplified to a single line:
                     *
                     * session.getBasicRemote().sendObject(chatMessage);
                     */
                } catch (Exception e) {
                    _log.warn("Error sending message to session {}: {}", session.getId(), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * onClose: Called when the session is being closed.
     *
     * @param session     the session about to be closed.
     * @param closeReason the reason the session was closed.
     */
    @Override
    public void onClose(final Session session, CloseReason closeReason) {
        if (_log.isDebugEnabled()) {
            _log.debug("Closing session id {}", session.getId());
        }

        activeSessions.remove(session);
        userSessions.remove(session.getId());
    }

    /**
     * onError: Called when an error occurs that websockets cannot handle per the spec.
     *
     * @param session the session in use when the error occurs.
     * @param thr the throwable representing the problem.
     */
    @Override
    public void onError(final Session session, Throwable thr) {
        _log.error("Websocket error on session id {}: {}", session.getId(), thr.getMessage(), thr);
    }

    @Reference
    private ChatMessageClusterSender chatMessageClusterSender;

    private static final Logger _log = LoggerFactory.getLogger(ChatEndpoint.class);
}
