package com.dnebinger.websockets.chat;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * class ChatMessageDecoder: A decoder class to convert from a JSON string back to a ChatMessage.
 *
 * @author dnebinger
 */
public class ChatMessageDecoder implements Decoder.Text<ChatMessage> {
    /**
     * decode: Decode the given JSON String into a ChatMessage.
     *
     * @param json string to be decoded.
     * @return ChatMessage the decoded message
     */
    @Override
    public ChatMessage decode(String json) throws DecodeException {
        try {
            JSONObject jsonObj = JSONFactoryUtil.createJSONObject(json);

            return new ChatMessage(jsonObj.getString("from"), jsonObj.getString("message"));
        } catch (JSONException e) {
            throw new DecodeException(json, "Failed to decode chat message", e);
        }
    }

    /**
     * willDecode: Answer whether the given String can be decoded into a ChatMessage.
     *
     * @param json the string being tested for decodability.
     * @return whether this decoder can decode the supplied string.
     */
    @Override
    public boolean willDecode(String json) {
        if ((json == null) || (json.trim().isEmpty())) {
            return false;
        }

        try {
            JSONObject jsonObj = JSONFactoryUtil.createJSONObject(json);

            if ((!jsonObj.has("from")) || (!jsonObj.has("message"))) {
                return false;
            }

            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * init: Called to initialize the decoder.
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
