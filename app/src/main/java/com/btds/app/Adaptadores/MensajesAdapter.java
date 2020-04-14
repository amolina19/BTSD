package com.btds.app.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Modelos.Mensaje;
import com.btds.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.ViewHolder>  {

    public static final int MENSAGE_TIPO_IZQUIERDA = 0;
    public static final int MENSAGE_TIPO_DERECHA = 1;

    private Boolean firstEnter = false;
    private Context context;
    private List<Mensaje> listaMensajes;

    FirebaseUser firebaseUser;
    DatabaseReference referenceChats = FirebaseDatabase.getInstance().getReference("Chats");


    public MensajesAdapter(Context contexto, List<Mensaje> listaMensajes){
        this.listaMensajes = listaMensajes;
        this.context = contexto;
    }

    @NonNull
    @Override
    public MensajesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MENSAGE_TIPO_DERECHA){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_derecha,parent,false);
            return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_izquierda,parent,false);
            return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull com.btds.app.Adaptadores.MensajesAdapter.ViewHolder holder, int posicion) {



        final Mensaje mensaje = listaMensajes.get(posicion);
        //mensaje.setLeido("true");

        if(mensaje.getLeido().contentEquals("true")){
            referenceChats.child(mensaje.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    referenceChats.child(mensaje.getId()).setValue(mensaje);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(!firstEnter){
            holder.hora.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
            holder.show_message.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
            firstEnter = true;
        }

        //holder.visto.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        //holder.estado.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));


        holder.show_message.setText(mensaje.getMensaje());
        holder.hora.setText(mensaje.getHora());

        // != null me ha solucionado el problema de Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.ImageView.setVisibility(int)' on a null object reference
        //Decia que ImagenView no existia, al haber un chat donde la conversacion son de ambas partes y el layout esta dividio en 2, en una existe el ImagenView y en la otra no.
        if(holder.visto != null){
            if(mensaje.getLeido().contentEquals("false")){
                holder.visto.setVisibility(View.GONE);
            }else{
                //holder.visto.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
                holder.visto.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public TextView hora;
        public TextView estado;
        ImageView visto;

        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.enseñar_mensaje);
            hora = itemView.findViewById(R.id.hora);
            estado = itemView.findViewById(R.id.estado);
            visto = itemView.findViewById(R.id.leido);

        }

    }

    @Override
    public int getItemViewType(int posicion) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(listaMensajes.get(posicion).getEmisor().equals(firebaseUser.getUid())){
            return MENSAGE_TIPO_DERECHA;
        }else{
            return MENSAGE_TIPO_IZQUIERDA;
        }
    }
}


