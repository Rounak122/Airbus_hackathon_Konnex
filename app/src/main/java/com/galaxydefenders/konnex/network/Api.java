package com.galaxydefenders.konnex.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

        //Chat
        @POST("chatbot")
        Call<ResponseBody> getReply(@Body RequestBody requestBody);

        //Feedback
        @POST("feedback")
        Call<ResponseBody> postFeedback(@Body RequestBody requestBody);

        //Bug
        @POST("bugs")
        Call<ResponseBody> postBug(@Body RequestBody requestBody);

        //Announcements
        @GET("announcement")
        Call<ResponseBody> getAnnouncements();




}
