package com.btds.app.Adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Modelos.PeticionAmistadUsuario;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.btds.app.Utils.Constantes;
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

    int limite = 7;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context context;
    private List<Usuario> listaUsuariosEncontrados;
    private HashMap<String,PeticionAmistadUsuario> peticionesEnviadasAnteriormente;
    //private HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    //private FirebaseUser firebaseUser;


    public BuscarAmigosAdapter(Context contexto, List<Usuario> listaUsuariosEncontrados,HashMap<String,PeticionAmistadUsuario> listaDePeticiones){
        this.listaUsuariosEncontrados = listaUsuariosEncontrados;
        this.peticionesEnviadasAnteriormente = listaDePeticiones;
        //Log.d("DEBUG BuscarAmigosAdapter","Lista de peticiones enviadas "+listaDePeticiones.size());
        //listaUsuariosBloqueados = new HashMap<>();
        this.context = contexto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_add_amigos,parent,false);
        return new BuscarAmigosAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        holder.imagen_perfil.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.usuario.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        holder.buttonPeticion.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));
        //holder.estado.setAnimation(AnimationUtils.loadAnimation(context,R.anim.recyclerview_users_anim));


        final Usuario usuario = listaUsuariosEncontrados.get(posicion);
        holder.usuario.setText(usuario.getUsuario());

        //System.out.println("usuario bindeado");

        if(usuario.getImagenURL().equals("default")){
            //holder.imagen_perfil.setImageResource(R.mipmap.ic_launcher);
            //private static final int USUARIO_BLOQUEADO = 1;
            String default_profile = Constantes.default_image_profile;
            Glide.with(context).load(default_profile).into(holder.imagen_perfil);
        }else{
            Glide.with(context).load(usuario.getImagenURL()).into(holder.imagen_perfil);
        }


        if(peticionesEnviadasAnteriormente.containsKey(firebaseUser.getUid()+""+usuario.getId())){
            holder.buttonPeticion.setText(context.getResources().getString(R.string.cancelarPeticion));
            holder.buttonPeticion.setBackgroundColor(context.getColor(R.color.colorPrimary));
        }else{
            holder.buttonPeticion.setText(context.getResources().getString(R.string.enviarPeticion));
            holder.buttonPeticion.setBackgroundColor(context.getColor(R.color.spring_green));
        }



        /*
        //listaUsuariosBloqueados = Funciones.obtenerUsuariosBloqueados(firebaseUser);
        if(!listaUsuariosBloqueados.containsKey(usuario.getId())){
            holder.estado.setText(usuario.getEstado());
        }else{
            holder.estado.setText(context.getResources().getString(R.string.bloqueado));
        }

         */


        //EnviarPeticion
        holder.itemView.setOnClickListener(v -> {
            //Funciones.setActividadEnUso(true);
            //Intent intentChat = new Intent(context, MessageActivity.class);
            //intentChat.putExtra("userID",usuario.getId());
            //context.startActivity(intentChat);

        });

        holder.buttonPeticion.setOnClickListener(v -> {
            Log.d("DEBUG BuscarAmigosAdapter","Peticion enviada a "+holder.usuario.getText());
            //Toast.makeText(context, "Has seleccionado la posicion"+holder.itemView.get, Toast.LENGTH_SHORT).show();

            if(holder.buttonPeticion.getText().toString().contentEquals(context.getResources().getString(R.string.enviarPeticion))){
                PeticionAmistadUsuario peticion = new PeticionAmistadUsuario(firebaseUser.getUid()+""+usuario.getId(),firebaseUser.getUid(),usuario.getId());
                Funciones.getPeticionesAmistadReference().child(firebaseUser.getUid()+""+usuario.getId()).setValue(peticion);
                holder.buttonPeticion.setText(context.getResources().getString(R.string.cancelarPeticion));
                holder.buttonPeticion.setBackgroundColor(context.getColor(R.color.colorPrimary));
            }else{
                //PeticionAmistadUsuario peticion = new PeticionAmistadUsuario(firebaseUser.getUid()+""+usuario.getId(),firebaseUser.getUid(),usuario.getId());
                Funciones.getPeticionesAmistadReference().child(firebaseUser.getUid()+""+usuario.getId()).removeValue();
                holder.buttonPeticion.setText(context.getResources().getString(R.string.enviarPeticion));
                holder.buttonPeticion.setBackgroundColor(context.getColor(R.color.spring_green));
            }
        });

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
        public Button buttonPeticion;


        ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            //estado = itemView.findViewById(R.id.estado);
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