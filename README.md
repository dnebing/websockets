# Liferay Websockets

This repository demonstrates how to create and use Websockets in Liferay.

It contains two modules.

The first is chat-server. This module contains the implementation of the Websocket for
a broadcast chat system. It is OSGi-ready and Liferay cluster aware. Any message this
Websocket receives will be broadcast out to all connected clients.

The second is a super simple chat portlet. It is not fancy at all, but it does show
how a portlet can open and use a Websocket to communicate back to Liferay.
