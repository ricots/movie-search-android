package com.romeroz.moviesearch;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application {

    public static String APP_TAG = "MovieSearch";

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Instantiate Realm with default configuration
         */
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        /**
         * Instantiate ImageLoader (do this once)
         * Also add these permissions to the Manifest to download and cache images on SD card:
         * <uses-permission android:name="android.permission.INTERNET" />
         * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
         *
         * Set up the configs to cache in memory and on disk (not enabled by default)
         * See: https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Useful-Info
         */
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);



    }
}