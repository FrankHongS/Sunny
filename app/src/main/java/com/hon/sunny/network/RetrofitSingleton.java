package com.hon.sunny.network;

import com.hon.mylogger.MyLogger;
import com.hon.sunny.BuildConfig;
import com.hon.sunny.Sunny;
import com.hon.sunny.component.OrmLite;
import com.hon.sunny.ui.about.domain.VersionAPI;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.utils.RxUtils;
import com.hon.sunny.utils.ToastUtil;
import com.hon.sunny.utils.Util;
import com.hon.sunny.vo.bean.main.CityORM;
import com.hon.sunny.vo.bean.main.Weather;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Frank on 2017/8/10.
 * E-mail:frank_hon@foxmail.com
 */
public class RetrofitSingleton {

    private static RetrofitSingleton sRetrofitSingleton;
    private static ApiInterface sApiService = null;
    private static Retrofit sRetrofit = null;
    private static OkHttpClient sOkHttpClient = null;

    private RetrofitSingleton() {
        init();
    }

    public static RetrofitSingleton getInstance() {
//        return SingletonHolder.INSTANCE;
        if (sRetrofitSingleton == null) {
            synchronized (RetrofitSingleton.class) {
                if (sRetrofitSingleton == null) {
                    sRetrofitSingleton = new RetrofitSingleton();
                }
            }
        }
        return sRetrofitSingleton;
    }

    private static void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            // https://drakeet.me/retrofit-2-0-okhttp-3-0-config
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }


        // 缓存 http://www.jianshu.com/p/93153b34310e
        File cacheFile = new File(Sunny.getAppCacheDir() + "/NetCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);


        Interceptor cacheInterceptor = chain -> {
            Request request = chain.request();
            if (!Util.isNetworkConnected(Sunny.getAppContext())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            Response.Builder newBuilder = response.newBuilder();
            if (Util.isNetworkConnected(Sunny.getAppContext())) {
                int maxAge = 0;
                // 有网络时 设置缓存超时时间0个小时
                newBuilder
                        .header("Cache-Control", "public, max-age=" + maxAge);
            } else {
                // 无网络时，设置超时为4周
                int maxStale = 60 * 60 * 24 * 28;
                newBuilder

                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale);
            }
            return newBuilder.build();
        };
        builder.cache(cache).addInterceptor(cacheInterceptor);
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        sOkHttpClient = builder.build();
    }

    private static void initRetrofit() {
        sRetrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.HOST)
                .client(sOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static void disposeFailureInfo(Throwable t) {
        if (t.toString().contains("GaiException") || t.toString().contains("SocketTimeoutException") ||
                t.toString().contains("UnknownHostException") || t.toString().contains("504")) {
            ToastUtil.showShort("网络问题");
        } else if (t.toString().contains("API没有")) {
            OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", Util.replaceInfo(t.getMessage())));
            MyLogger.w(Util.replaceInfo(t.getMessage()));
            ToastUtil.showShort("错误: " + t.getMessage());
        }
        MyLogger.e(t.getMessage());
    }

    private void init() {
        initOkHttp();
        initRetrofit();
        sApiService = sRetrofit.create(ApiInterface.class);
    }

    public ApiInterface getApiService() {
        return sApiService;
    }

    public Flowable<Weather> fetchWeather(String city) {
        return sApiService.weather(city, Constants.KEY)
                .flatMap(weatherContainer -> {
                    String status = weatherContainer.weatherList.get(0).status;
                    if ("no more requests".equals(status)) {
                        return Flowable.error(new RuntimeException("/(ㄒoㄒ)/~~,API免费次数已用完"));
                    } else if ("unknown city".equals(status)) {
                        return Flowable.error(new RuntimeException(String.format("API没有%s", city)));
                    }
                    return Flowable.just(weatherContainer);
                })
                .map(weatherContainer -> {
                    Weather weather = weatherContainer.weatherList.get(0);
                    weather.city = city;
                    return weather;
                })
                .flatMap(weather -> sApiService
                        .weatherQuality(city, Constants.KEY)
                        .map(weatherQuality -> {
                            if (weatherQuality != null && weatherQuality.qualityList.get(0).airNowCity != null) {
                                weather.quality = weatherQuality.qualityList.get(0).airNowCity.quality;
                                weather.pm25 = weatherQuality.qualityList.get(0).airNowCity.pm25;
                                weather.aqi = weatherQuality.qualityList.get(0).airNowCity.aqi;
                            }
                            return weather;
                        })
                )
                .subscribeOn(Schedulers.io());
    }

    public Observable<VersionAPI> fetchVersion() {
        return sApiService.mVersionAPI(Constants.API_TOKEN)
                .compose(RxUtils.rxSchedulerHelper());
    }
}
