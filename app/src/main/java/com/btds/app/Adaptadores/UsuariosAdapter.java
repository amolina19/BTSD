package com.btds.app.Adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.MessageActivity;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Alejandro Molina Louchnikov
 */

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder>  {

    private static final int USUARIO_NO_BLOQUEADO = 0;
    //private static final int USUARIO_BLOQUEADO = 1;

    //private boolean firstSearch = true;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context context;
    private List<Usuario> listaUsuarios;
    private HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    //private FirebaseUser firebaseUser;


    public UsuariosAdapter(Context contexto, List<Usuario> listaUsuarios, HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados){
        this.listaUsuarios = listaUsuarios;
        this.listaUsuariosBloqueados = listaUsuariosBloqueados;
        this.context = contexto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //if(viewType == USUARIO_NO_BLOQUEADO){
            View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_friends_fragment,parent,false);
            return new com.btds.app.Adaptadores.UsuariosAdapter.ViewHolder(view);
        //}

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        holder.imagen_perfil.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.usuario.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.verPerfil.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));

        final Usuario usuario = listaUsuarios.get(posicion);
        holder.usuario.setText(usuario.getUsuario());

        //System.out.println("usuario bindeado");

        if(usuario.getImagenURL().equals("default")){
            //holder.imagen_perfil.setImageResource(R.mipmap.ic_launcher);
            //private static final int USUARIO_BLOQUEADO = 1;
            Glide.with(context).load(R.drawable.default_user_picture).into(holder.imagen_perfil);
        }else{
            Glide.with(context).load(usuario.getImagenURL()).into(holder.imagen_perfil);
        }


        if(firebaseUser != null && !listaUsuariosBloqueados.containsKey(firebaseUser.getUid()+usuario.getId())){

            if(usuario.getEstado().contentEquals(context.getResources().getString(R.string.online))){
                holder.imagen_perfil.setBorderColor(context.getColor(R.color.spring_green));
                //holder.imagen_perfil.setMaxWidth(1);
            }else{
                holder.imagen_perfil.setBorderColor(context.getColor(R.color.red));
                //holder.imagen_perfil.setMaxWidth(1);
            }

        }else{
            holder.imagen_perfil.setBorderColor(context.getColor(R.color.colorPrimary));
            //holder.imagen_perfil.setMaxWidth(1);
        }
        holder.verPerfil.setText(R.string.verPerfil);

        holder.verPerfil.setOnClickListener(v -> {
            //Toast.makeText(context, "Has seleccionado ver el perfil", Toast.LENGTH_SHORT).show();

            final Dialog dialogo = new Dialog(context);
            dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogo.setCancelable(true);
            Objects.requireNonNull(dialogo.getWindow()).setBackgroundDrawable(context.getDrawable(R.drawable.activity_drawable_ver_perfil_acrylic));
            dialogo.setContentView(R.layout.ver_perfil_dialog);

            listaUsuariosBloqueados = Funciones.obtenerUsuariosBloqueados(firebaseUser);

            Button ver_perfil_dialog_llamar = dialogo.findViewById(R.id.ver_perfil_dialog_llamar);
            Button ver_perfil_dialog_enviar_mensaje = dialogo.findViewById(R.id.ver_perfil_dialog_enviar_mensaje);
            Button ver_perfil_dialog_bloquear = dialogo.findViewById(R.id.ver_perfil_dialog_bloquear);

            CircleImageView imagen_perfil = dialogo.findViewById(R.id.imagen_perfil);
            imagen_perfil.setBorderColor(context.getColor(R.color.colorPrimary));
            if(usuario.getImagenURL().equals("default")){
                //holder.imagen_perfil.setImageResource(R.mipmap.ic_launcher);
                //private static final int USUARIO_BLOQUEADO = 1;
                Glide.with(context).load(R.drawable.default_user_picture).into(imagen_perfil);
            }else{
                Glide.with(context).load(usuario.getImagenURL()).into(imagen_perfil);
            }

            TextView usuarioCampoPerfil = dialogo.findViewById(R.id.usuarioCampoPerfil);
            usuarioCampoPerfil.setText(usuario.getUsuario());
            TextView descripcionCampoPerfil = dialogo.findViewById(R.id.descripcionCampoPerfil);

            if(usuario.getDescripcion()==null){
                descripcionCampoPerfil.setText(R.string.sinDescripcion);
            }else{
                descripcionCampoPerfil.setText(usuario.getDescripcion());
            }

            TextView usuarioTelefonoperfil = dialogo.findViewById(R.id.usuarioTelefonoperfil);
            if(usuario.getTelefono() == null || usuario.getTelefono().length() == 0){
                usuarioTelefonoperfil.setText(R.string.telefonoDesconocido);
            }else{
                usuarioTelefonoperfil.setText(usuario.getTelefono());
            }
            dialogo.show();

           ver_perfil_dialog_llamar.setOnClickListener(v1 -> {

           });
           ver_perfil_dialog_enviar_mensaje.setOnClickListener(v12 -> {
               Intent intentChat = new Intent(context, MessageActivity.class);
               intentChat.putExtra("userID",usuario.getId());
               context.startActivity(intentChat);
           });

           ver_perfil_dialog_bloquear.setOnClickListener(v13 -> {

               if(!listaUsuariosBloqueados.containsKey(usuario.getId())){
                   Funciones.bloquearUsuario(usuario);
                   Log.d("DEBUG Dialogo Ver Perfil","USUARIO BLOQUEADO");
                   ver_perfil_dialog_bloquear.setText(R.string.desbloquear);
                   //ver_perfil_dialog_bloquear.setBackgroundColor(context.getColor(R.color.spring_green));
               }else{
                   Funciones.desbloquearUsuario(usuario);
                   Log.d("DEBUG Dialogo Ver Perfil","USUARIO DESBLOQUEADO");
                   ver_perfil_dialog_bloquear.setText(R.string.bloquear);
                   //ver_perfil_dialog_bloquear.setBackgroundColor(context.getColor(R.color.red));
               }
           });
        });



        //Entra a la actividad
        holder.itemView.setOnClickListener(v -> {
            //Funciones.setActividadEnUso(true);
            Intent intentChat = new Intent(context, MessageActivity.class);
            intentChat.putExtra("userID",usuario.getId());
            context.startActivity(intentChat);
        });
    }

    @Override
    public int getItemCount() {
        if (listaUsuarios != null){
            return listaUsuarios.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        public CircleImageView imagen_perfil;
        Button verPerfil;


        ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            verPerfil = itemView.findViewById(R.id.estado);
        }
    }

    public int getItemViewType(int posicion) {

        /*
        if(Funciones.obtenerUsuariosBloqueados(firebaseUser).containsKey(listaUsuarios.get(posicion).getId())){
            System.out.println("error encontrado");
            return  USUARIO_BLOQUEADO;
        }else{
            System.out.println("error no encontrado");
            return USUARIO_NO_BLOQUEADO;
        }

         */


        return USUARIO_NO_BLOQUEADO;
    }

}