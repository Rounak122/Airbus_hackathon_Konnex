package com.galaxydefenders.konnex.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.galaxydefenders.konnex.R;
import com.galaxydefenders.konnex.adapter.AdapterChat;
import com.galaxydefenders.konnex.model.Message;
import com.galaxydefenders.konnex.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private ImageView btn_send;
    private EditText et_content;
    private AdapterChat adapter;
    private RecyclerView recycler_view;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        NavController navController = NavHostFragment.findNavController(this);

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        MaterialToolbar toolbar = v.findViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar,navController,appBarConfiguration);

        initComponent(v);




        return v;
    }


    public void initComponent(View v) {
        recycler_view = v.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);

        adapter = new AdapterChat(getContext());
        recycler_view.setAdapter(adapter);
        adapter.insertItem(new Message(adapter.getItemCount(), "Hello!", false, adapter.getItemCount() % 5 == 0));
//        adapter.insertItem(new Message(adapter.getItemCount(), "Hello!", true, adapter.getItemCount() % 5 == 0, Tools.getFormattedTimeEvent(System.currentTimeMillis())));

        btn_send = v.findViewById(R.id.btn_send);
        et_content = v.findViewById(R.id.text_content);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChat();
            }
        });
    }

    private void sendChat() {
        final String msg = et_content.getText().toString();
        if(msg.isEmpty()) return;
        adapter.insertItem(new Message(adapter.getItemCount(), msg, true, adapter.getItemCount() % 5 == 0));
        et_content.setText("");
        recycler_view.scrollToPosition(adapter.getItemCount() - 1);



        String jsonString = "{\"msg\":\"" + msg + "}";
        RequestBody requestBody=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonString);

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getReply(requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {

                        String res=response.body().string();
                        Log.i("LOGIN", "USERNAME RESPONSE: ---------------------------->" + res);
                        JSONObject responseJSON = new JSONObject(res);
                        boolean error = responseJSON.getBoolean("error");
                        if (error){
                            Toast.makeText(getContext(), "Cannot connect to the bot", Toast.LENGTH_SHORT).show();
                        }else {
                            adapter.insertItem(new Message(adapter.getItemCount(), responseJSON.getString("reply"), false, adapter.getItemCount() % 5 == 0));
                            recycler_view.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Internet not available", Toast.LENGTH_SHORT).show();
            }
        });



//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                adapter.insertItem(new Message(adapter.getItemCount(), msg, false, adapter.getItemCount() % 5 == 0));
//                recycler_view.scrollToPosition(adapter.getItemCount() - 1);
//            }
//        }, 100);


    }





}