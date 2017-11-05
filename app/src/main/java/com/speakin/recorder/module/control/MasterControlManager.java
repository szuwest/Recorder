package com.speakin.recorder.module.control;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.koushikdutta.async.AsyncNetworkSocket;
import com.koushikdutta.async.http.WebSocket;
import com.speakin.recorder.module.control.search.MasterSearchManager;
import com.speakin.recorder.module.control.websocket.MasterWebSocketManager;

import org.json.JSONObject;

import java.util.List;

/**
 * Copyright 2017 SpeakIn.Inc
 * Created by west on 2017/10/9.
 */

public class MasterControlManager implements MasterSearchManager.MasterSearchManagerCallback, MasterWebSocketManager.MasterSocketManagerCallback{

    public interface MasterControlManagerCallback {
        void onServerError(Exception ex);
        void onClientConnected(String clientSocket);
        void onClientDisconnect(String clientSocket);
        void onMessageReceive(String clientSocket, String message);
        void onReceiveFile(String clientSocket, String filePath);
    }

    private static final String TAG = MasterControlManager.class.getSimpleName();
    private MasterSearchManager searchManager;
    private MasterWebSocketManager socketManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private MasterControlManagerCallback controlManagerCallback;
    private boolean isRunning = false;

    public void setControlManagerCallback(MasterControlManagerCallback controlManagerCallback) {
        this.controlManagerCallback = controlManagerCallback;
    }

    public void start() {
        start(ControlConstants.SLAVECOUNT, ControlConstants.TEAMID, ControlConstants.TASKID);
    }

    public void stop() {
        stopSearch();
        stopMaster();
        isRunning = false;
    }

    public void start(int slaveCount, String teamId, String taskId) {
        if (isRunning) {
            Log.d(TAG, "is running");
            return;
        }
        searchManager = new MasterSearchManager(slaveCount, teamId, taskId);
        searchManager.setSearchCallback(this);
        searchManager.start();
        socketManager = new MasterWebSocketManager(slaveCount);
        socketManager.setMasterSocketManager(this);
        socketManager.start();

    }

    public void send(String message) {
        if (socketManager != null) {
            socketManager.sendMessage(message);
        }
    }

    private void stopMaster() {
        if (socketManager != null) {
            socketManager.stop();
            socketManager.setMasterSocketManager(null);
        }
        socketManager = null;
    }

    private void stopSearch() {
        if (searchManager != null) {
            searchManager.stop();
            searchManager.setSearchCallback(null);
        }
        searchManager = null;
    }

    @Override
    public void onFoundNewSlave(String slaveIp, JSONObject slaveInfo) {
        Log.d(TAG, "found slave " + slaveIp + " info=" + slaveInfo);
    }

    @Override
    public void onFoundSlaves(List<String> slaveIpList) {
        Log.d(TAG, "slave count = " + slaveIpList.size());
        String msg = "发现从机：\n";
        for (String ip : slaveIpList) {
            msg += "IP地址：" + ip + "\n";
        }
        Log.d(TAG, msg);

        handler.post(new Runnable() {
            @Override
            public void run() {
                stopSearch();
            }
        });
    }

    @Override
    public void onServerError(final Exception ex) {
        Log.d(TAG, "start server error " + ex.getLocalizedMessage());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (controlManagerCallback != null) {
                    controlManagerCallback.onServerError(ex);
                }
            }
        });

        isRunning = false;
    }

    @Override
    public void onClientConnected(WebSocket clientSocket) {
        final AsyncNetworkSocket workSocket = (AsyncNetworkSocket) clientSocket.getSocket();
        Log.d(TAG, "client connected: " + workSocket.getRemoteAddress().getHostName());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (controlManagerCallback != null) {
                    controlManagerCallback.onClientConnected(workSocket.getRemoteAddress().getHostName());
                }
                if (socketManager.getClientCount() == ControlConstants.SLAVECOUNT) {
                    stopSearch();
                }
            }
        });
    }

    @Override
    public void onClientDisconnect(WebSocket clientSocket) {
        final AsyncNetworkSocket workSocket = (AsyncNetworkSocket) clientSocket.getSocket();
        Log.d(TAG, "client onClientDisconnect: " + workSocket.getRemoteAddress().getHostName());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (controlManagerCallback != null)
                    controlManagerCallback.onClientDisconnect(workSocket.getRemoteAddress().getHostName());
            }
        });

    }

    @Override
    public void onMessageReceive(WebSocket clientSocket, final String message) {
        final AsyncNetworkSocket workSocket = (AsyncNetworkSocket) clientSocket.getSocket();
        Log.d(TAG, "receive: " + message + "from: " + workSocket.getRemoteAddress().getHostName());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (controlManagerCallback != null)
                    controlManagerCallback.onMessageReceive(workSocket.getRemoteAddress().getHostName(), message);
            }
        });
    }

    @Override
    public void onFileReceive(WebSocket clientSocket, final String filePath) {
        final AsyncNetworkSocket workSocket = (AsyncNetworkSocket) clientSocket.getSocket();
        Log.d(TAG, "receive: " + filePath + "from: " + workSocket.getRemoteAddress().getHostName());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (controlManagerCallback != null)
                    controlManagerCallback.onReceiveFile(workSocket.getRemoteAddress().getHostName(), filePath);
            }
        });
    }
}
