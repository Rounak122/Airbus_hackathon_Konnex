package com.galaxydefenders.konnex.network;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

        private static final String OPERATOR_BASE_URL = "http://wr.rounak.tech/";

        private static RetrofitClient mInstance;
        private Retrofit retrofit;

        private RetrofitClient(){

            retrofit= new Retrofit.Builder()
                    .baseUrl(OPERATOR_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        public static synchronized  RetrofitClient getInstance(){
            if (mInstance==null){
                mInstance=new RetrofitClient();
            }
            return mInstance;
        }


        public Api getApi(){

            return retrofit.create(Api.class);

        }
}
