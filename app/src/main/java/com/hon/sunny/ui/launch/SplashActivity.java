package com.hon.sunny.ui.launch;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private static final int COUNT_DOWN_TIME = 5;
    private static final int COUNT_DOWN_INTERVAL = 1;

    @BindView(R.id.svg_splash)
    SvgView svgView;
    @BindView(R.id.tv_count)
    TextView count;
    @BindView(R.id.tv_skip)
    TextView skip;

    private CountDownTimer mCountDownTimer = new CountDownTimer(COUNT_DOWN_TIME * 1000,
            COUNT_DOWN_INTERVAL * 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            count.setText(getString(R.string.splash_countdown_time,
                    millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            count.setText(getString(R.string.splash_countdown_time,
                    0));
            finishSelf();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //activity切换的淡入淡出效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        svgView.setSvgResource(R.raw.cloud);
        svgView.startAnimation();
        mCountDownTimer.start();

        skip.setOnClickListener(v -> {
                    finishSelf();
                    mCountDownTimer.cancel();
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountDownTimer.cancel();
    }

    private void finishSelf() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
