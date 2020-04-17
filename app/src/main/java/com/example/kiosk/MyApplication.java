package com.example.kiosk;

import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;

@AcraCore(buildConfigClass = BuildConfig.class)
@AcraMailSender(mailTo = "kyle.gilbert1622@gmail.com")
public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
