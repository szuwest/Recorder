package com.speakin.recorder.module.control.socket;

import com.speakin.recorder.module.control.socket.handle.MsgReceiveHandler;
import com.speakin.recorder.module.control.socket.handle.MsgSendHandler;

import java.net.Socket;

/**
 * Copyright 2017 SpeakIn.Inc
 * Created by west on 2017/10/24.
 */

public class SocketClient {

    private Socket socket;
    private MsgReceiveHandler receiveHandler;
    private MsgSendHandler  sendHandler;

    public SocketClient(Socket socket) {
        this.socket = socket;
    }

    public void start() {
        receiveHandler = new MsgReceiveHandler(socket);
        receiveHandler.start();
        sendHandler = new MsgSendHandler(socket);
        sendHandler.start();
    }

}
