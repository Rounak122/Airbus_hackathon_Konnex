package com.galaxydefenders.konnex.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

public interface Api {

        //Chat
        @GET("chatbot")
        Call<ResponseBody> getReply(@Body RequestBody requestBody);

        //Feedback
        @GET("feedback")
        Call<ResponseBody> postFeedback(@Body RequestBody requestBody);

        //Bug
        @GET("bugs")
        Call<ResponseBody> postBug(@Body RequestBody requestBody);

        //Announcements
        @GET("announcements")
        Call<ResponseBody> getAnnouncements(@Body RequestBody requestBody);




}
