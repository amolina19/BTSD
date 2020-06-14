package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.vdx.designertoast.DesignerToast;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author Alejandro Molina Louchnikov
 */

public class PhoneVerifyActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private String verificationid, nTelefono;
    private EditText editText;
    private DatabaseReference databaseUserReference;
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);
        progressBar = findViewById(R.id.progressbar);

        editText = findViewById(R.id.editTextCode);
        Button buttonVerificacion = findViewById(R.id.buttonEnviarVerificacion);

        nTelefono = getIntent().getStringExtra("phonenumber");
        enviarVerificacion(nTelefono);


        buttonVerificacion.setOnClickListener(v -> {
            String codigoIntroducido = editText.getText().toString().trim();

            if ((codigoIntroducido.isEmpty() || codigoIntroducido.length() < 6)){

                editText.setError(getResources().getString(R.string.introduceElCodigoVerf));
                editText.requestFocus();
                return;
            }
            verificarCodigo(codigoIntroducido);
        });
    }

    private void enviarVerificacion(String number){
        Funciones.getPhoneAuthProvider().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
    }

    private void verificarCodigo(String codigo){
        PhoneAuthCredential credencial = PhoneAuthProvider.getCredential(verificationid, codigo);
        editText.setText(credencial.getSmsCode());

        assert firebaseUser != null;
        databaseUserReference = Funciones.getUsersDatabaseReference().child(firebaseUser.getUid());

        databaseUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                if(usuario !=null){
                    usuario.setTelefono(nTelefono);
                    usuario.setTwoAunthenticatorFactor(true);
                    Funciones.actualizarT2A(true,usuario);
                    Funciones.mostrarDatosUsuario(usuario);
                    // Toast.makeText(PhoneVerifyActivity.this, getResources().getString(R.string.cambiosGuardados), Toast.LENGTH_SHORT).show();
                    databaseUserReference.setValue(usuario).addOnCompleteListener(Task::isSuccessful);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent mainActivity = new Intent(PhoneVerifyActivity.this,MainActivity.class);
        startActivity(mainActivity);
        PhoneCheckActivity.getInstanceCheckPhone().finish();
        finish();
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NotNull String s, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String codigoSMSrecibido = phoneAuthCredential.getSmsCode();
            assert codigoSMSrecibido != null;
            progressBar.setVisibility(View.VISIBLE);
            verificarCodigo(codigoSMSrecibido);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            DesignerToast.Error(PhoneVerifyActivity.this, getResources().getString(R.string.introduceTlefonoValido), Gravity.BOTTOM, Toast.LENGTH_SHORT);
            Toast.makeText(PhoneVerifyActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

}
