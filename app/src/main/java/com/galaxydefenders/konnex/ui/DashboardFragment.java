package com.galaxydefenders.konnex.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.galaxydefenders.konnex.R;
import com.galaxydefenders.konnex.network.RetrofitClient;
import com.galaxydefenders.konnex.utils.ViewAnimation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private View parent_view;
    private View back_drop;
    private boolean rotate = false;
    private View lyt_chat;
    private View lyt_feedback;
    private View lyt_bug;
    private boolean isBackdrop;
    private ListView announcement_list;

    FloatingActionButton fab_add;
    FloatingActionButton fab_move;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final NavController navController = NavHostFragment.findNavController(this);


        announcement_list = v.findViewById(R.id.list_announcements);
        parent_view = v.findViewById(R.id.coordinator_dashboard);
        back_drop = v.findViewById(R.id.back_drop);
        isBackdrop=false;

        final FloatingActionButton fab_chat = v.findViewById(R.id.fab_chat);
        final FloatingActionButton fab_feedback = v.findViewById(R.id.fab_feedback);
        final FloatingActionButton fab_bug = v.findViewById(R.id.fab_bug);
        fab_add = v.findViewById(R.id.fab_add);
        fab_move = v.findViewById(R.id.fab_Moving);


        lyt_chat = v.findViewById(R.id.lyt_chat);
        lyt_feedback = v.findViewById(R.id.lyt_feedback);
        lyt_bug = v.findViewById(R.id.lyt_bug);

        ViewAnimation.initShowOut(lyt_chat);
        ViewAnimation.initShowOut(lyt_feedback);
        ViewAnimation.initShowOut(lyt_bug);
        ViewAnimation.initShowOut(fab_add);

        back_drop.setVisibility(View.GONE);

//        fab_add.setVisibility(View.GONE);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            toggleFabMode();
            }
        });

        back_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode();
            }
        });



        fab_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogBug();
            }
        });

        fab_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFeedback();
            }
        });

        fab_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_nav_dashboardFragment_to_chatFragment);
            }
        });

        fab_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFabMode();
            }
        });

        fetchAnnouncements();



        return v;
    }

    private void toggleFabMode() {

        if(!isBackdrop){

            fab_add.setVisibility(View.VISIBLE);
            ViewAnimation.showIn(lyt_chat);
            ViewAnimation.showIn(lyt_feedback);
            ViewAnimation.showIn(lyt_bug);
            ViewAnimation.showIn(fab_add);
            back_drop.setVisibility(View.VISIBLE);
            fab_move.hide();
            isBackdrop=true;



















        }else{
            ViewAnimation.showOut(lyt_chat);
            ViewAnimation.showOut(lyt_feedback);
            ViewAnimation.showOut(lyt_bug);
            ViewAnimation.showOut(fab_add);
            back_drop.setVisibility(View.GONE);
            fab_move.show();
            isBackdrop=false;
        }


    }


    private void fetchAnnouncements(){

        ArrayList<String> announcements = new ArrayList<>();

        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getApi()
                .getAnnouncements();


        call.enqueue(new Callback<ResponseBody>() {
            @Override

            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {

                        Log.i("ANNOUNCE", "onResponse: SUCCESS");

                        JSONArray jsonarray = new JSONArray(response.body().string());
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            String a = jsonobject.getString("announce");
                            announcements.add(a);
                        }

                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                R.layout.item_textview, announcements);

                        announcement_list.setAdapter(adapter);


                    }
                } catch (IOException | JSONException e) {

                    Log.i("ANNOUNCE", "onResponse: FAIL");

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (Objects.equals(t.getMessage(), "Internet Not available"))

                    Toast.makeText(getContext(), "Cannot load announcements", Toast.LENGTH_SHORT).show();
//                    Log.i("ANNOUNCE", "onResponse: INTERNET UNAVIALABLE");


            }
        });

    }

    private void showDialogFeedback() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.setCancelable(true);

        Button btn_submit = dialog.findViewById(R.id.bt_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText et_msg = dialog.findViewById(R.id.et_feedback);
                String response = et_msg.getText().toString();

                if (response.equals("")){
                    Toast.makeText(getContext(), "Please enter your response", Toast.LENGTH_SHORT).show();
                }else {

                    String jsonString = "{\"feedback\":\"" + response + "\"}";
                    Log.i("DASHBOARD", "onClick: " + jsonString);

                   RequestBody requestBody=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonString);

                    Call<ResponseBody> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .postFeedback(requestBody);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Thanks for your Feedback", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "Feedback Sending Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        Button btn_cancel = dialog.findViewById(R.id.bt_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        dialog.show();
    }
    private void showDialogBug() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_bug);
        dialog.setCancelable(true);


        Button btn_submit = dialog.findViewById(R.id.bt_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText et_msg = dialog.findViewById(R.id.et_bug);
                String response = et_msg.getText().toString();

                if (response.equals("")){
                    Toast.makeText(getContext(), "Please enter your response", Toast.LENGTH_SHORT).show();
                }else {



                    String jsonString = "{\"bugs\":\"" + response + "\"}";
                    Log.i("DASHBOARD", "onClick: " + jsonString);

                    RequestBody requestBody=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),jsonString);

                    Call<ResponseBody> call = RetrofitClient
                            .getInstance()
                            .getApi()
                            .postBug(requestBody);

                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            try {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Thanks for Reporting Bug", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }else{
                                    Log.i("DASHBOARD", "onResponse: " + response.errorBody() +"   " + response.code() +"   " +response.message());

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), "Bug Reporting Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        Button btn_cancel = dialog.findViewById(R.id.bt_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        dialog.show();
    }

}