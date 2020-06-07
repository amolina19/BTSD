package com.btds.app.Fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Adaptadores.UsuariosAdapter;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import in.shrinathbhosale.preffy.Preffy;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Amigos extends Fragment {

    private RecyclerView recyclerView;
    private UsuariosAdapter usuariosAdapter;
    private List<Usuario> listaUsuarios;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private HashMap<String, UsuarioBloqueado> listaUsuariosBloqueados = new HashMap<>();
    private HashMap<String,String> listaAmigos = new HashMap<>();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amigos,container,false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.divider_recycler_view)));
        recyclerView.addItemDecoration(itemDecorator);

        usuariosAdapter = new UsuariosAdapter(getActivity(),listaUsuarios,listaUsuariosBloqueados,listaAmigos);
        recyclerView.setAdapter(usuariosAdapter);

        Preffy preffy = Preffy.getInstance(getContext());
        preffy.putString("FragmentHome", "Amigos");

        listaUsuarios = new ArrayList<>();
        obtenerAmigos();
        return view;
    }


    private void obtenerAmigos(){

        assert firebaseUser != null;
        Funciones.getAmigosReference().child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAmigos.clear();
                for(DataSnapshot data:dataSnapshot.getChildren()){
                    String valorClaveAmigo = data.getValue(String.class);
                    listaAmigos.put(valorClaveAmigo,valorClaveAmigo);
                }

                obtenerUsuarios();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerUsuarios(){

        Funciones.getBlockUsersListDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuariosBloqueados.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    UsuarioBloqueado usuarioBloqueado = data.getValue(UsuarioBloqueado.class);
                    assert usuarioBloqueado != null;
                    assert firebaseUser != null;
                    if(usuarioBloqueado.getUsuarioAccionBloquear().contentEquals(firebaseUser.getUid())){
                        listaUsuariosBloqueados.put(data.getKey(),usuarioBloqueado);
                    }
                }

                Funciones.getUsersDatabaseReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaUsuarios.clear();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Usuario usuario = snapshot.getValue(Usuario.class);
                            if(usuario !=null){
                                assert firebaseUser != null;
                                if(!usuario.getId().equals(firebaseUser.getUid())  && listaAmigos.containsKey(usuario.getId())){
                                    listaUsuarios.add(usuario);
                                }
                            }
                        }
                        //System.out.println("ARRAY "+listaUsuarios.size());
                        Log.d("DEBUG Fragment Amigos","Lista de Amigos "+listaUsuarios.size() );
                        Log.d("DEBUG Fragment Amigos","Lista de Amigos Bloqueados "+listaUsuariosBloqueados.size());
                        usuariosAdapter = new UsuariosAdapter(getContext(),listaUsuarios,listaUsuariosBloqueados,listaAmigos);
                        //usuariosAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(usuariosAdapter);
                        usuariosAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
