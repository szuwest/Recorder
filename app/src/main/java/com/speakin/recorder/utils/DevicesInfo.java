package com.speakin.recorder.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.speakin.recorder.RecorderApp;


public class DevicesInfo {

    private static DevicesInfo info = null;
	public String imei;
    public String deviceName;
    private String systemVer;

	public DevicesInfo(Context ctx) {
        imei = DeviceUUID.getDeviceUUID(ctx);
        deviceName = Build.MODEL;
        systemVer = Build.VERSION.SDK;
    }

	public static DevicesInfo getDeviceInfo() {
        if (info == null) {
            info = new DevicesInfo(RecorderApp.app);
        }
        return info;
    }

	public String getIMEI(){
		return imei;
	}

}
