package com.hon.sunny.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;

import com.hon.sunny.R;
import com.hon.sunny.ui.base.BaseActivity;
import com.hon.sunny.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class SplashActivity extends BaseActivity {

    private static final int COUNT_DOWN_TIME = 4;
    private static final int COUNT_DOWN_INTERVAL = 1;

    @BindView(R.id.svg_splash)
    SvgView svgView;
    @BindView(R.id.btn_skip)
    Button skip;

    private final CountDownTimer countDownTimer = new CountDownTimer(COUNT_DOWN_TIME * 1000,
            COUNT_DOWN_INTERVAL * 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            skip.setText(String.format(getString(R.string.skip), millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            skip.setText(String.format(getString(R.string.skip), 0));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlag();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        svgView.setSvgResource(R.raw.cloud);
        svgView.setCallback(new SvgView.SvgCompletedCallBack() {
            @Override
            public void onSvgStart() {
                countDownTimer.start();
            }

            @Override
            public void onSvgCompleted() {
                finishSelf();
            }
        });

        skip.setOnClickListener(v -> svgView.stop());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        svgView.stop();
        countDownTimer.cancel();
    }

    private void finishSelf() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}