package com.btds.app.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.MessageActivity;
import com.btds.app.Modelos.PeticionAmistadUsuario;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.List;

/**
 * @author Alejandro Molina Louchnikov
 */

public class BuscarAmigosAdapter extends RecyclerView.Adapter<BuscarAmigosAdapter.ViewHolder>  {

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context context;
    private List<Usuario> listaUsuariosEncontrados;
    private HashMap<String,PeticionAmistadUsuario> peticionesEnviadasAnteriormente;


    public BuscarAmigosAdapter(Context contexto, List<Usuario> listaUsuariosEncontrados,HashMap<String,PeticionAmistadUsuario> listaDePeticiones){
        this.listaUsuariosEncontrados = listaUsuariosEncontrados;
        this.peticionesEnviadasAnteriormente = listaDePeticiones;
        this.context = contexto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_search,parent,false);
        return new BuscarAmigosAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        final Usuario usuario = listaUsuariosEncontrados.get(posicion);
        holder.usuario.setText(usuario.getUsuario());

        if(usuario.getImagenURL().equals("default")){
            Glide.with(context).load(R.drawable.default_user_picture).into(holder.imagen_perfil);
        }else{
            Glide.with(context).load(usuario.getImagenURL()).into(holder.imagen_perfil);
        }

        if(firebaseUser != null){

            if(peticionesEnviadasAnteriormente.containsKey(firebaseUser.getUid()+""+usuario.getId())){
                holder.buttonPeticion.setText(context.getResources().getString(R.string.cancelarPeticion));
                holder.buttonPeticion.setBackgroundColor(context.getColor(R.color.colorPrimary));
            }else{
                holder.buttonPeticion.setText(context.getResources().getString(R.string.enviarPeticion));
                holder.buttonPeticion.setBackgroundColor(context.getColor(R.color.spring_green));
            }


            //EnviarPeticion
            holder.itemView.setOnClickListener(v -> {
                Intent intentChat = new Intent(context, MessageActivity.class);
                intentChat.putExtra("userID",usuario.getId());
                context.startActivity(intentChat);

            });

            holder.buttonPeticion.setOnClickListener(v -> {
                Log.d("DEBUG BuscarAmigosAdapter","Peticion enviada a "+holder.usuario.getText());
                if(holder.buttonPeticion.getText().toString().contentEquals(context.getResources().getString(R.string.enviarPeticion))){
                    PeticionAmistadUsuario peticion = new PeticionAmistadUsuario(firebaseUser.getUid()+""+usuario.getId(),firebaseUser.getUid(),usuario.getId());
                    Funciones.getPeticionesAmistadReference().child(firebaseUser.getUid()+""+usuario.getId()).setValue(peticion);
                    holder.buttonPeticion.setText(context.getResources().getString(R.string.cancelarPeticion));
                    holder.buttonPeticion.setBackgroundColor(context.getColor(R.color.colorPrimary));
                }else{
                    Funciones.getPeticionesAmistadReference().child(firebaseUser.getUid()+""+usuario.getId()).removeValue();
                    holder.buttonPeticion.setText(context.getResources().getString(R.string.enviarPeticion));
                    holder.buttonPeticion.setBackgroundColor(context.getColor(R.color.spring_green));
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        if (listaUsuariosEncontrados != null){
            return  listaUsuariosEncontrados.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        public TextView estado;
        public ImageView imagen_perfil;
        Button buttonPeticion;


        ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            buttonPeticion = itemView.findViewById(R.id.buttonAdd);
        }
    }

    public int getItemViewType(int posicion) {

        if (listaUsuariosEncontrados != null){
            return listaUsuariosEncontrados.size();
        }else {
            return 0;
        }
    }

}