package com.misscellapp.news;

import android.app.Application;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by chenjishi on 16/7/27.
 */
public class NewsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
    }
}
