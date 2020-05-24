package com.btds.app.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.MessageActivity;
import com.btds.app.Modelos.ListaMensajesChat;
import com.btds.app.Modelos.Mensaje;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * @author Alejandro Molina Louchnikov
 */

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder>  {

    //private static final int USUARIO_NO_BLOQUEADO = 0;
    //private static final int USUARIO_BLOQUEADO = 1;

    private Context context;
    //private HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    private List<ListaMensajesChat> lista;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    public ChatsAdapter(Context contexto, List<ListaMensajesChat> lista){
        this.lista = lista;
        this.context = contexto;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //if(viewType == USUARIO_NO_BLOQUEADO){
        View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_chats_fragment,parent,false);
        return new ChatsAdapter.ViewHolder(view);
        //}

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        holder.imagen_perfil.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.usuario.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.textoChat.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.hora.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.leido.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));

        final ListaMensajesChat listaMensaje = lista.get(posicion);

        holder.usuario.setText(listaMensaje.getUsuarios().getUsuario());
        if(listaMensaje.getUsuarios().getImagenURL().contentEquals("default")){
            Glide.with(context).load(R.drawable.default_user_picture).into(holder.imagen_perfil);
        }else{
            Glide.with(context).load(listaMensaje.getUsuarios().getImagenURL()).into(holder.imagen_perfil);
        }

        if(listaMensaje.getMensaje().getTipoMensaje() == Mensaje.Tipo.TEXTO){
            holder.textoChat.setText(listaMensaje.getMensaje().getMensaje());
        }else if(listaMensaje.getMensaje().getTipoMensaje() == Mensaje.Tipo.FOTO){
            holder.textoChat.setText(R.string.imagen);
        }else if(listaMensaje.getMensaje().getTipoMensaje() == Mensaje.Tipo.LOCALIZACION){
            holder.textoChat.setText(R.string.ubicacion);
        }


        int minutosTranscurridos = Funciones.tiempoTranscurrido(listaMensaje.getMensaje().getFecha());

        if(minutosTranscurridos < 1440){
            String hora = listaMensaje.getMensaje().fecha.hora+":"+listaMensaje.getMensaje().fecha.minutos;
            holder.hora.setText(hora);
        }else if(minutosTranscurridos < 2880 ){
            holder.hora.setText(context.getResources().getString(R.string.ayer));
        }else{
            String fecha = listaMensaje.getMensaje().getFecha().dia+"/"+listaMensaje.getMensaje().getFecha().mes+"/"+listaMensaje.getMensaje().getFecha().anno;
            holder.hora.setText(fecha);
        }



        if(listaMensaje.getMensaje().getLeido() && listaMensaje.getMensaje().getEmisor().contentEquals(firebaseUser.getUid())){
            holder.leido.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intentChat = new Intent(context, MessageActivity.class);
            intentChat.putExtra("userID",listaMensaje.getUsuarios().getId());
            context.startActivity(intentChat);
        });
    }

    @Override
    public int getItemCount() {
        if (lista != null){
            return lista.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        public ImageView imagen_perfil;
        TextView textoChat;
        public TextView hora;
        TextView leido;
        //Button estado;


        ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            textoChat = itemView.findViewById(R.id.textoChat);
            hora = itemView.findViewById(R.id.horaTexto);
            leido = itemView.findViewById(R.id.textoLeidoChat);
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
        return 0;
    }

}