package com.btds.app.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.EstadoActivity;
import com.btds.app.Modelos.Estados;
import com.btds.app.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * @author Alejandro Molina Louchnikov
 */

public class EstadosAdapter extends RecyclerView.Adapter<EstadosAdapter.ViewHolder>  {


    //public static int ESTADO_NO_VISTO = 0;
    private List<Estados> listaEstados;
    private Context context;
    //private HashMap<String,Usuario> usuariosEstados;
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public EstadosAdapter(Context contexto, List<Estados> listaEstados){
        this.context = contexto;
        this.listaEstados = listaEstados;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int ESTADO_VISTO = 1;

        if(viewType == ESTADO_VISTO){
            View view = LayoutInflater.from(context).inflate(R.layout.estados2_item,parent,false);

            return new ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.estados_item,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        holder.imagen_estado.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.usuario.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));

        final Estados estado = listaEstados.get(posicion);
        //holder.usuario.setText(estado.getUsuarioEstado().getUsuario());

        //usuariosEstados.put(usuarioObject.getId(),usuarioObject);

        holder.usuario.setText(estado.getUsuario().getUsuario());

        if(posicion == 0){
            holder.usuario.setText(context.getResources().getString(R.string.tusHistorias));
            holder.imagen_estado.setImageResource(R.drawable.add_historia_personal);
            holder.imagen_estado.setBorderWidth(0);
            holder.imagen_estado.setBorderOverlay(false);
            holder.imagen_estado.setCircleBackgroundColorResource(R.color.white);
        }else{

            if(estado.getUsuario().getVisibilidad().getUsuario()){
                holder.usuario.setText(estado.getUsuario().getUsuario());
            }else{
                holder.usuario.setText(R.string.desconocido);
            }

            Glide.with(context).load(estado.getEstadoURL()).into(holder.imagen_estado);
        }

        //Entra a la actividad
        holder.itemView.setOnClickListener(v -> {
            final int position = holder.getAdapterPosition();

            //ENTRAR A TU PROPIA HISTORA Y SUBIR CONTENIDO O BORRAR
            if(position == 0){
                Log.d("DEBUG EstadosAdapter","Has entrado en tus estados");
                Intent tusEstados = new Intent(context, EstadoActivity.class);
                assert firebaseUser != null;
                tusEstados.putExtra("estadoUsuarioIDFirebase",firebaseUser.getUid());
                context.startActivity(tusEstados);
            }else{
                Intent intentEstado = new Intent(context, EstadoActivity.class);
                intentEstado.putExtra("estadoUsuarioIDFirebase",estado.getUsuario().getId());
                context.startActivity(intentEstado);
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

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        CircleImageView imagen_estado;

        ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_estado = itemView.findViewById(R.id.imagen_estado);
        }
    }

    public int getItemViewType(int posicion) {
        return 0;
    }

}
