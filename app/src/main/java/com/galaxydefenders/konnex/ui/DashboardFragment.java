package com.galaxydefenders.konnex.ui;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.galaxydefenders.konnex.R;
import com.galaxydefenders.konnex.model.Message;
import com.galaxydefenders.konnex.network.RetrofitClient;
import com.galaxydefenders.konnex.utils.ViewAnimation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.fragment.app.Fragment;
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
    private View lyt_goto;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        parent_view = v.findViewById(R.id.coordinator_dashboard);
        back_drop = v.findViewById(R.id.back_drop);

        final FloatingActionButton fab_chat = v.findViewById(R.id.fab_chat);
        final FloatingActionButton fab_feedback = v.findViewById(R.id.fab_feedback);
        final FloatingActionButton fab_bug = v.findViewById(R.id.fab_bug);
        final FloatingActionButton fab_goto = v.findViewById(R.id.fab_goto);
        final FloatingActionButton fab_add = v.findViewById(R.id.fab_add);

        lyt_chat = v.findViewById(R.id.lyt_chat);
        lyt_feedback = v.findViewById(R.id.lyt_feedback);
        lyt_bug = v.findViewById(R.id.lyt_bug);
        lyt_goto = v.findViewById(R.id.lyt_goto);

        ViewAnimation.initShowOut(lyt_chat);
        ViewAnimation.initShowOut(lyt_feedback);
        ViewAnimation.initShowOut(lyt_bug);
        ViewAnimation.initShowOut(lyt_goto);

        back_drop.setVisibility(View.GONE);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(v);
            }
        });

        back_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(fab_add);
            }
        });


        return v;
    }

    private void toggleFabMode(View v) {
        rotate = ViewAnimation.rotateFab(v, !rotate);
        if (rotate) {
            ViewAnimation.showIn(lyt_chat);
            ViewAnimation.showIn(lyt_feedback);
            ViewAnimation.showIn(lyt_bug);
            ViewAnimation.showIn(lyt_goto);
            back_drop.setVisibility(View.VISIBLE);
        } else {
            ViewAnimation.showOut(lyt_chat);
            ViewAnimation.showOut(lyt_feedback);
            ViewAnimation.showOut(lyt_bug);
            ViewAnimation.showOut(lyt_goto);
            back_drop.setVisibility(View.GONE);
        }
    }


    private void showDialogPrescriptionConfirmation() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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














                    String paid = "Processing";
                    RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
                    int id = radioGroup.getCheckedRadioButtonId();

                    if (id == R.id.radioButtonYes) {
                        paid = "Processed";

                        mViewModel.confirmPrescription(jwt, appointmentId, paid, amountText);
                        savePrescription();
                        Toast.makeText(getContext(), "Prescription sent to " + individualName, Toast.LENGTH_LONG).show();
                        dialog.hide();

                    } else if (id == R.id.radioButtonNo) {
                        paid = "Processing";

                        mViewModel.confirmPrescription(jwt, appointmentId, paid, amountText);
                        savePrescription();
                        Toast.makeText(getContext(), "Prescription sent to " + individualName, Toast.LENGTH_LONG).show();
                        dialog.hide();

                    }else{
                        Toast.makeText(getContext(), "Please select Payment Status", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        Button btn_cancel = dialog.findViewById(R.id.bt_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });


        dialog.show();
    }

}