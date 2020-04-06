package com.btds.app.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.MessageActivity;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder>  {

    public static final int USUARIO_NO_BLOQUEADO = 0;
    public static final int USUARIO_BLOQUEADO = 1;

    private boolean firstSearch = true;
    private Context context;
    private List<Usuario> listaUsuarios;
    private HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    private FirebaseUser firebaseUser;


    public UsuariosAdapter(Context contexto, List<Usuario> listaUsuarios){
        this.listaUsuarios = listaUsuarios;
        listaUsuariosBloqueados = new HashMap<>();
        this.context = contexto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == USUARIO_NO_BLOQUEADO){
            View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item,parent,false);
            return new UsuariosAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_bloqueado,parent,false);
            return new UsuariosAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        final Usuario usuario = listaUsuarios.get(posicion);
        holder.usuario.setText(usuario.getUsuario());

        //System.out.println("usuario bindeado");

        if(usuario.getImagenURL().equals("default")){
            holder.imagen_perfil.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(context).load(usuario.getImagenURL()).into(holder.imagen_perfil);
        }

        if(!listaUsuariosBloqueados.containsKey(usuario.getId())){
            holder.estado.setText(usuario.getEstado());
        }


        //Entra a la actividad
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Funciones.setActividadEnUso(true);
                Intent intentChat = new Intent(context, MessageActivity.class);
                intentChat.putExtra("userID",usuario.getId());
                context.startActivity(intentChat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        public TextView estado;
        public ImageView imagen_perfil;


        public ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            estado = itemView.findViewById(R.id.estado);
        }
    }

    public int getItemViewType(int posicion) {

        return 0;
    }

}
