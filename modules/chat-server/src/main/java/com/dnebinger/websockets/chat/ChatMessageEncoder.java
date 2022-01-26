package com.dnebinger.websockets.chat;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * class ChatMessageEncoder: Encodes our chat message as a JSON string.
 *
 * @author dnebinger
 */
public class ChatMessageEncoder implements Encoder.Text<ChatMessage> {
    /**
     * encode: Encode the given chat message into a JSON String.
     *
     * @param chatMessage the object being encoded.
     * @return String the encoded object as a JSON string.
     */
    @Override
    public String encode(ChatMessage chatMessage) throws EncodeException {
        JSONObject json = JSONFactoryUtil.createJSONObject();

        json.put("from", chatMessage.getFrom());
        json.put("message", chatMessage.getMessage());

        return json.toString();
    }

    /**
     * init: Called to initialize the encoder.
     *
     * @param config the endpoint configuration object when being brought into
     *               service
     */
    @Override
    public void init(EndpointConfig config) {

    }

    /**
     * destroy: Called when about to be destroyed.
     */
    @Override
    public void destroy() {

    }
}
