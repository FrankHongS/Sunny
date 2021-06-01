package com.hon.sunny.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hon.sunny.R;
import com.hon.sunny.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.svg_splash)
    SvgView svgView;
    @BindView(R.id.tv_skip)
    TextView skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //activity切换的淡入淡出效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        svgView.setSvgResource(R.raw.cloud);
        svgView.setCallback(new SvgView.SvgCompletedCallBack() {
            @Override
            public void onSvgStart() {

            }

            @Override
            public void onSvgCompleted() {
                finishSelf();
            }
        });

        skip.setOnClickListener(v -> svgView.stop());
    }

    private void finishSelf() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}