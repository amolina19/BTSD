package com.btds.app.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.EstadoActivity;
import com.btds.app.Activitys.TusEstadosActivity;
import com.btds.app.Modelos.EstadosClass;
import com.btds.app.R;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class EstadosAdapter extends RecyclerView.Adapter<EstadosAdapter.ViewHolder>  {


    public static int ESTADO_VISTO = 1;
    public static int ESTADO_NO_VISTO = 0;
    private List<EstadosClass> listaEstados;

    private Context context;

    public EstadosAdapter(Context contexto, List<EstadosClass> listaEstados){
        this.context = contexto;
        this.listaEstados = listaEstados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        if(viewType == ESTADO_VISTO){
            View view = LayoutInflater.from(context).inflate(R.layout.estados2_item,parent,false);

            return new EstadosAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.estados_item,parent,false);
            return new EstadosAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        holder.imagen_estado.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.usuario.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));

        final EstadosClass estado = listaEstados.get(posicion);
        //holder.usuario.setText(estado.getUsuarioEstado().getUsuario());
        holder.usuario.setText(estado.getUsuario());

        if(estado.getestadoURL().equals("default")){
            holder.imagen_estado.setImageResource(R.mipmap.ic_launcher);
        }else{
            //Glide.with(context).load(listaEstados.get(0).getestadoURL()).into(holder.imagen_estado);
            Glide.with(context).load(estado.getestadoURL()).into(holder.imagen_estado);
        }

        /*
        if(!listaUsuariosBloqueados.containsKey(usuario.getId())){
            holder.estado.setText(usuario.getEstado());
        }

         */

        //System.out.println("usuario bindeado");
        /*
        if(usuario.getImagenURL().equals("default")){
            holder.imagen_perfil.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(context).load(usuario.getImagenURL()).into(holder.imagen_perfil);
        }

        if(!listaUsuariosBloqueados.containsKey(usuario.getId())){
            holder.estado.setText(usuario.getEstado());
        }

        */

        //Entra a la actividad
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Funciones.setActividadEnUso(true);
                */

                final int position = holder.getAdapterPosition();

                //ENTRAR A TU PROPIA HISTORA Y SUBIR CONTENIDO O BORRAR
                if(position == 0){
                    Toast.makeText(context, "FUNCIONA", Toast.LENGTH_SHORT).show();
                    Intent tusEstados = new Intent(context, TusEstadosActivity.class);
                    context.startActivity(tusEstados);
                }else{
                    Intent intentEstado = new Intent(context, EstadoActivity.class);
                    intentEstado.putExtra("userID",estado.getUsuario());
                    context.startActivity(intentEstado);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listaEstados != null){
            return listaEstados.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        CircleImageView imagen_estado;
        //public TextView estado;
        //public ImageView imagen_perfil;


        public StoriesProgressView storiesProgressView;


        public ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_estado = itemView.findViewById(R.id.imagen_estado);
            //estado = itemView.findViewById(R.id.estado);
        }
    }

    public int getItemViewType(int posicion) {
        return 0;
    }

}
