package com.btds.app.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.MapsActivity;
import com.btds.app.Activitys.ViewImageActivity;
import com.btds.app.Modelos.LatLng;
import com.btds.app.Modelos.Mensaje;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * @author Alejandro Molina Louchnikov
 */


public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.ViewHolder> {

    final private String mapsKey = "AIzaSyBORKSQ18QNgE513owQRilIwZOaTY89oko";
    private static final int MENSAGE_TIPO_IZQUIERDA = 0;
    private static final int MENSAGE_TIPO_DERECHA = 1;
    private static final int MENSAGE_TIPO_IZQUIERDA_FOTO = 2;
    private static final int MENSAGE_TIPO_DERECHA_FOTO = 3;
    private static final int MENSAGE_TIPO_IZQUIERDA_LOCALIZACION = 4;
    private static final int MENSAGE_TIPO_DERECHA_LOCALIZACION = 5;

    private Context context;
    private List<Mensaje> listaMensajes;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference referenceChats = FirebaseDatabase.getInstance().getReference("Chats");


    public MensajesAdapter(Context contexto, List<Mensaje> listaMensajes){
        this.listaMensajes = listaMensajes;
        this.context = contexto;
    }

    @NonNull
    @Override
    public MensajesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    
        View view = null;     
        
        if(viewType == MENSAGE_TIPO_DERECHA){
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_derecha,parent,false);
        }else if(viewType == MENSAGE_TIPO_IZQUIERDA){
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_izquierda,parent,false);
            return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
        }else if(viewType == MENSAGE_TIPO_DERECHA_FOTO){
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_derecha_foto,parent,false);
            return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
        }else if(viewType == MENSAGE_TIPO_IZQUIERDA_FOTO){ 
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_izquierda_foto,parent,false);
            return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
        }else if(viewType == MENSAGE_TIPO_DERECHA_LOCALIZACION){
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_derecha_localizacion,parent,false);
            return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
        }else if(viewType == MENSAGE_TIPO_IZQUIERDA_LOCALIZACION){
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_izquierda_localizacion,parent,false);
            return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
        }
        return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull com.btds.app.Adaptadores.MensajesAdapter.ViewHolder holder, int posicion) {



        final Mensaje mensaje = listaMensajes.get(posicion);
        //mensaje.setLeido("true");

        if(mensaje.getLeido()){
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

        /*
        if(!firstEnter){
            //holder.hora.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
            //holder.show_message.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
            firstEnter = true;
        }

         */

        //holder.visto.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        //holder.estado.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));

        // != null me ha solucionado el problema de Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.ImageView.setVisibility(int)' on a null object reference
        //Decia que ImagenView no existia, al haber un chat donde la conversacion son de ambas partes y el layout esta dividio en 2, en una existe el ImagenView y en la otra no.


        if(mensaje.getTipoMensaje() == Mensaje.Tipo.TEXTO){
            Log.d("Debug MensajeAdapter","Mensaje Tipo TEXTO");
            holder.show_message.setText(mensaje.getMensaje());
            String hora = mensaje.fecha.hora+":"+mensaje.fecha.minutos;
            holder.hora.setText(hora);
        }else if(mensaje.getTipoMensaje() == Mensaje.Tipo.FOTO){

            if(Funciones.conectividadDisponible(context)){
                String hora = mensaje.fecha.hora+":"+mensaje.fecha.minutos;
                holder.hora.setText(hora);
                Log.d("Debug MensajeAdapter","Mensaje Tipo FOTO URL "+mensaje.getMensaje());
                //holder.mensaje_foto.
                Picasso.with(context).load(mensaje.getMensaje()).fit().centerCrop().into(holder.mensaje_foto);
                holder.mensaje_foto.setOnClickListener(v -> {

                    Intent intentViewImage = new Intent(context, ViewImageActivity.class);
                    intentViewImage.putExtra("FotoURL",mensaje.getMensaje());
                    context.startActivity(intentViewImage);

                });
            }else{
                holder.error_imagen.setVisibility(View.VISIBLE);
                holder.mensaje_foto.setVisibility(View.GONE);
                holder.hora.setVisibility(View.GONE);
                holder.visto.setVisibility(View.GONE);
            }

        }else if(mensaje.getTipoMensaje() == Mensaje.Tipo.LOCALIZACION){

            if(Funciones.conectividadDisponible(context)){
                String hora = mensaje.fecha.hora+":"+mensaje.fecha.minutos;
                holder.hora.setText(hora);
                String URLMAP = "https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/pin-s-marker+80ff80("+mensaje.getUbicacion().getLongitude()+","+mensaje.getUbicacion().getLatitude()+")/"
                        +mensaje.getUbicacion().getLongitude()+","+mensaje.getUbicacion().getLatitude()+
                        ",16,0,60/300x300@2x?access_token=pk.eyJ1IjoibG9sbTMiLCJhIjoiY2szcnV0dXZsMDBjNDNlbHV2aW05b2dwMSJ9.zjKTLo61BZzfI6e4OSzgEg&logo=false";
                Log.d("Debug MensajesAdapter","MAPBOX URL ubicacion "+URLMAP);
                Picasso.with(context).load(URLMAP).fit().centerCrop().into(holder.ubicacion);

                holder.ubicacion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentMaps = new Intent(context, MapsActivity.class);
                        LatLng ubicacion = mensaje.getUbicacion();
                        intentMaps.putExtra("Ubicacion", (Parcelable) ubicacion);
                        context.startActivity(intentMaps);
                    }
                });
            }else{
                holder.error_ubicacion.setVisibility(View.VISIBLE);
                holder.ubicacion.setVisibility(View.GONE);
                holder.hora.setVisibility(View.GONE);
                holder.visto.setVisibility(View.GONE);
            }

            if(holder.visto != null){
                if(!mensaje.getLeido()){
                    holder.visto.setVisibility(View.GONE);
                }else{
                    //holder.visto.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
                    if(mensaje.getEmisor().contentEquals(firebaseUser.getUid()) && mensaje.getLeido()){
                        holder.visto.setVisibility(View.VISIBLE);
                    }

                }
            }

        }
    }

    @Override
    public int getItemCount() {
        if (listaMensajes != null){
            return listaMensajes.size();
        }else {
            return 0;
        }
    }



    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView show_message;
        TextView hora;
        TextView estado;
        TextView error_imagen;
        TextView error_ubicacion;
        ImageView visto;
        ImageView mensaje_foto;
        ImageView ubicacion;

        ViewHolder(View itemView){
            super(itemView);
            show_message = itemView.findViewById(R.id.enseñar_mensaje);
            mensaje_foto = itemView.findViewById(R.id.enseñar_mensaje_foto);
            error_imagen = itemView.findViewById(R.id.imagen_error_chat);
            ubicacion = itemView.findViewById(R.id.enseñar_mensaje_ubicacion);
            error_ubicacion = itemView.findViewById(R.id.ubicacion_error_chat);
            hora = itemView.findViewById(R.id.hora);
            estado = itemView.findViewById(R.id.estado);
            visto = itemView.findViewById(R.id.leido);
        }

    }



    @Override
    public int getItemViewType(int posicion) {

        int devolver = -1;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        if(listaMensajes.get(posicion).getEmisor().equals(firebaseUser.getUid())){

            if(listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.FOTO){
                devolver = MENSAGE_TIPO_DERECHA_FOTO;
            }else if (listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.TEXTO){
                devolver = MENSAGE_TIPO_DERECHA;
            }else if(listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.LOCALIZACION){
                devolver = MENSAGE_TIPO_DERECHA_LOCALIZACION;
            }
        }else{

            if(listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.FOTO){
                devolver = MENSAGE_TIPO_IZQUIERDA_FOTO;
            }else if (listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.TEXTO){
                devolver = MENSAGE_TIPO_IZQUIERDA;
            }else if(listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.LOCALIZACION){
                devolver = MENSAGE_TIPO_IZQUIERDA_LOCALIZACION;
            }
        }
        return devolver;
    }
}


