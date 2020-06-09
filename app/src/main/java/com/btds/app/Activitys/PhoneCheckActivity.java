package com.btds.app.Activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author Alejandro Molina Louchnikov
 */

public class PhoneCheckActivity extends AppCompatActivity {

    private EditText editTextMobile;
    private static Activity activityCheckPhone;
    private TextView textView_informacion, textView;
    private Button buttonOmitir, buttonSalir;
    CountryCodePicker codeCountryPicker;
    final private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Usuario usuarioActual;
    String phoneCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_check);
        activityCheckPhone = this;
        codeCountryPicker = findViewById(R.id.codeCountryPicker);
        buttonOmitir = findViewById(R.id.buttonOmitir);
        textView_informacion = findViewById(R.id.textView_informacion);
        textView = findViewById(R.id.textView);
        buttonSalir = findViewById(R.id.buttonSalir);
        textView_informacion.setError(getResources().getString(R.string.OmitirMasTarde));

        Intent intentMain = getIntent();
        Bundle bundle = intentMain.getExtras();
        assert bundle != null;
        usuarioActual = bundle.getParcelable("usuario");

        buttonSalir.setOnClickListener(v -> {
            Intent goLogin = new Intent(PhoneCheckActivity.this,LoginActivity.class);
            startActivity(goLogin);
            Funciones.actualizarT2A(false, usuarioActual);
            FirebaseAuth.getInstance().signOut();
            finish();
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
                    assert usuarioActual != null;
                    usuarioActual.setTwoAunthenticatorFactor(false);

                    Funciones.getActualUserDatabaseReference(firebaseUser).setValue(usuarioActual).addOnCompleteListener(task -> {
                        Intent intent = new Intent(PhoneCheckActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
        //systemLanguage = Funciones.getSystemLanguage();

        if(!(Funciones.getCountryCode() == -1)){
            codeCountryPicker.setCountryForPhoneCode(Funciones.getCountryCode());
        }else{
            codeCountryPicker.setCountryForNameCode("USA");
        }
        codeCountryPicker.changeLanguage(Funciones.obtainLanguageContryPicker());
        phoneCode = "+"+ Funciones.getCountryCode();

        codeCountryPicker.setOnCountryChangeListener(() -> phoneCode = codeCountryPicker.getSelectedCountryCodeWithPlus());

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
            Intent intent = new Intent(PhoneCheckActivity.this, PhoneVerifyActivity.class);
            intent.putExtra("phonenumber", mobile);
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
            public void onCodeSent(@NotNull String verificationId,
                                   @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // Save the verification id somewhere
                // ...

                // The corresponding whitelisted code above should be used to complete sign-in.
                //MainActivity.this.enableUserManuallyInputCode();
            }

            @Override
            public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
                // Sign in with the credential
                // ...
            }

            @Override
            public void onVerificationFailed(@NotNull FirebaseException e) {
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
