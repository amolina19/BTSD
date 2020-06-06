package com.btds.app.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.Visibilidad;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;

import in.shrinathbhosale.preffy.Preffy;

public class AjustesActivity extends AppCompatActivity {


    Usuario usuario;
    Visibilidad visibilidad;

    Button buttonContrasena;
    Button buttonEmail;
    //Switchs

    Switch usuarioSwitch;
    Switch descripcionSwitch;
    Switch telefonoSwitch;
    Switch fotoSwitch;
    Switch lineaSwitch;
    Switch autenticacionSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        Preffy preffy = Preffy.getInstance(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        assert bundle != null;
        usuario = bundle.getParcelable("usuarioAjustes");
        assert usuario != null;
        visibilidad = usuario.getVisibilidad();

        usuarioSwitch = findViewById(R.id.ajustes_switch_usuario);
        descripcionSwitch = findViewById(R.id.ajustes_switch_descripcion);
        telefonoSwitch = findViewById(R.id.ajustes_switch_telefono);
        fotoSwitch = findViewById(R.id.ajustes_switch_foto);
        lineaSwitch = findViewById(R.id.ajustes_switch_linea);
        autenticacionSwitch = findViewById(R.id.ajustes_switch_T2A);
        buttonContrasena = findViewById(R.id.ajustes_button_contrasena);
        buttonEmail = findViewById(R.id.ajustes_button_email);

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentEmail = new Intent(AjustesActivity.this,ChangeEmailActivity.class);
                startActivity(intentEmail);
            }
        });

        buttonContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cambiarPasswd = new Intent(AjustesActivity.this,ChangePasswordActivity.class);
                startActivity(cambiarPasswd);
            }
        });

        if(visibilidad.getUsuario()){
            usuarioSwitch.setChecked(true);
        }else{
            usuarioSwitch.setChecked(false);
        }

        if(visibilidad.getDescripcion()){
            descripcionSwitch.setChecked(true);
        }else{
            descripcionSwitch.setChecked(false);
        }

        if(visibilidad.getTelefono()){
            telefonoSwitch.setChecked(true);
        }else{
            telefonoSwitch.setChecked(false);
        }

        if(visibilidad.getFoto()){
            fotoSwitch.setChecked(true);
        }else{
            fotoSwitch.setChecked(false);

        }

        if(visibilidad.getEnLinea()){
            lineaSwitch.setChecked(true);
        }else{
            lineaSwitch.setChecked(false);
        }

        if (usuario.getTwoAunthenticatorFactor()){
            autenticacionSwitch.setChecked(true);
        }else{
            autenticacionSwitch.setChecked(false);
        }


        usuarioSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                visibilidad.setUsuario(true);
                preffy.putString("VisibilidadUsuario", "true");
            }else{
                visibilidad.setUsuario(false);
                preffy.putString("VisibilidadUsuario", "false");
            }
            usuario.setVisibilidad(visibilidad);
            Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
        });

        descripcionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                visibilidad.setDescripcion(true);
                preffy.putString("VisibilidadDescripcion", "true");
            }else{
                visibilidad.setDescripcion(false);
                preffy.putString("VisibilidadDescripcion", "false");
            }
            usuario.setVisibilidad(visibilidad);
            Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
        });

        telefonoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                visibilidad.setTelefono(true);
                preffy.putString("VisibilidadTelefono", "true");
            }else{
                visibilidad.setTelefono(false);
                preffy.putString("VisibilidadTelefono", "false");
            }
            usuario.setVisibilidad(visibilidad);
            Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
        });

        fotoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                visibilidad.setFoto(true);
                preffy.putString("VisibilidadFoto", "true");
            }else{
                visibilidad.setFoto(false);
                preffy.putString("VisibilidadFoto", "false");
            }
            usuario.setVisibilidad(visibilidad);
            Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
        });

        lineaSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                visibilidad.setEnLinea(true);
                preffy.putString("VisibilidadLinea", "true");
            }else{
                visibilidad.setEnLinea(false);
                preffy.putString("VisibilidadLinea", "false");
            }
            usuario.setVisibilidad(visibilidad);
            Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
        });

        autenticacionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                usuario.setTwoAunthenticatorFactor(true);
            }else{
                usuario.setTwoAunthenticatorFactor(false);
            }
            Funciones.getUsersDatabaseReference().child(usuario.getId()).setValue(usuario);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(usuario != null){
            Funciones.actualizarConexion(getResources().getString(R.string.online),usuario);
        }
    }
}