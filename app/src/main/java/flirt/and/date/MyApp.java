package flirt.and.date;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.StorageReference;
import com.my.tracker.MyTracker;

import java.io.InputStream;

public class MyApp extends Application {
    String SDK_KEY = "91295278886155800759";
    @Override
    public void onCreate()
    {
        super.onCreate();


        MyTracker.initTracker(SDK_KEY, this);


    }


}
