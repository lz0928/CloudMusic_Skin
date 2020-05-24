package com.louis.skin;

import android.app.Application;

import com.louis.cloudmusic.skin.library.SkinManager;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.init(this);
    }
}
