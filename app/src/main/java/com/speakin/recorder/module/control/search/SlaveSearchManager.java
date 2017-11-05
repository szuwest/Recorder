package com.speakin.recorder.module.control.search;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright 2017 SpeakIn.Inc
 * Created by west on 2017/9/27.
 */

public class SlaveSearchManager implements DeviceBroadcastReceiver.BroadcastReceiverCallback{

    private static final String TAG = "SlaveSearchManager";
    private DeviceBroadcastSender broadcastSender;
    private DeviceBroadcastReceiver broadcastReceiver;

    public interface SlaveSearchManagerCallback {
        void onFoundMaster(String masterIp, JSONObject masterInfo);
    }

    private SlaveSearchManagerCallback callback;

    public void setSlaveSearchCallback(SlaveSearchManagerCallback callback) {
        this.callback = callback;
    }

    private String teamId;
    private String taskId;

    public SlaveSearchManager(String teamId, String taskId) {
        this.teamId = teamId;
        this.taskId = taskId;
    }

    private String getSendMsg() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "slave");
            jsonObject.put("teamId", this.teamId);
            jsonObject.put("taskId", this.taskId);
            jsonObject.put("deviceName", Build.MODEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public void start() {
        broadcastReceiver = new DeviceBroadcastReceiver(true);
        broadcastReceiver.setBroadcastReceiveCallback(this);

        broadcastSender = new DeviceBroadcastSender(true);
        broadcastReceiver.startBroadcastReceive();
    }

    public void stop() {
        broadcastReceiver.stopReceive();
        broadcastReceiver.setBroadcastReceiveCallback(null);
        broadcastReceiver = null;
        broadcastSender = null;
    }

    @Override
    public void onError(String errMsg) {
        Log.d(TAG, "errMsg=" + errMsg);
    }

    @Override
    public void onReceive(String senderIp, String message) {
        Log.d(TAG, "message:" + message);
        try {
            JSONObject jsonObj = new JSONObject(message);
            String type = jsonObj.optString("type");
            if (type != null && type.equals("master")) {
                String team = jsonObj.optString("teamId");
                String task = jsonObj.optString("taskId");
                broadcastSender.sendBroadcastData(getSendMsg());
                if (!TextUtils.isEmpty(team) && team.equals(teamId) && !TextUtils.isEmpty(task) && task.equals(taskId)) {
                    if (callback != null) callback.onFoundMaster(senderIp, jsonObj);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
