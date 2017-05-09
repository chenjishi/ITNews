package com.misscellapp.news;

import android.app.Application;
import com.flurry.android.FlurryAgent;

/**
 * Created by chenjishi on 16/7/27.
 */
public class NewsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        new FlurryAgent.Builder().build(this, "QHDH3D87G8PSSYCGM36C");
    }
}
