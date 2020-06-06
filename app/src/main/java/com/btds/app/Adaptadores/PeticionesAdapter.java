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

public class PeticionesAdapter extends RecyclerView.Adapter<PeticionesAdapter.ViewHolder>  {

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context context;
    private List<Usuario> listaPeticiones;
    private HashMap<String,String> listaAmigos;
    //private HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    //private FirebaseUser firebaseUser;


    public PeticionesAdapter(Context contexto, List<Usuario> listaDePeticiones,HashMap<String,String> listaAmigos ){
        this.listaPeticiones = listaDePeticiones;
        this.listaAmigos = listaAmigos;
        //Log.d("DEBUG BuscarAmigosAdapter","Lista de peticiones enviadas "+listaDePeticiones.size());
        //listaUsuariosBloqueados = new HashMap<>();
        this.context = contexto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_petitions,parent,false);
        return new PeticionesAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        final Usuario usuario = listaPeticiones.get(posicion);
        holder.usuario.setText(usuario.getUsuario());
        if(usuario.getImagenURL().equals("default")){
            //holder.imagen_perfil.setImageResource(R.mipmap.ic_launcher);
            //private static final int USUARIO_BLOQUEADO = 1;
            Glide.with(context).load(R.drawable.default_user_picture).into(holder.imagen_perfil);
        }else{
            Glide.with(context).load(usuario.getImagenURL()).into(holder.imagen_perfil);
        }


        holder.buttonPeticionAccept.setOnClickListener(v -> Funciones.aceptarAmigo(firebaseUser,usuario));

        holder.buttonPeticionDeny.setOnClickListener(v -> Funciones.eliminarPeticion(firebaseUser,usuario));

    }

    @Override
    public int getItemCount() {



        if (listaPeticiones != null){
            return  listaPeticiones.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        public TextView estado;
        public ImageView imagen_perfil;
        Button buttonPeticionAccept;
        Button buttonPeticionDeny;


        ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            //estado = itemView.findViewById(R.id.estado);
            buttonPeticionAccept = itemView.findViewById(R.id.buttonAdd);
            buttonPeticionDeny = itemView.findViewById(R.id.buttonDeny);
        }
    }

    public int getItemViewType(int posicion) {

        if (listaPeticiones != null){
            return listaPeticiones.size();
        }else {
            return 0;
        }
    }

}