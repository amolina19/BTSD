package com.btds.app.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Modelos.Llamada;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Alejandro Molina Louchnikov
 */

public class LlamadasAdapter extends RecyclerView.Adapter<LlamadasAdapter.ViewHolder>  {

    //private boolean firstSearch = true;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context context;
    private List<Llamada> listaLlamadas;
    private String tiempoTranscurrido;
    //private FirebaseUser firebaseUser;


    public LlamadasAdapter(Context contexto, List<Llamada> listaLlamadas){
        this.listaLlamadas = listaLlamadas;
        this.context = contexto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //if(viewType == USUARIO_NO_BLOQUEADO){
            View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_llamadas,parent,false);
            return new LlamadasAdapter.ViewHolder(view);
        //}

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        //holder.imagen_perfil.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        //holder.usuario.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        //holder.tiempoTranscurrido.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        //holder.typeCall.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        final Llamada llamada = listaLlamadas.get(posicion);

        holder.usuario.setText(llamada.usuarioOrigen.getUsuario());

        if(llamada.usuarioOrigen.getImagenURL().equals("default")){
            //holder.imagen_perfil.setImageResource(R.mipmap.ic_launcher);
            //private static final int USUARIO_BLOQUEADO = 1;
            Glide.with(context).load(R.drawable.default_user_picture).into(holder.imagen_perfil);
        }else{
            Glide.with(context).load(llamada.usuarioOrigen.getImagenURL()).into(holder.imagen_perfil);
        }


        if(llamada.isVideollamada()){
            Glide.with(context).load(R.drawable.ic_video_call_item_fragment).into((holder.typeCall));
        }else{
            Glide.with(context).load(R.drawable.ic_call_end_item_fragment).into((holder.typeCall));
        }

        if(llamada.getTiempoTranscurrido() !=null){

            switch (llamada.getTiempoTranscurrido().size()){
                case 1:
                    tiempoTranscurrido = llamada.getTiempoTranscurrido().get(0)+"s";
                    break;
                case 2:
                    tiempoTranscurrido = llamada.getTiempoTranscurrido().get(1)+"m "+llamada.getTiempoTranscurrido().get(0)+"s";
                    break;
                case 3:
                    tiempoTranscurrido = llamada.getTiempoTranscurrido().get(2)+"h "+llamada.getTiempoTranscurrido().get(1)+"m "+llamada.getTiempoTranscurrido().get(0)+"s";
                    break;
            }

            holder.tiempoTranscurrido.setText(context.getResources().getString(R.string.transcurrido)+" "+tiempoTranscurrido);
        }


        int minutosTranscurridos = Funciones.tiempoTranscurrido(llamada.getFecha());

        if(minutosTranscurridos < 1440){
            String hora = llamada.fecha.hora+":"+llamada.fecha.minutos;
            holder.horaTexto.setText(hora);
        }else if(minutosTranscurridos < 2880 ){
            holder.horaTexto.setText(context.getResources().getString(R.string.ayer));
        }else{
            String fecha = llamada.getFecha().dia+"/"+llamada.getFecha().mes+"/"+llamada.getFecha().anno;
            holder.horaTexto.setText(fecha);
        }

    }

    @Override
    public int getItemCount() {
        if (listaLlamadas != null){
            return listaLlamadas.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        public CircleImageView imagen_perfil;
        Button verPerfil;
        public ImageView typeCall;
        public TextView tiempoTranscurrido;
        public TextView horaTexto;


        ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            typeCall = itemView.findViewById(R.id.typeCall);
            tiempoTranscurrido = itemView.findViewById(R.id.tiempoTranscurrido);
            horaTexto = itemView.findViewById(R.id.horaTexto);
        }
    }

    public int getItemViewType(int posicion) {
        return 0;
    }

}