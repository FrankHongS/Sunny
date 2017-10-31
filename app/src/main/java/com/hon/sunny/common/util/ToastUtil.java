package com.hon.sunny.common.util;

import android.widget.Toast;

import com.hon.sunny.Sunny;

/**
 * Created by Frank on 2017/8/9.
 * E-mail:frank_hon@foxmail.com
 */

public class ToastUtil {

    private static Toast sToast=null;

    private ToastUtil(){}

    public static void showShort(String content){
        if(sToast==null){
            synchronized (ToastUtil.class){
                if(sToast==null){
                    sToast=Toast.makeText(Sunny.sAppContext,content,Toast.LENGTH_SHORT);
                }
            }
        }else {
            sToast.setText(content);
        }
        sToast.show();
    }

    public static void showLong(String content){
        if(sToast==null){
            synchronized (ToastUtil.class){
                if(sToast==null){
                    sToast=Toast.makeText(Sunny.sAppContext,content,Toast.LENGTH_LONG);
                }
            }
        }else {
            sToast.setText(content);
        }
        sToast.show();
    }
}
