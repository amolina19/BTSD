package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.R;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

public class CallActivity extends AppCompatActivity {

    private static final String APP_KEY = "b464b750-1044-4c6c-91f0-d35dde526b58";
    private static final String APP_SECRET = "go21mH/XrUSnK12FmjNtdA==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Call call;
    private TextView estadoLlamada;
    private SinchClient sinchClient;
    private Button button;
    private String callerID;
    private String recipientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        Intent intent = getIntent();
        callerID = intent.getStringExtra("callerID");
        recipientID = intent.getStringExtra("recipientID");



        sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, Call call) {

            }
        });


    }
}
