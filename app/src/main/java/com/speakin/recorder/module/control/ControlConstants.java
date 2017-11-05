package com.speakin.recorder.module.control;

import android.os.Environment;

/**
 * Copyright 2017 SpeakIn.Inc
 * Created by west on 2017/10/17.
 */

public class ControlConstants {

    public static final String TEAMID = "Speakin";
    public static final String TASKID  = "Test";
    public static final int SLAVECOUNT = 3;

    public static final int SERVER_SOCKET_PORT = 8080;
    public static final String PROTOCOL = "speakinchat";//

    public static final int SLAVE_LISTEN_PORT = 8883;
    public static final int MASTER_LISTEN_PORT = 8882;

    public static final String ROOT  = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SpeakInRecorder/";
    public static final String RECEIVE_DIR = ROOT + "receive/";

}
