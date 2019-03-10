package com.example.vincius.myapplication.Notifications;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static Retrofit retrofit = null;

    public static Retrofit getCliente(String url){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    return retrofit;
    }
}
