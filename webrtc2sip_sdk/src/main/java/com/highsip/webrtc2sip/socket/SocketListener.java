package com.highsip.webrtc2sip.socket;

import java.net.Socket;

public interface SocketListener {

    void onOpen(Socket socket, Request request);

    void onMessage(Socket socket, String text);

    void onIdle(Socket socket);

    void onClosing(Socket socket);

    void onClosed(Socket socket);

    void onFailure(Socket socket);
}
