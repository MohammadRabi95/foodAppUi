package com.lescale.restaurant.Activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.lescale.restaurant.Classes.AppHelper;
import com.lescale.restaurant.R;

public class SplashScreen extends AppCompatActivity {

    private AnimationDrawable animationDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreem);

        ImageView container = findViewById(R.id.iv_icons);
        container.setBackgroundResource(R.drawable.splash_animation);

        animationDrawable  = (AnimationDrawable) container.getBackground();

        AppHelper.setIsPromoDialogShown(SplashScreen.this,false);

    }
    @Override
    protected void onResume() {
        super.onResume();

        animationDrawable.start();

        checkAnimationStatus(50, animationDrawable);
    }

    private void checkAnimationStatus(final int time, final AnimationDrawable animationDrawable) {

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                if (animationDrawable.getCurrent() != animationDrawable.getFrame(animationDrawable.getNumberOfFrames() - 1))
                    checkAnimationStatus(time, animationDrawable);

                    startActivity(new Intent(SplashScreen.this,HomeActivity.class));
                finish();

                AppHelper.setIsAppRanFirstTime(SplashScreen.this,true);
                startActivity(new Intent(SplashScreen.this,Language.class));
                finish();

        }, 5800);
    }

}

