package com.speakin.recorder.module.control.search;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.speakin.recorder.RecorderApp;
import com.speakin.recorder.module.control.ControlConstants;
import com.speakin.recorder.utils.IpUtil;
import com.speakin.recorder.utils.NetHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Copyright 2017 SpeakIn.Inc
 * Created by west on 2017/9/26.
 */

public class DeviceBroadcastSender {

    private static final String BROADCAST_IP = "255.255.255.255";
//    private static final String MSG_TO_SEND = "{\"from\":\"speakin\",\"type\":\"slave\",\"port\":9001}";
//    private static final String MSG_TO_SEND2 = "{\"from\":\"speakin\",\"type\":\"master\"},\"port\":9002";


    private int port = 0;
    private boolean isSlave = true;

    public DeviceBroadcastSender(boolean isSlave) {
        this.isSlave = isSlave;
        if (isSlave) port = ControlConstants.MASTER_LISTEN_PORT;
        else port = ControlConstants.SLAVE_LISTEN_PORT;
    }

//    private String getMsgToSend() {
//        if (isSlave) return MSG_TO_SEND;
//        else return MSG_TO_SEND2;
//    }

    private void sendBroadcast(String message) throws IOException {

        String ipAddr = BROADCAST_IP;
        if (IpUtil.isWifiApEnabled(RecorderApp.app)) {
            System.out.println("已开启热点");
            ipAddr = "192.168.43.255";
        }

        byte[] msg = new String(message).getBytes();
        /*
         * 在Java UDP中单播与广播的代码是相同的,要实现具有广播功能的程序只需要使用广播地址即可, 例如：这里使用了本地的广播地址
         */
        InetAddress inetAddr = InetAddress.getByName(ipAddr);
        DatagramSocket client = new DatagramSocket();

        DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddr, port);

        client.send(sendPack);
        System.out.println("send msg complete");
        client.close();
    }


    public void sendBroadcastData(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sendBroadcast(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static InetAddress getBroadcastAddress(Context context) throws UnknownHostException {
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        if(dhcp==null) {
            return InetAddress.getByName("255.255.255.255");
        }
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }
}
