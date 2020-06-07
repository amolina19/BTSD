package com.btds.app.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Modelos.Usuario;
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

public class BloqueadosAdapter extends RecyclerView.Adapter<BloqueadosAdapter.ViewHolder>  {

    private static final int USUARIO_NO_BLOQUEADO = 0;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context context;
    private List<Usuario> listaUsuariosBloqueados;

    public BloqueadosAdapter(Context contexto, List<Usuario> listaUsuariosBloqueados){
        this.listaUsuariosBloqueados = listaUsuariosBloqueados;
        this.context = contexto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_blocked,parent,false);
        return new BloqueadosAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        final Usuario usuario = listaUsuariosBloqueados.get(posicion);
        holder.usuario.setText(usuario.getUsuario());

        if(usuario.getImagenURL().equals("default")){
            Glide.with(context).load(R.drawable.default_user_picture).into(holder.imagen_perfil);
        }else{
            Glide.with(context).load(usuario.getImagenURL()).into(holder.imagen_perfil);
        }

        holder.imagen_perfil.setBorderColor(context.getColor(R.color.colorPrimary));
        holder.buttonDesbloquear.setText(R.string.desbloquear);
        holder.buttonDesbloquear.setOnClickListener(v -> {
            assert firebaseUser != null;
            Funciones.getBlockUsersListDatabaseReference().child(firebaseUser.getUid()+usuario.getId()).removeValue();
            listaUsuariosBloqueados.clear();
        });

    }

    @Override
    public int getItemCount() {
        if (listaUsuariosBloqueados != null){
            return listaUsuariosBloqueados.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        public CircleImageView imagen_perfil;
        Button buttonDesbloquear;


        ViewHolder(View itemView){
            super(itemView);
            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            buttonDesbloquear = itemView.findViewById(R.id.buttonDesbloquear);
        }
    }

    public int getItemViewType(int posicion) {
        return USUARIO_NO_BLOQUEADO;
    }

}