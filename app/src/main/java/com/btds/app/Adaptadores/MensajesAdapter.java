package com.btds.app.Adaptadores;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.MapsActivity;
import com.btds.app.Activitys.ViewImageActivity;
import com.btds.app.Modelos.LatLng;
import com.btds.app.Modelos.Mensaje;
import com.btds.app.Modelos.Usuario;
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

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.view.View.GONE;

/**
 * @author Alejandro Molina Louchnikov
 */


public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.ViewHolder> {

    private static final int MENSAGE_TIPO_DERECHA = 1;
    private static final int MENSAGE_TIPO_DERECHA_FOTO = 3;
    private static final int MENSAGE_TIPO_DERECHA_LOCALIZACION = 5;
    private static final int MENSAGE_TIPO_DERECHA_AUDIO = 7;

    private static final int MENSAGE_TIPO_IZQUIERDA = 0;
    private static final int MENSAGE_TIPO_IZQUIERDA_FOTO = 2;
    private static final int MENSAGE_TIPO_IZQUIERDA_LOCALIZACION = 4;
    private static final int MENSAGE_TIPO_IZQUIERDA_AUDIO= 6;



    private Context context;
    private List<Mensaje> listaMensajes;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Usuario usuarioChat;
    private Usuario usuarioActual;

    private DatabaseReference referenceChats = FirebaseDatabase.getInstance().getReference("Chats");


    public MensajesAdapter(Context contexto, List<Mensaje> listaMensajes,Usuario usuarioChat,Usuario usuarioActual){
        this.listaMensajes = listaMensajes;
        if(usuarioChat != null){
            this.usuarioChat = usuarioChat;
        }
        if(usuarioActual != null){
            this.usuarioActual = usuarioActual;
        }
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
        }else if(viewType == MENSAGE_TIPO_DERECHA_AUDIO){
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_derecha_audio,parent,false);
            return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
        }else if(viewType == MENSAGE_TIPO_IZQUIERDA_AUDIO){
            view = LayoutInflater.from(context).inflate(R.layout.chat_item_izquierda_audio,parent,false);
            return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
        }
        return new com.btds.app.Adaptadores.MensajesAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull com.btds.app.Adaptadores.MensajesAdapter.ViewHolder holder, int posicion) {



        final Mensaje mensaje = listaMensajes.get(posicion);

        //Comprueba si el mensaje es leido
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

        // != null me ha solucionado el problema de Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.ImageView.setVisibility(int)' on a null object reference
        //Decia que ImagenView no existia, al haber un chat donde la conversacion son de ambas partes y el layout esta dividio en 2, en una existe el ImagenView y en la otra no.
        if(holder.visto != null){
            if(!mensaje.getLeido()){
                holder.visto.setVisibility(GONE);
            }else{
                if(mensaje.getEmisor().contentEquals(firebaseUser.getUid())){
                    if(getMensajePosicionDerecha(getItemViewType(posicion))){
                        if(mensaje.getLeido()){
                            holder.visto.setImageDrawable(null);
                            holder.visto.setImageResource(R.drawable.ic_leido);
                            //holder.visto.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }

        if(mensaje.getTipoMensaje() == Mensaje.Tipo.TEXTO){
            holder.show_message.setText(mensaje.getMensaje());
            String hora = mensaje.fecha.hora+":"+mensaje.fecha.minutos;
            holder.hora.setText(hora);
        }else if(mensaje.getTipoMensaje() == Mensaje.Tipo.FOTO){

            if(Funciones.conectividadDisponible(context)){
                String hora = mensaje.fecha.hora+":"+mensaje.fecha.minutos;
                holder.hora.setText(hora);
                Picasso.with(context).load(mensaje.getMensaje()).fit().centerCrop().into(holder.mensaje_foto);
                holder.mensaje_foto.setOnClickListener(v -> {

                    Intent intentViewImage = new Intent(context, ViewImageActivity.class);
                    if(mensaje.getEmisor().contentEquals(firebaseUser.getUid())){
                        intentViewImage.putExtra("Usuario", usuarioActual);
                    }else{
                        intentViewImage.putExtra("Usuario", usuarioChat);
                    }

                    intentViewImage.putExtra("Mensaje",mensaje);
                    intentViewImage.putExtra("Fecha",mensaje.getFecha());
                    context.startActivity(intentViewImage);

                });
            }else{
                holder.error_imagen.setVisibility(View.VISIBLE);
                holder.mensaje_foto.setVisibility(GONE);
                holder.hora.setVisibility(GONE);
                //Item derecha foto no existe definido el visto, error tiempo ejecución.
                if(getItemViewType(posicion) == MENSAGE_TIPO_DERECHA_FOTO){
                    holder.visto.setVisibility(GONE);
                }
            }

        }else if(mensaje.getTipoMensaje() == Mensaje.Tipo.LOCALIZACION){

            if(Funciones.conectividadDisponible(context)){
                String hora = mensaje.fecha.hora+":"+mensaje.fecha.minutos;
                holder.hora.setText(hora);
                String URLMAP = "https://api.mapbox.com/styles/v1/mapbox/streets-v11/static/pin-s-marker+80ff80("+mensaje.getUbicacion().getLongitude()+","+mensaje.getUbicacion().getLatitude()+")/"
                        +mensaje.getUbicacion().getLongitude()+","+mensaje.getUbicacion().getLatitude()+
                        ",16,0,60/300x300@2x?access_token=pk.eyJ1IjoibG9sbTMiLCJhIjoiY2szcnV0dXZsMDBjNDNlbHV2aW05b2dwMSJ9.zjKTLo61BZzfI6e4OSzgEg&logo=false";
                Picasso.with(context).load(URLMAP).fit().centerCrop().into(holder.ubicacion);

                holder.ubicacion.setOnClickListener(v -> {

                    Intent intentMaps = new Intent(context, MapsActivity.class);
                    LatLng ubicacion = mensaje.getUbicacion();
                    intentMaps.putExtra("Ubicacion", (Parcelable) ubicacion);
                    context.startActivity(intentMaps);

                });
            }else{
                holder.error_ubicacion.setVisibility(View.VISIBLE);
                holder.ubicacion.setVisibility(GONE);
                holder.hora.setVisibility(GONE);
                //Item derecha localización no existe definido el visto, error tiempo ejecución.
                if(getItemViewType(posicion) == MENSAGE_TIPO_DERECHA_LOCALIZACION){
                    holder.visto.setVisibility(GONE);
                }
            }


        }else if(mensaje.getTipoMensaje() == Mensaje.Tipo.AUDIO){

            Uri uriAudio = Uri.parse(mensaje.getAudio().getURLirebase());
            File fileAudio = new File(mensaje.getAudio().getPathLocal());

            if(!fileAudio.exists()){

                if(Funciones.conectividadDisponible(context)){

                    DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uriAudio);
                    Uri destinationUri = Uri.fromFile(new File(mensaje.getAudio().getPathLocal()));
                    request.setDestinationUri(destinationUri);
                    assert downloadmanager != null;
                    downloadmanager.enqueue(request);

                }else {
                    holder.error_imagen.setVisibility(View.VISIBLE);
                    holder.audioPause.setClickable(false);
                    holder.audioPause.setVisibility(GONE);
                    holder.audioStart.setClickable(false);
                    holder.audioStart.setVisibility(GONE);
                }

            }

            String hora = mensaje.fecha.hora+":"+mensaje.fecha.minutos;
            holder.hora.setText(hora);

            MediaPlayer mediaPlayer = new MediaPlayer();
            try{
                if(fileAudio.exists()){
                    mediaPlayer.setDataSource(fileAudio.getAbsolutePath());
                    mediaPlayer.prepare();
                }
                String tiempo = Funciones.calcularTiempo(mediaPlayer.getDuration());
                String duracion = context.getResources().getString(R.string.duracionAudio)+" "+tiempo;
                holder.duracionAudio.setText(duracion);

            }catch (IOException ioe){
                ioe.printStackTrace();
            }

            mediaPlayer.setOnCompletionListener(mp -> {
                holder.audioStart.setVisibility(View.VISIBLE);
                holder.audioStart.setClickable(true);
                holder.audioPause.setVisibility(View.INVISIBLE);
                holder.audioPause.setClickable(false);
            });

            holder.audioStart.setOnClickListener(v -> {
                holder.audioPause.setVisibility(View.VISIBLE);
                holder.audioPause.setClickable(true);
                holder.audioStart.setVisibility(View.INVISIBLE);
                holder.audioStart.setClickable(false);
                mediaPlayer.start();
            });

            holder.audioPause.setOnClickListener(v -> {
                holder.audioStart.setVisibility(View.VISIBLE);
                holder.audioStart.setClickable(true);
                holder.audioPause.setVisibility(View.INVISIBLE);
                holder.audioPause.setClickable(false);
                mediaPlayer.pause();
            });
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

        TextView show_message, hora, estado, error_imagen, error_ubicacion;
        ImageView visto, mensaje_foto, ubicacion;
        TextView duracionAudio;
        ImageButton audioStart, audioPause;

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
            duracionAudio = itemView.findViewById(R.id.duracionAudio);
            audioStart = itemView.findViewById(R.id.buttonAudioPlay);
            audioPause = itemView.findViewById(R.id.buttonAudioStop);
        }

    }

    public boolean getMensajePosicionDerecha(int tipoMensajeIntView){
        boolean valor;
        valor = (tipoMensajeIntView % 2) != 0;
        return valor;
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
            }else if(listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.AUDIO){
                devolver = MENSAGE_TIPO_DERECHA_AUDIO;
            }

        }else{

            if(listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.FOTO){
                devolver = MENSAGE_TIPO_IZQUIERDA_FOTO;
            }else if (listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.TEXTO){
                devolver = MENSAGE_TIPO_IZQUIERDA;
            }else if(listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.LOCALIZACION){
                devolver = MENSAGE_TIPO_IZQUIERDA_LOCALIZACION;
            }else if(listaMensajes.get(posicion).getTipoMensaje() == Mensaje.Tipo.AUDIO){
                devolver = MENSAGE_TIPO_IZQUIERDA_AUDIO;
            }
        }
        return devolver;
    }
}


