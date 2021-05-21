package com.galaxydefenders.konnex.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.galaxydefenders.konnex.R;
import com.galaxydefenders.konnex.adapter.AdapterChat;
import com.galaxydefenders.konnex.model.Message;
import com.galaxydefenders.konnex.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private ImageView btn_close;
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
        final NavController navController = NavHostFragment.findNavController(this);
        initComponent(v);
        btn_close = v.findViewById(R.id.btn_close);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_chatFragment_pop);
            }
        });

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//        hideKeyboard(v);

        return v;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }



    public void initComponent(View v) {
        recycler_view = v.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);
//        ((LinearLayoutManager)recycler_view.getLayoutManager()).setStackFromEnd(true);
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



        String jsonString = "{\"msg\":\"" + msg + "\"}";
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
                        Log.i("Chat_Response", res);
                        JSONObject responseJSON = new JSONObject(res);
                        adapter.insertItem(new Message(adapter.getItemCount(), responseJSON.getString("reply"), false, adapter.getItemCount() % 5 == 0));
                        recycler_view.scrollToPosition(adapter.getItemCount() - 1);

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