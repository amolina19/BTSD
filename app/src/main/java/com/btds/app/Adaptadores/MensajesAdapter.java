package com.btds.app.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Modelos.Mensaje;
import com.btds.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.List;

public class MensajesAdapter extends RecyclerView.Adapter<MensajesAdapter.ViewHolder>  {

    public static final int MENSAGE_TIPO_IZQUIERDA = 0;
    public static final int MENSAGE_TIPO_DERECHA = 1;

    private Context context;
    private List<Mensaje> listaMensajes;

    FirebaseUser firebaseUser;


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

        Mensaje mensaje = listaMensajes.get(posicion);
        holder.show_message.setText(mensaje.getMensaje());
        System.out.println(mensaje.getHora());
        holder.hora.setText(mensaje.getHora());
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public TextView hora;
        public TextView estado;
        //public ImageView imagen_perfil;


        public ViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.enseñar_mensaje);
            hora = itemView.findViewById(R.id.hora);
            //imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            estado = itemView.findViewById(R.id.estado);
            //Registrar menu contextual
            //itemView.setOnCreateContextMenuListener(this);
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


