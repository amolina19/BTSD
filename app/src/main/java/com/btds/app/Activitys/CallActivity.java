package com.btds.app.Activitys;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.Modelos.Llamada;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class CallActivity extends AppCompatActivity {

    private static final String APP_KEY = "b464b750-1044-4c6c-91f0-d35dde526b58";
    private static final String APP_SECRET = "go21mH/XrUSnK12FmjNtdA==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Call call;
    private CallClient callClient;
    private SinchClient sinchClient;
    private Usuario usuarioChat;
    private Usuario usuarioActual;
    private Llamada llamada;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private CircleButton endcallButton;
    private CircleButton acceptCallButton;
    private CircleButton denyCallButton;
    private CircleImageView circleImageViewCall;
    private TextView usuarioTextView;
    private Chronometer chronometer;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        llamada = bundle.getParcelable("llamada");
        usuarioChat = bundle.getParcelable("UsuarioChat");
        usuarioActual = bundle.getParcelable("UsuarioActual");

        usuarioTextView = findViewById(R.id.usuarioLlamada);
        circleImageViewCall = findViewById(R.id.circleImageViewCall);
        chronometer =  findViewById(R.id.timerCall);
        chronometer.setVisibility(View.GONE);
        chronometer.setFormat("%s");
        denyCallButton = findViewById(R.id.denyCallButton);
        acceptCallButton = findViewById(R.id.acceptCallButton);
        endcallButton = findViewById(R.id.endCallButton);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //v.vibrate(VibrationEffect.createOneShot(0, VibrationEffect.DEFAULT_AMPLITUDE));
            assert v != null;
            v.vibrate(VibrationEffect.createWaveform(vibratorPatern(), 1));
        } else {
            //deprecated in API 26
            assert v != null;
            v.vibrate(60000);
        }

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(firebaseUser.getUid())
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        //recipientID = intent.getStringExtra("recipientID");

        sinchClient.setSupportCalling(true);
        //sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        endcallButton.setVisibility(View.GONE);
        endcallButton.setClickable(false);

        if(llamada == null){
            v.cancel();
            //usuarioChat = bundle.getParcelable("UsuarioChat");
            endcallButton.setVisibility(View.VISIBLE);
            acceptCallButton.setVisibility(View.GONE);
            acceptCallButton.setClickable(false);
            denyCallButton.setVisibility(View.GONE);
            denyCallButton.setClickable(false);

            if (call == null) {
                call = sinchClient.getCallClient().callUser(usuarioChat.getId().toLowerCase());
                Log.d("DEBUG CallActivity ","Calling "+ usuarioChat.getUsuario().toLowerCase());
            }

        }else{
            usuarioChat = llamada.getUsuarioOrigen();
        }

        endcallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarLlamada();
            }
        });

        acceptCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cogerLlamada();
            }
        });
        //RotateDrawable rotateDrawable = (RotateDrawable) acceptCallButton.getDrawable();
        //rotateDrawable.setLevel(500);
        denyCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarLlamada();
            }
        });

        if (!CallActivity.this.isFinishing()) {
            assert usuarioChat != null;
            usuarioTextView.setText(usuarioChat.getUsuario());
            //TESTING


            if(usuarioChat.getImagenURL().contentEquals("default")){
                Glide.with(CallActivity.this).load(R.drawable.default_user_picture).into(circleImageViewCall);
            }else{
                Glide.with(getApplicationContext()).load(usuarioChat.getImagenURL()).into(circleImageViewCall);
            }

        }

        endcallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarLlamada();
            }
        });

        assert usuarioChat != null;
        String callerID = usuarioChat.getUsuario().toLowerCase();


        sinchClient.getCallClient().addCallClientListener((callClient, call) -> {
            Log.d("DEBUG CallActivity ","CREATED SinchCallClientListener");
        });

        sinchClient.addSinchClientListener(new SinchClientListener() {

            public void onClientStarted(SinchClient client) {
                Log.d("CallActivity ","VOIP Client Started");
            }

            public void onClientStopped(SinchClient client) {
                Log.d("CallActivity ","VOIP Client Stopped");
            }

            public void onClientFailed(SinchClient client, SinchError error) {
                Log.d("CallActivity ","VOIP Client Failed");
            }

            @Override
            public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {

            }
            public void onLogMessage(int level, String area, String message) { }
        });
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            Log.d("DEBUG CallActivity SinchCallListener","Call Ended");
            call = null;
            SinchError a = endedCall.getDetails().getError();
            //button.setText("Call");
            //callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            //callState.setText("connected");
            Log.d("DEBUG CallActivity SinchCallListener","Call Established");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            Log.d("DEBUG CallActivity SinchCallListener","Call Progressing");
        }

    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient clientCall, Call incomingCall) {
            call = incomingCall;
            callClient = clientCall;
            Log.d("DEBUG CallActivity ","OnIncomingCall idCall "+call.getCallId()+" STATUS "+call.getState()+" DETAILS "+call.getDetails()+" STATUS "+call.getState());
            Toast.makeText(CallActivity.this, "incoming call", Toast.LENGTH_SHORT).show();
            //call.answer();
            call.addCallListener(new SinchCallListener());
            //button.setText("Hang Up");
        }
    }

    public void cogerLlamada(){

        if(call != null){
            v.cancel();
            acceptCallButton.setVisibility(View.GONE);
            acceptCallButton.setClickable(false);
            llamada.setAceptada(true);
            Funciones.getLlamadasReference().child(usuarioChat.getId()).child(llamada.getIdLlamada()).setValue(llamada);
            denyCallButton.setVisibility(View.GONE);
            denyCallButton.setClickable(false);
            endcallButton.setVisibility(View.VISIBLE );
            chronometer.setVisibility(View.VISIBLE);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        }

    }

    public void finalizarLlamada(){

        if(call != null){
            call.hangup();
            call = null;
            int elapsed = (int)(SystemClock.elapsedRealtime()-chronometer.getBase());
            List<Integer> tiempoTranscurrido = Funciones.obtenerTiempoLlamada(elapsed);
            llamada.setTiempoTranscurrido(tiempoTranscurrido);
            llamada.setFinalizado(true);
            Funciones.getLlamadasReference().child(usuarioChat.getId()).child(llamada.getIdLlamada()).setValue(llamada);
            v.cancel();
            Intent intentChat = new Intent(CallActivity.this, MessageActivity.class);
            assert usuarioChat != null;
            intentChat.putExtra("userID", usuarioChat.getId());
            startActivity(intentChat);
            finish();
        }else{
            int elapsed = (int)(SystemClock.elapsedRealtime()-chronometer.getBase());
            List<Integer> tiempoTranscurrido = Funciones.obtenerTiempoLlamada(elapsed);
            llamada.setTiempoTranscurrido(tiempoTranscurrido);
            llamada.setFinalizado(true);
            Funciones.getLlamadasReference().child(usuarioChat.getId()).child(llamada.getIdLlamada()).setValue(llamada);
            v.cancel();
            Intent intentChat = new Intent(CallActivity.this, MessageActivity.class);
            assert usuarioChat != null;
            intentChat.putExtra("userID", usuarioChat.getId());
            startActivity(intentChat);
            finish();
        }
    }

    public long[] vibratorPatern(){

        long[] pattern = new long[20];
        pattern[0] = 2000; // Wait one second
        pattern[1] = 2000;  // Vibrate for most a second
        pattern[2] = 2000;   // A pause long enough to feel distinction
        pattern[3] = 2000;  // Repeat 3 more times
        pattern[4] = 2000;
        pattern[5] = 2000;
        return pattern;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(usuarioActual != null){
            Funciones.actualizarConexion(getResources().getString(R.string.online),usuarioActual);
        }
    }

}
