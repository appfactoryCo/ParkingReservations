package com.ridecell.app.ridecell.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ridecell.app.ridecell.R;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Set texts and animations and customize fonts
        final Typeface font_dayslater = Typeface.createFromAsset(this.getAssets(), "fonts/dayslater.ttf");
        final Typeface font_crochet = Typeface.createFromAsset(this.getAssets(), "fonts/crochetpattern.ttf");

        final Animation fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in_splash);

        final TextView sanfranciscoTv = (TextView) findViewById(R.id.sanfranciscoTv);
        sanfranciscoTv.setTypeface(font_dayslater);

        final TextView parkingTv = (TextView) findViewById(R.id.parkingTv);
        parkingTv.setTypeface(font_dayslater);

        final TextView madeEasyTv = (TextView) findViewById(R.id.madeEasyTv);
        madeEasyTv.setTypeface(font_crochet);
        madeEasyTv.setText("made easy");
        madeEasyTv.startAnimation(fadein);

        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent i = new Intent(getBaseContext(), ParkingSpotsActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
