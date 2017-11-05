package com.speakin.recorder;

import android.app.Application;

/**
 * Copyright 2017 SpeakIn.Inc
 * Created by west on 2017/10/25.
 */

public class RecorderApp extends Application {

    public static RecorderApp app = null;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
