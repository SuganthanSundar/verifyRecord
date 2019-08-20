package com.quantrium.verifydoc;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SetURL {
    private Retrofit retrofit;
    private static SetURL setupApi;
    private static final String BASE_URL="http://192.168.2.38:5000/";
   // http://192.168.2.84:7000/panVerification
    //http://192.168.2.38:5000/
    //http://192.168.2.16/MobilityService_Testing/matex.svc/help/operations/MobilityTracker
    SetURL(){
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient= new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();
        Retrofit.Builder builder=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());
        retrofit= builder.client(okHttpClient).build();

    }
    public static SetURL getInstance(){
        if(setupApi ==null){
            synchronized (SetURL.class){
                setupApi =new SetURL();
            }
        }
        return setupApi;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
