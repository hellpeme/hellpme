package com.example.vincius.myapplication.Fragments;

import com.example.vincius.myapplication.Notifications.MyResponse;
import com.example.vincius.myapplication.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA_lROVMM:APA91bGgk-ek1geJTw-h6CoGsdH4KVa_uxEnvFIwY2pA3UB-duBuRBLGioUriNqBHgetKpJnHYeglG6K8EivM44EgRA22wHhVpZ4WwBVFg3zXL6WBpCwyhj_HeszdlleFwecZhIzJ58f"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
