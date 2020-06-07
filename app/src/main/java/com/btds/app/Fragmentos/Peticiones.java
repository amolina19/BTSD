package com.btds.app.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Adaptadores.BloqueadosAdapter;
import com.btds.app.Adaptadores.PeticionesAdapter;
import com.btds.app.Modelos.PeticionAmistadUsuario;
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


/**
 * @author Alejandro Molina Louchnikov
 */

public class Peticiones extends Fragment {

    private RecyclerView recyclerViewPeticiones, recyclerViewBloqueados;

    private PeticionesAdapter peticionesAdapter;
    private BloqueadosAdapter bloqueadosAdapter;

    private List<Usuario> listaPeticiones, listaBloqueados;
    private HashMap<String,String> listaAmigos = new HashMap<>();
    private HashMap<String,PeticionAmistadUsuario> listaUsuariosEncontrados = new HashMap<>();
    private HashMap<String,UsuarioBloqueado> listaUsuariosBloqueados = new HashMap<>();

    private Button buttonSitch;
    private TextView textView;

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_peticiones_bloqueados,container,false);

        buttonSitch = view.findViewById(R.id.buttonSwitch);
        textView = view.findViewById(R.id.textView);

        recyclerViewPeticiones = view.findViewById(R.id.recycler_view_peticiones);
        recyclerViewPeticiones.setHasFixedSize(true);
        recyclerViewPeticiones.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewBloqueados = view.findViewById(R.id.recycler_view_bloqueados);
        recyclerViewBloqueados.setHasFixedSize(true);
        recyclerViewBloqueados.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewBloqueados.setVisibility(View.GONE);

        DividerItemDecoration itemDecorator = new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.divider_recycler_view)));
        recyclerViewBloqueados.addItemDecoration(itemDecorator);
        recyclerViewPeticiones.addItemDecoration(itemDecorator);

        buttonSitch.setOnClickListener(v -> {


            if(textView.getText().toString().contentEquals(getString(R.string.bloqueados))){
                recyclerViewBloqueados.setVisibility(View.GONE);
                recyclerViewPeticiones.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.peticionesPendientes));
                buttonSitch.setText(getString(R.string.bloqueados));
            }else{
                recyclerViewPeticiones.setVisibility(View.GONE);
                recyclerViewBloqueados.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.bloqueados));
                buttonSitch.setText(getString(R.string.peticionesPendientes));
            }
        });

        listaPeticiones = new ArrayList<>();
        listaBloqueados = new ArrayList<>();


        peticionesAdapter = new PeticionesAdapter(getActivity(),listaPeticiones,listaAmigos);
        recyclerViewPeticiones.setAdapter(peticionesAdapter);

        bloqueadosAdapter = new BloqueadosAdapter(getActivity(),listaBloqueados);
        recyclerViewBloqueados.setAdapter(bloqueadosAdapter);

        obtenerPeticiones();
        return view;
    }



    private void obtenerPeticiones(){
        obtenerAmigos();
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

                Funciones.getPeticionesAmistadReference().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaUsuariosEncontrados.clear();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            PeticionAmistadUsuario peticionAmistadUsuario = snapshot.getValue(PeticionAmistadUsuario.class);
                            if(peticionAmistadUsuario !=null){
                                assert firebaseUser != null;
                                if(peticionAmistadUsuario.getUsuarioEnviadoPeticion().contentEquals(firebaseUser.getUid())){
                                    if(!listaAmigos.containsKey(peticionAmistadUsuario.getUsuarioAccionPeticion())){
                                        listaUsuariosEncontrados.put(peticionAmistadUsuario.getUsuarioAccionPeticion(),peticionAmistadUsuario);
                                    }else{
                                        Funciones.eliminarPeticion(peticionAmistadUsuario.getKey());
                                    }

                                }
                            }
                        }

                        Funciones.getUsersDatabaseReference().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                listaPeticiones.clear();
                                for(DataSnapshot data:dataSnapshot.getChildren()){
                                    Usuario usuario = data.getValue(Usuario.class);
                                    assert usuario != null;
                                    if(listaUsuariosEncontrados.containsKey(usuario.getId())){
                                        listaPeticiones.add(usuario);
                                    }
                                    assert firebaseUser != null;
                                    if(listaUsuariosBloqueados.containsKey(firebaseUser.getUid()+usuario.getId())){
                                        listaBloqueados.add(usuario);
                                    }
                                }

                                peticionesAdapter = new PeticionesAdapter(getContext(),listaPeticiones,listaAmigos);
                                recyclerViewPeticiones.setAdapter(peticionesAdapter);
                                peticionesAdapter.notifyDataSetChanged();

                                bloqueadosAdapter = new BloqueadosAdapter(getContext(),listaBloqueados);
                                recyclerViewBloqueados.setAdapter(bloqueadosAdapter);
                                bloqueadosAdapter.notifyDataSetChanged();
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
