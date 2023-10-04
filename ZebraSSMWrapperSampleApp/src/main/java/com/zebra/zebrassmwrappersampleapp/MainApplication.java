package com.zebra.zebrassmwrappersampleapp;

import android.app.Application;

import com.zebra.zebrassmwrapper.PackagesSignaturesManager;

public class MainApplication extends Application {

    public static PackagesSignaturesManager packagesSignaturesManager = null;
    @Override
    public void onCreate() {
        super.onCreate();
        packagesSignaturesManager = new PackagesSignaturesManager(this);
        packagesSignaturesManager.broadcastCurrentSignature();
    }
}
