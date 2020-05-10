package com.btds.app.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.MessageActivity;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.btds.app.R;
import com.btds.app.Utils.Constantes;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;

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

        if(viewType == USUARIO_NO_BLOQUEADO){
            View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_tests,parent,false);
            return new com.btds.app.Adaptadores.UsuariosAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_blocked,parent,false);
            return new com.btds.app.Adaptadores.UsuariosAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        holder.imagen_perfil.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.usuario.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.estado.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));

        final Usuario usuario = listaUsuarios.get(posicion);
        holder.usuario.setText(usuario.getUsuario());

        //System.out.println("usuario bindeado");

        if(usuario.getImagenURL().equals("default")){
            //holder.imagen_perfil.setImageResource(R.mipmap.ic_launcher);
            //private static final int USUARIO_BLOQUEADO = 1;
            String default_profile = Constantes.default_image_profile;
            Glide.with(context).load(default_profile).into(holder.imagen_perfil);
        }else{
            Glide.with(context).load(usuario.getImagenURL()).into(holder.imagen_perfil);
        }

        if(firebaseUser != null && !listaUsuariosBloqueados.containsKey(firebaseUser.getUid()+usuario.getId())){

            if(usuario.getEstado().contentEquals(context.getResources().getString(R.string.online))){
                holder.estado.setText(R.string.online);
                holder.estado.setBackgroundColor(context.getColor(R.color.spring_green));
            }else{
                holder.estado.setText(R.string.offline);
                holder.estado.setBackgroundColor(context.getColor(R.color.red));
            }

        }else{
            holder.estado.setText(R.string.bloqueado);
            holder.estado.setBackgroundColor(context.getColor(R.color.colorPrimary));
        }


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
        public ImageView imagen_perfil;
        public Button estado;


        ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            estado = itemView.findViewById(R.id.estado);
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