package com.hon.sunny.ui.about.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Frank on 2017/9/27.
 * E-mail:frank_hon@foxmail.com
 */

public class VersionBean {
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;
    @SerializedName("code")
    public String code;

    public static class Data {
        @SerializedName("versionName")
        public String versionName;
        @SerializedName("appUrl")
        public String appUrl;
        @SerializedName("lastBuild")
        public String lastBuild;
        @SerializedName("build")
        public String build;
        @SerializedName("releaseNote")
        public String relaseNote;
        @SerializedName("dowmloadURL")
        public String dowmloadURL;
        @SerializedName("versionCode")
        public int versionCode;
    }
}
