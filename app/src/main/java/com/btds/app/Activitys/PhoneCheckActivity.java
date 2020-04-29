package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneCheckActivity extends AppCompatActivity {

    private EditText editTextMobile;
    CountryCodePicker codeCountryPicker;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //String systemLanguage;
    //String verificationid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_check);
        codeCountryPicker = findViewById(R.id.codeCountryPicker);

        //systemLanguage = Funciones.getSystemLanguage();
        Log.d("DEBUG IDIOMA SISTEMA", Funciones.getSystemLanguage());


        if(!(Funciones.getCountryCode() == -1)){
            codeCountryPicker.setCountryForPhoneCode(Funciones.getCountryCode());
        }else{
            codeCountryPicker.setCountryForNameCode("USA");
        }
        codeCountryPicker.changeLanguage(Funciones.obtainLanguageContryPicker());

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

            mobile = "+"+Funciones.getCountryCode()+""+mobile;
            //Log.d("DEBUG TELEFONO INTRODUCIDO",mobile);

            Intent intent = new Intent(PhoneCheckActivity.this, PhoneVerifyActivity.class);
            intent.putExtra("phonenumber", mobile);
            Log.d("DEBUG TELEFONO INTRODUCIDO",mobile);
            startActivity(intent);
            //enviarVerificacionTelefono(mobile);


        });
    }

    private void enviarVerificacionTelefono(String phoneNum){
        phoneNum = "666662442";
        String testVerificationCode = "123456";
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
                    }

                });
    }



}
