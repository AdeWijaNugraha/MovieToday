package com.awn.app.movietoday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.daimajia.androidanimations.library.Techniques;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class SplashScreen extends AwesomeSplash {
//    private SharedPreferences sharedPreferences;


    @Override
    public void initSplash(ConfigSplash configSplash) {

        configSplash.setBackgroundColor(R.color.colorPrimary);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            //Background Animation


//
//            if (sharedPreferences.contains(getString(R.string.colorPrimaryPreference))){
//                Log.e("LOL", "initSplash: YES");
//
//                Log.e("LOL", "initSplash: " + sharedPreferences.getAll());
//
//
//            } else {
//                Log.e("LOL", "initSplash: NO");
//            }
//
//            configSplash.setBackgroundColor(sharedPreferences.getInt("Color Primary Preference",  -27801));
            configSplash.setAnimCircularRevealDuration(2000);
            configSplash.setRevealFlagX(Flags.REVEAL_LEFT);
            configSplash.setRevealFlagX(Flags.REVEAL_BOTTOM);



        //Logo
        configSplash.setLogoSplash(R.drawable.camera);
        configSplash.setAnimLogoSplashDuration(2000);
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeIn);


        //Title
        configSplash.setTitleSplash("MovieToday");
        configSplash.setTitleTextColor(R.color.colorTxtSplash);
        configSplash.setTitleTextSize(30f);
        configSplash.setAnimTitleDuration(2000);
        configSplash.setAnimTitleTechnique(Techniques.FlipInX);

    }

    @Override
    public void animationsFinished() {
        startActivity(new Intent(SplashScreen.this, MainActivity.class));
        finish();
    }
}
