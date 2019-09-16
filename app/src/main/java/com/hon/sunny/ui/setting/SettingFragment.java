package com.hon.sunny.ui.setting;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.hon.sunny.R;
import com.hon.sunny.Sunny;
import com.hon.sunny.service.AutoUpdateService;
import com.hon.sunny.ui.main.MainActivity;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.FileSizeUtil;
import com.hon.sunny.utils.FileUtil;
import com.hon.sunny.utils.RxUtils;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

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

public class SettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    private static final String CACHE_PATH = Sunny.getAppCacheDir() + "/NetCache";

    private SharedPreferences mSharedPreferences;

    private Preference changeIcons;
    private SeekBarPreference changeUpdateTime;
    private Preference clearCache;

    private SwitchPreference autoUpdate;
    private CheckBoxPreference notificationType;
    private CheckBoxPreference animation;

    private Disposable cacheDisposable;

    private SettingIconDialog mSettingIconDialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(Sunny.getAppContext());

        initView();
    }

    private void initView() {
        changeIcons = findPreference(CHANGE_ICONS);
        animation = (CheckBoxPreference) findPreference(ANIM_START);
        autoUpdate = (SwitchPreference) findPreference(AUTO_UPDATE);
        changeUpdateTime = (SeekBarPreference) findPreference(CHANGE_UPDATE_TIME);
        notificationType = (CheckBoxPreference) findPreference(NOTIFICATION_MODEL);
        clearCache = findPreference(CLEAR_CACHE);

        changeIcons.setSummary(
                getResources().getStringArray(R.array.icons)[mSharedPreferences.getInt(CHANGE_ICONS, 1) - 1]);
        animation.setChecked(mSharedPreferences.getBoolean(ANIM_START, false));
        autoUpdate.setChecked(mSharedPreferences.getBoolean(AUTO_UPDATE, false));
        changeUpdateTime.setSummary(getString(R.string.setting_update_summary,
                mSharedPreferences.getInt(CHANGE_UPDATE_TIME, 3)));
        notificationType.setChecked(mSharedPreferences.getBoolean(NOTIFICATION_MODEL, true));
        clearCache.setSummary(FileSizeUtil.getFilesSize(CACHE_PATH));

        changeUpdateTime.setMin(3);
        changeUpdateTime.setMax(12);
        changeUpdateTime.setValue(mSharedPreferences.getInt(CHANGE_UPDATE_TIME, 3));

        changeIcons.setOnPreferenceClickListener(this);
        clearCache.setOnPreferenceClickListener(this);

        changeUpdateTime.setOnPreferenceChangeListener(this);
        autoUpdate.setOnPreferenceChangeListener(this);
        notificationType.setOnPreferenceChangeListener(this);
    }


    @SuppressWarnings("all")
    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == changeIcons) {
            showIconDialog();
        } else if (preference == clearCache) {
            cacheDisposable = Observable.just(FileUtil.delete(CACHE_PATH))
                    .map(success -> {
                        Glide.get(Sunny.getAppContext()).clearDiskCache();
                        return success;
                    })
                    .compose(RxUtils.rxSchedulerHelper())
                    .subscribe(success -> {
                        clearCache.setSummary(FileSizeUtil.getFilesSize(CACHE_PATH));
                        if (success) {
                            Snackbar.make(getView(), R.string.setting_cache_cleared_hint, Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == autoUpdate) {
            if (mSharedPreferences.getBoolean(AUTO_UPDATE, false)) {
                Intent i = new Intent(getActivity(), AutoUpdateService.class);
                i.putExtra(INIT_SERVICE, true);
                getActivity().startService(i);
            } else {
                getActivity().stopService(new Intent(getActivity(), AutoUpdateService.class));
            }
        } else if (preference == changeUpdateTime) {
            changeUpdateTime.setSummary(String.format(getString(R.string.setting_update_summary), (int) newValue));
        } else if (preference == notificationType) {
            setNotificationModel(
                    (boolean) newValue ? Notification.FLAG_ONGOING_EVENT : Notification.FLAG_AUTO_CANCEL
            );
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cacheDisposable != null && !cacheDisposable.isDisposed()) {
            cacheDisposable.dispose();
        }
    }

    private void showIconDialog() {
        if (mSettingIconDialog == null) {
            mSettingIconDialog = new SettingIconDialog(getContext());
        }
        mSettingIconDialog.setOnClickListener((dialog, which) -> {
            String[] iconsText = getResources().getStringArray(R.array.icons);
            changeIcons.setSummary(iconsText[mSettingIconDialog.getChecked() - 1]);

            Snackbar.make(getView(), R.string.setting_modify_icons_hint,
                    Snackbar.LENGTH_LONG).setAction(R.string.setting_restart,
                    v -> {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(Constants.RESST_ICONS, true);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }).show();
        });

        mSettingIconDialog.show();

    }

    //  通知栏模式 默认为常驻
    private void setNotificationModel(int t) {
        if (Build.VERSION.SDK_INT >= 23) {
            NotificationManager manager = (NotificationManager) Sunny.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
            StatusBarNotification[] notifications = manager.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {
                notification.getNotification().flags = t;
                manager.notify(1, notification.getNotification());
            }
        }
    }
}
