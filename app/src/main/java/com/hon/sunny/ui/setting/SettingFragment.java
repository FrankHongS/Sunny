package com.hon.sunny.ui.setting;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.hon.sunny.R;
import com.hon.sunny.Sunny;
import com.hon.sunny.component.ImageLoader;
import com.hon.sunny.component.event.ChangeCityEvent;
import com.hon.sunny.service.AutoUpdateService;
import com.hon.sunny.ui.main.MainActivity;
import com.hon.sunny.utils.FileSizeUtil;
import com.hon.sunny.utils.FileUtil;
import com.hon.sunny.utils.RxUtils;
import com.hon.sunny.utils.SharedPreferenceUtil;
import com.hon.sunny.utils.SimpleSubscriber;
import com.hon.sunny.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import io.reactivex.Observable;

import static com.hon.sunny.utils.Constants.ANIM_START;
import static com.hon.sunny.utils.Constants.AUTO_UPDATE;
import static com.hon.sunny.utils.Constants.CHANGE_ICONS;
import static com.hon.sunny.utils.Constants.CHANGE_UPDATE_TIME;
import static com.hon.sunny.utils.Constants.CLEAR_CACHE;
import static com.hon.sunny.utils.Constants.INIT_SERVICE;
import static com.hon.sunny.utils.Constants.NOTIFICATION_MODEL;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {
    private static String TAG = SettingFragment.class.getSimpleName();

    private SharedPreferenceUtil mSharedPreferenceUtil;

    private Preference mChangeIcons;
    private Preference mChangeUpdateTime;
    private Preference mClearCache;

    // if auto update
    private CheckBoxPreference mAutoUpdate;
    private CheckBoxPreference mNotificationType;
    private CheckBoxPreference mAnimationOnOff;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        mSharedPreferenceUtil = SharedPreferenceUtil.getInstance();

        initView();
    }

    private void initView() {
        mChangeIcons = findPreference(CHANGE_ICONS);
        mAutoUpdate = (CheckBoxPreference) findPreference(AUTO_UPDATE);
        mChangeUpdateTime = findPreference(CHANGE_UPDATE_TIME);
        mClearCache = findPreference(CLEAR_CACHE);

        mAnimationOnOff = (CheckBoxPreference) findPreference(ANIM_START);
        mNotificationType = (CheckBoxPreference) findPreference(NOTIFICATION_MODEL);

        mNotificationType.setChecked(
                mSharedPreferenceUtil.getNotificationModel()
                        == Notification.FLAG_ONGOING_EVENT
        );
        mAnimationOnOff.setChecked(mSharedPreferenceUtil.getMainAnim());
        mChangeIcons.setSummary(
                getResources().getStringArray(R.array.icons)[mSharedPreferenceUtil.getInt(CHANGE_ICONS,0)]);

        //为了兼容小米，应用被彻底清除之后，前台service也会关闭
        mAutoUpdate.setChecked(Util.isServiceRunning(getActivity(),"com.hon.sunny.service.AutoUpdateService"));
//        mAutoUpdate.setChecked(mSharedPreferenceUtil.getBoolean(AUTO_UPDATE));
        mChangeUpdateTime.setEnabled(mSharedPreferenceUtil.getBoolean(AUTO_UPDATE));
        mChangeUpdateTime.setSummary(
                "每" + mSharedPreferenceUtil.getInt(CHANGE_UPDATE_TIME,3) + "小时更新");
        mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(Sunny.getAppCacheDir() + "/NetCache"));

        mChangeIcons.setOnPreferenceClickListener(this);
        mChangeUpdateTime.setOnPreferenceClickListener(this);
        mClearCache.setOnPreferenceClickListener(this);

        mAutoUpdate.setOnPreferenceChangeListener(this);
        mNotificationType.setOnPreferenceChangeListener(this);
        mAnimationOnOff.setOnPreferenceChangeListener(this);
    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (mChangeIcons == preference) {
            showIconDialog();
        } else if (mClearCache == preference) {

            ImageLoader.clear(getActivity());
            Observable.just(FileUtil.delete(new File(Sunny.getAppCacheDir() + "/NetCache")))
                .compose(RxUtils.rxSchedulerHelper()).subscribe(new SimpleSubscriber<Boolean>() {
                @Override
                public void onNext(Boolean success) {
                    mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(Sunny.getAppCacheDir() + "/NetCache"));
                    if(success){
                        Snackbar.make(getView(), "缓存已清除", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (mChangeUpdateTime == preference) {
            showUpdateDialog();
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        Boolean newSetting = (Boolean) newValue;

        if (preference == mAnimationOnOff) {
            mSharedPreferenceUtil.setMainAnim(newSetting);
        } else if (preference == mAutoUpdate) {
            if (newSetting) {

                Intent i=new Intent(getActivity(), AutoUpdateService.class);
                i.putExtra(INIT_SERVICE,true);
                getActivity().startService(i);

                mSharedPreferenceUtil.putBoolean(AUTO_UPDATE,true);
                mChangeUpdateTime.setEnabled(true);
            } else {

                getActivity().stopService(new Intent(getActivity(), AutoUpdateService.class));

                mSharedPreferenceUtil.putBoolean(AUTO_UPDATE,false);
                mChangeUpdateTime.setEnabled(false);
            }
        } else if (preference == mNotificationType) {
            mSharedPreferenceUtil.setNotificationModel(
                    newSetting ? Notification.FLAG_ONGOING_EVENT : Notification.FLAG_AUTO_CANCEL);
        }

        return true;
    }

    private void showIconDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.icon_dialog, getActivity().findViewById(R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();

        LinearLayout layoutTypeOne = dialogLayout.findViewById(R.id.layout_one);
        layoutTypeOne.setClickable(true);
        RadioButton radioTypeOne = dialogLayout.findViewById(R.id.radio_one);
        LinearLayout layoutTypeTwo = dialogLayout.findViewById(R.id.layout_two);
        layoutTypeTwo.setClickable(true);
        RadioButton radioTypeTwo = dialogLayout.findViewById(R.id.radio_two);
        TextView done = dialogLayout.findViewById(R.id.done);

        radioTypeOne.setClickable(false);
        radioTypeTwo.setClickable(false);

        alertDialog.show();

        switch (mSharedPreferenceUtil.getInt(CHANGE_ICONS,0)) {
            case 0:
                radioTypeOne.setChecked(true);
                radioTypeTwo.setChecked(false);
                break;
            case 1:
                radioTypeOne.setChecked(false);
                radioTypeTwo.setChecked(true);
                break;
        }

        layoutTypeOne.setOnClickListener(v -> {
            radioTypeOne.setChecked(true);
            radioTypeTwo.setChecked(false);
        });

        layoutTypeTwo.setOnClickListener(v -> {
            radioTypeOne.setChecked(false);
            radioTypeTwo.setChecked(true);
        });

        done.setOnClickListener(v -> {
            mSharedPreferenceUtil.putInt(CHANGE_ICONS,radioTypeOne.isChecked() ? 0 : 1);
            String[] iconsText = getResources().getStringArray(R.array.icons);
            mChangeIcons.setSummary(radioTypeOne.isChecked() ? iconsText[0] :
                    iconsText[1]);

            alertDialog.dismiss();
            Snackbar.make(getView(), "切换成功,重启应用生效",
                    Snackbar.LENGTH_INDEFINITE).setAction("重启",
                    v1 -> {

                        //Intent intent =
                        //    getActivity().getPackageManager().getLaunchIntentForPackage(getActivity().getPackageName());
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                        EventBus.getDefault().post(new ChangeCityEvent());
                    }).show();
        });
    }

    private void showUpdateDialog() {
        //将 SeekBar 放入 Dialog 的方案 http://stackoverflow.com/questions/7184104/how-do-i-put-a-seek-bar-in-an-alert-dialog
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.update_dialog, (ViewGroup) getActivity().findViewById(
                R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();

        final SeekBar mSeekBar = dialogLayout.findViewById(R.id.time_seekbar);
        final TextView tvShowHour = dialogLayout.findViewById(R.id.tv_showhour);
        TextView tvDone = dialogLayout.findViewById(R.id.done);

        mSeekBar.setMax(24);
        mSeekBar.setProgress(mSharedPreferenceUtil.getInt(CHANGE_UPDATE_TIME,3));
        tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()));
        alertDialog.show();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvDone.setOnClickListener(v -> {
            mSharedPreferenceUtil.putInt(CHANGE_UPDATE_TIME,mSeekBar.getProgress()+1);
            mChangeUpdateTime.setSummary(
                    "每" + mSharedPreferenceUtil.getInt(CHANGE_UPDATE_TIME,3) + "小时更新");
            getActivity().stopService(new Intent(getActivity(), AutoUpdateService.class));
            getActivity().startService(new Intent(getActivity(), AutoUpdateService.class));
            alertDialog.dismiss();
        });
    }

}
