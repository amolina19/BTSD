package com.btds.app.Adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Activitys.MessageActivity;
import com.btds.app.Modelos.PeticionAmistadUsuario;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vdx.designertoast.DesignerToast;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.btds.app.Utils.Funciones.borrarAmigo;

/**
 * @author Alejandro Molina Louchnikov
 */

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder>  {

    private static final int USUARIO_NO_BLOQUEADO = 0;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private Context context;
    private List<Usuario> listaUsuarios;
    private HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados;
    private HashMap<String,String> listaAmigos;


    public UsuariosAdapter(Context contexto, List<Usuario> listaUsuarios, HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados,HashMap<String,String> listaAmigos){
        this.listaUsuarios = listaUsuarios;
        this.listaUsuariosBloqueados = listaUsuariosBloqueados;
        this.listaAmigos = listaAmigos;
        this.context = contexto;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.usuarios_item_friends_fragment,parent,false);
        return new com.btds.app.Adaptadores.UsuariosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int posicion) {

        final Usuario usuario = listaUsuarios.get(posicion);
        holder.usuario.setText(usuario.getUsuario());

        if(usuario.getVisibilidad().getFoto()){
            if(usuario.getImagenURL().equals("default")){
                //private static final int USUARIO_BLOQUEADO = 1;
                Glide.with(context).load(R.drawable.default_user_picture).into(holder.imagen_perfil);
            }else{
                Glide.with(context).load(usuario.getImagenURL()).into(holder.imagen_perfil);
            }
        }else{
            Glide.with(context).load(R.drawable.default_user_picture).into(holder.imagen_perfil);
        }

        if(firebaseUser != null && !listaUsuariosBloqueados.containsKey(firebaseUser.getUid()+usuario.getId())){

            if(usuario.getVisibilidad().getFoto()){
                if(usuario.getEstado().contentEquals(context.getResources().getString(R.string.online))){
                    holder.imagen_perfil.setBorderColor(context.getColor(R.color.spring_green));
                }else{
                    holder.imagen_perfil.setBorderColor(context.getColor(R.color.red));
                }
            }else{
                holder.imagen_perfil.setBorderColor(context.getColor(R.color.cyan));
            }

        }else{
            holder.imagen_perfil.setBorderColor(context.getColor(R.color.colorPrimary));
        }
        holder.verPerfil.setText(R.string.verPerfil);
        holder.verPerfil.setOnClickListener(v -> {

            final Dialog dialogo = new Dialog(context);
            dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogo.setCancelable(true);
            dialogo.setContentView(R.layout.ver_perfil_dialog);

            listaUsuariosBloqueados = Funciones.obtenerUsuariosBloqueados(firebaseUser);

            Button ver_perfil_bloquear = dialogo.findViewById(R.id.ver_perfil_bloquear);
            Button ver_perfil_peticion = dialogo.findViewById(R.id.ver_perfil_peticion);
            Button ver_perfil_dialog_enviar_mensaje = dialogo.findViewById(R.id.ver_perfil_dialog_enviar_mensaje);

            if(listaAmigos.containsKey(usuario.getId())){
                ver_perfil_peticion.setText(R.string.eliminarAmigo);
            }

            if(listaUsuariosBloqueados.containsKey(usuario.getId())){
                ver_perfil_bloquear.setText(R.string.desbloquear);
            }

            CircleImageView imagen_perfil = dialogo.findViewById(R.id.imagen_perfil);

            if(usuario.getVisibilidad().getEnLinea()){
                if(Funciones.obtenerEstadoUsuario(context,usuario)){
                    imagen_perfil.setBorderColor(context.getColor(R.color.spring_green));
                }else{
                    imagen_perfil.setBorderColor(context.getColor(R.color.red));
                }
            }else{
                imagen_perfil.setBorderColor(context.getColor(R.color.cyan));
            }

            if(usuario.getVisibilidad().getFoto()){
                if(usuario.getImagenURL().equals("default")){
                    //private static final int USUARIO_BLOQUEADO = 1;
                    Glide.with(context).load(R.drawable.default_user_picture).into(imagen_perfil);
                }else{
                    Glide.with(context).load(usuario.getImagenURL()).into(imagen_perfil);
                }
            }else{
                Glide.with(context).load(R.drawable.default_user_picture).into(imagen_perfil);
            }


            TextView usuarioCampoPerfil = dialogo.findViewById(R.id.usuarioCampoPerfil);

            if(usuario.getVisibilidad().getUsuario()){
                usuarioCampoPerfil.setText(usuario.getUsuario());
            }else{
                usuarioCampoPerfil.setText(R.string.desconocido);
            }

            TextView descripcionCampoPerfil = dialogo.findViewById(R.id.descripcionCampoPerfil);

            if(usuario.getVisibilidad().getDescripcion()){
                if(usuario.getDescripcion()==null){
                    descripcionCampoPerfil.setText(R.string.sinDescripcion);
                }else{
                    descripcionCampoPerfil.setText(usuario.getDescripcion());
                }
            }else{
                descripcionCampoPerfil.setText(R.string.privado);
            }


            TextView usuarioTelefonoperfil = dialogo.findViewById(R.id.usuarioTelefonoperfil);

            if(usuario.getVisibilidad().getTelefono()){
                if(usuario.getTelefono() == null || usuario.getTelefono().length() == 0){
                    usuarioTelefonoperfil.setText(R.string.telefonoDesconocido);
                }else{
                    usuarioTelefonoperfil.setText(usuario.getTelefono());
                }
            }else{
                usuarioTelefonoperfil.setText(R.string.privado);
            }

            dialogo.show();

           ver_perfil_dialog_enviar_mensaje.setOnClickListener(v12 -> {
               Intent intentChat = new Intent(context, MessageActivity.class);
               intentChat.putExtra("userID",usuario.getId());
               context.startActivity(intentChat);
           });

           ver_perfil_bloquear.setOnClickListener(v1 -> {
               if(listaUsuariosBloqueados.containsKey(usuario.getId())){
                   Funciones.desbloquearUsuario(usuario);
                   DesignerToast.Warning(context,context.getResources().getString(R.string.desbloquear)+" "+ usuario.getUsuario(), Gravity.CENTER, Toast.LENGTH_SHORT);
                   ver_perfil_bloquear.setText(R.string.bloquear);
               }else{
                   Funciones.bloquearUsuario(usuario);
                   DesignerToast.Warning(context,context.getResources().getString(R.string.blockedUser)+" "+ usuario.getUsuario(), Gravity.CENTER, Toast.LENGTH_SHORT);
                   ver_perfil_bloquear.setText(R.string.desbloquear);
               }
           });

           ver_perfil_peticion.setOnClickListener(v13 -> {
               if(listaAmigos.containsKey(usuario.getId())){
                   borrarAmigo(firebaseUser,usuario);
                   DesignerToast.Warning(context,context.getResources().getString(R.string.hasEliminado)+" "+ usuario.getUsuario(), Gravity.CENTER, Toast.LENGTH_SHORT);
                   ver_perfil_peticion.setText(R.string.enviarPeticion);
               }else{
                   DesignerToast.Success(context, context.getResources().getString(R.string.peticionEnviada)+" "+usuario.getUsuario(), Gravity.CENTER, Toast.LENGTH_SHORT);
                   assert firebaseUser != null;
                   PeticionAmistadUsuario peticion = new PeticionAmistadUsuario(firebaseUser.getUid()+""+usuario.getId(),firebaseUser.getUid(),usuario.getId());
                   Funciones.getPeticionesAmistadReference().child(firebaseUser.getUid()+""+usuario.getId()).setValue(peticion);
               }
           });



        });

        //Entra a la actividad
        holder.itemView.setOnClickListener(v -> {
            Intent intentChat = new Intent(context, MessageActivity.class);
            intentChat.putExtra("userID",usuario.getId());
            context.startActivity(intentChat);
        });
    }

    @Override
    public int getItemCount() {
        if (listaUsuarios != null){
            return listaUsuarios.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView usuario;
        public CircleImageView imagen_perfil;
        Button verPerfil;

        ViewHolder(View itemView){
            super(itemView);

            usuario = itemView.findViewById(R.id.usuario);
            imagen_perfil = itemView.findViewById(R.id.imagen_perfil);
            verPerfil = itemView.findViewById(R.id.estado);
        }
    }

    public int getItemViewType(int posicion) {
        return USUARIO_NO_BLOQUEADO;
    }

}