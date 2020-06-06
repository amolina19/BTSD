package com.btds.app.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneCheckActivity extends AppCompatActivity {

    private EditText editTextMobile;
    private static Activity activityCheckPhone;
    private TextView textView_informacion;
    private TextView textView;
    private Button buttonOmitir;
    private Button buttonSalir;
    CountryCodePicker codeCountryPicker;
    final private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Usuario usuarioActual;
    String phoneCode;
    //String systemLanguage;
    //String verificationid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_check);
        Log.d("DEBUG ","PhoneCheckActivity Created");
        activityCheckPhone = this;
        codeCountryPicker = findViewById(R.id.codeCountryPicker);
        buttonOmitir = findViewById(R.id.buttonOmitir);
        textView_informacion = findViewById(R.id.textView_informacion);
        textView = findViewById(R.id.textView);
        buttonSalir = findViewById(R.id.buttonSalir);
        textView_informacion.setError(getResources().getString(R.string.OmitirMasTarde));

        Intent intentMain = getIntent();
        Bundle bundle = intentMain.getExtras();
        usuarioActual = bundle.getParcelable("usuario");

        buttonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goLogin = new Intent(PhoneCheckActivity.this,LoginActivity.class);
                startActivity(goLogin);
                Funciones.actualizarT2A(false, usuarioActual);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
        if(!(usuarioActual.getTwoAunthenticatorFactor() == null)){
            if(usuarioActual.getTwoAunthenticatorFactor()){
                T2A();
            }
        }


        buttonOmitir.setOnClickListener(v -> {
            assert firebaseUser != null;
            Funciones.getActualUserDatabaseReference(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Usuario usuarioActual = dataSnapshot.getValue(Usuario.class);
                    usuarioActual.setTwoAunthenticatorFactor(false);

                    Funciones.getActualUserDatabaseReference(firebaseUser).setValue(usuarioActual).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("DEBUG Phone Check","Verificacion omitida");
                            Intent intent = new Intent(PhoneCheckActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
        //systemLanguage = Funciones.getSystemLanguage();
        Log.d("DEBUG IDIOMA SISTEMA", Funciones.getSystemLanguage());




        if(!(Funciones.getCountryCode() == -1)){
            codeCountryPicker.setCountryForPhoneCode(Funciones.getCountryCode());
        }else{
            codeCountryPicker.setCountryForNameCode("USA");
        }
        codeCountryPicker.changeLanguage(Funciones.obtainLanguageContryPicker());
        phoneCode = "+"+ Funciones.getCountryCode();

        codeCountryPicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                phoneCode = codeCountryPicker.getSelectedCountryCodeWithPlus();
            }
        });

        //firebaseAuth.useAppLanguage();
        //mAuth.setLanguageCode("es");

        editTextMobile = findViewById(R.id.editTextMobile);

        findViewById(R.id.buttonContinue).setOnClickListener(v -> {

            String mobile = editTextMobile.getText().toString().trim();

            if(mobile.isEmpty() || mobile.length() < 6){
                editTextMobile.setError("Enter a valid mobile");
                editTextMobile.requestFocus();
                return;
            }

            mobile = phoneCode+""+mobile;
            //Log.d("DEBUG TELEFONO INTRODUCIDO",mobile);

            Intent intent = new Intent(PhoneCheckActivity.this, PhoneVerifyActivity.class);
            intent.putExtra("phonenumber", mobile);
            Log.d("DEBUG TELEFONO INTRODUCIDO",mobile);
            startActivity(intent);
            //enviarVerificacionTelefono(mobile);


        });
    }

    public static Activity getInstanceCheckPhone(){
        return activityCheckPhone;
    }

    private void enviarVerificacionTelefono(String phoneNum){
        //phoneNum = "666662442";
        //String testVerificationCode = "123456";
        // Whenever verification is triggered with the whitelisted number,
        // provided it is not set for auto-retrieval, onCodeSent will be triggered.

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60 /*timeout*/, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // Save the verification id somewhere
                // ...

                // The corresponding whitelisted code above should be used to complete sign-in.
                //MainActivity.this.enableUserManuallyInputCode();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // Sign in with the credential
                // ...
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // ...
                //UNFINISHED
            }

        });
    }

    private void T2A(){
        textView_informacion.setVisibility(View.GONE);
        buttonOmitir.setVisibility(View.GONE);
        buttonOmitir.setClickable(false);
        textView.setText(R.string.introduceTelefonoT2A);
        buttonSalir.setVisibility(View.VISIBLE);
        buttonSalir.setClickable(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(usuarioActual != null){
            Funciones.actualizarConexion(getResources().getString(R.string.online),usuarioActual);
        }
    }


}
