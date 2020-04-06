package com.btds.app.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Adaptadores.UsuariosAdapter;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.btds.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Amigos extends Fragment {




    private RecyclerView recyclerView;
    private UsuariosAdapter usuariosAdapter;
    private List<Usuario> listaUsuarios;
    private List<Usuario> listaAmigos;
    HashMap<String, UsuarioBloqueado> listaUsuariosBloqueados;
    FloatingActionButton fab;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amigos,container,false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fab = view.findViewById(R.id.button_buscar_amigos);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/


            }
        });

        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //DatabaseReference referenceUserDataBase = FirebaseDatabase.getInstance().getReference("Usuarios");
        //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, referenceUserDataBase, getContext());


        //new TaskProgressBar().execute();

        listaUsuarios = new ArrayList<>();
        obtenerUsuarios();

        //System.out.println("FRAGMENTO CREADO");
        //listaAmigos = new ArrayList<>();

        //registerForContextMenu(recyclerView);
        //obtenerAmigos();
        return view;
    }


    public String limpiarCadenaBaseDatos(String valor){

        int position = valor.lastIndexOf("=");
        String valorFinal = valor.substring(position+1,valor.length()-1);
        return valorFinal;
    }


    public void obtenerUsuarios(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference references = FirebaseDatabase.getInstance().getReference("Usuarios");


        references.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuarios.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    if(usuario !=null){
                        if(!usuario.getId().equals(firebaseUser.getUid())){
                            listaUsuarios.add(usuario);

                        }
                    }
                }
                System.out.println("ARRAY "+listaUsuarios.size());
                usuariosAdapter = new UsuariosAdapter(getContext(),listaUsuarios);
                recyclerView.setAdapter(usuariosAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void obtenerAmigos() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference references = FirebaseDatabase.getInstance().getReference("Usuarios");
        final DatabaseReference amigosUsuario = FirebaseDatabase.getInstance().getReference("Usuarios").child(firebaseUser.getUid()).child("Amigos");
        final HashMap<Integer, String> hashMap = new HashMap<>();


        //amigosUsuario
        amigosUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //RECOGER EL ID DEL AMIGO
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    int i= 0;
                    String valor = limpiarCadenaBaseDatos(dataSnapshot.getValue().toString());
                    hashMap.put(i,valor);
                    System.out.println("VALOR MAPA: "+valor);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        references.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAmigos.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Usuario usuario = snapshot.getValue(Usuario.class);
                    if(usuario !=null){
                        System.out.println("VALOR USUARIO "+usuario.getId());

                        if(!usuario.getId().equals(firebaseUser.getUid()) &&  hashMap.containsValue(usuario.getId())){
                            System.out.println("LISTA AMIGOS "+listaAmigos.size());
                            listaAmigos.add(usuario);
                        }
                    }

                }

                usuariosAdapter = new UsuariosAdapter(getContext(),listaAmigos);
                recyclerView.setAdapter(usuariosAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
