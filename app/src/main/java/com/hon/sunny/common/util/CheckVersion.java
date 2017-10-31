package com.hon.sunny.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.hon.sunny.component.retrofit.RetrofitSingleton;
import com.hon.sunny.about.domain.VersionAPI;
import com.hon.sunny.about.domain.VersionBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */

public class CheckVersion {

    public static void checkVersion(Context context) {
        RetrofitSingleton.getInstance().fetchVersion()
                .subscribe(new SimpleSubscriber<VersionAPI>() {
                    @Override
                    public void onNext(VersionAPI versionAPI) {
                        String firVersionName = versionAPI.versionShort;
                        String currentVersionName = Util.getVersion(context);
                        if (currentVersionName.compareTo(firVersionName) < 0) {
                            if (!SharedPreferenceUtil.getInstance().getString("version", "").equals(versionAPI.versionShort)) {
                                showUpdateDialog(versionAPI, context);
                            }
                        }
                    }
                });
    }

    public static void checkVersion(Context context, boolean force) {
        RetrofitSingleton.getInstance().fetchVersion()
                .subscribe(new SimpleSubscriber<VersionAPI>() {
                    @Override
                    public void onNext(VersionAPI versionAPI) {
                        String firVersionName = versionAPI.versionShort;
                        String currentVersionName = Util.getVersion(context);
                        if (currentVersionName.compareTo(firVersionName) < 0) {
                            showUpdateDialog(versionAPI, context);
                        } else {
                            ToastUtil.showShort("已经是最新版本(⌐■_■)");
                        }
                    }
                });
    }

    public static void checkVersionByPgy(Activity activity){
        PgyUpdateManager.register(activity, "com.hon.sunny.frank_hon",
                new UpdateManagerListener() {
                    @Override
                    public void onNoUpdateAvailable() {
                    }

                    @Override
                    public void onUpdateAvailable(String result) {
                        int currentVersionCode = Util.getVersionCode(activity);
                        VersionBean versionBean=Util.parseJsonByGson(result);
                        if(versionBean!=null){
                            int serverVersionCode=versionBean.data.versionCode;
                            if (currentVersionCode<serverVersionCode) {
                                if (SharedPreferenceUtil.getInstance().getInt("version_code", 0)!=serverVersionCode) {
                                    showUpdateDialog(versionBean, activity);
                                }
                            }
                        }
                    }
                }
        );
    }

    private static void showUpdateDialog(VersionAPI versionAPI, final Context context) {
        String title = "发现新版" + versionAPI.name + "版本号：" + versionAPI.versionShort;

        new AlertDialog.Builder(context).setTitle(title)
                .setMessage(versionAPI.changelog)
                .setPositiveButton("下载", (dialog, which) -> {
                    Uri uri = Uri.parse(versionAPI.updateUrl);   //指定网址
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);           //指定Action
                    intent.setData(uri);                            //设置Uri
                    context.startActivity(intent);        //启动Activity
                })
                .setNegativeButton("跳过此版本", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferenceUtil.getInstance().putString("version", versionAPI.versionShort);
                    }
                })
                .show();
    }

    private static void showUpdateDialog(VersionBean versionBean,Context context){
        String title = "ersion "+versionBean.data.versionName+" is available !";

        new AlertDialog.Builder(context).setTitle(title)
                .setMessage(versionBean.data.relaseNote)
                .setPositiveButton("Download", (dialog, which) -> {
                    Uri uri = Uri.parse(versionBean.data.appUrl);   //指定网址
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);           //指定Action
                    intent.setData(uri);                            //设置Uri
                    context.startActivity(intent);        //启动Activity
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferenceUtil.getInstance().putInt("version_code", versionBean.data.versionCode);
                    }
                })
                .show();
    }
}

