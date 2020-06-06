package com.btds.app.Fragmentos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Adaptadores.BuscarAmigosAdapter;
import com.btds.app.Modelos.PeticionAmistadUsuario;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * @author Alejandro Molina Louchnikov
 */
public class BuscarAmigos extends Fragment {


    private RecyclerView recyclerView;
    private EditText buscarAmigosEditText;
    private BuscarAmigosAdapter buscarAmigosAdapter;
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private static HashMap<String,PeticionAmistadUsuario> peticionAmistadUsuarios = new HashMap<>();
    private static HashMap<String,String> listaAmigos = new HashMap<>();
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscaramigos,container,false);
        //Adaptador
        //ProgressBar progressBar = view.findViewById(R.id.progressBar);
        buscarAmigosEditText = view.findViewById(R.id.buscar_amigos_editText);
        setOnFocusChangeListener(buscarAmigosEditText);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DividerItemDecoration itemDecorator = new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getActivity(), R.drawable.divider_recycler_view)));
        recyclerView.addItemDecoration(itemDecorator);

        buscarAmigosAdapter = new BuscarAmigosAdapter(getActivity(),listaUsuarios,peticionAmistadUsuarios);
        recyclerView.setAdapter(buscarAmigosAdapter);
        obtenerAmigos();
        return view;
    }

    private void obtenerAmigos(){

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

            Funciones.getPeticionesAmistadReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //obtenerAmigos();
                    peticionAmistadUsuarios.clear();
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        PeticionAmistadUsuario peticion = data.getValue(PeticionAmistadUsuario.class);
                        assert firebaseUser != null;
                        if(peticion.getUsuarioAccionPeticion().contentEquals(firebaseUser.getUid()) && !listaAmigos.containsKey(peticion.getUsuarioAccionPeticion())){
                            peticionAmistadUsuarios.put(data.getKey(),peticion);
                        }
                    }


                        //Log.d("DEBUG BuscarAmigosAdapter","Lista de peticiones enviadas "+peticionAmistadUsuarios.size());
                        //DatabaseReference references = FirebaseDatabase.getInstance().getReference("Usuarios");

                        Funciones.getUsersDatabaseReference().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                listaUsuarios.clear();
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    Usuario usuario = snapshot.getValue(Usuario.class);
                                    if(usuario !=null){
                                        assert firebaseUser != null;
                                        if(!usuario.getId().equals(firebaseUser.getUid())  && !listaAmigos.containsKey(usuario.getId())){
                                            listaUsuarios.add(usuario);

                                        }
                                    }
                                }
                                Log.d("DEBUG Buscar Amigos", String.valueOf(listaUsuarios.size()));
                                Log.d("DEBUG BuscarAmigosAdapter","Lista de peticiones enviadas "+peticionAmistadUsuarios.size());
                                buscarAmigosAdapter = new BuscarAmigosAdapter(getActivity(),listaUsuarios,peticionAmistadUsuarios);

                                recyclerView.setAdapter(buscarAmigosAdapter);
                                buscarAmigosAdapter.notifyDataSetChanged();
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



    private void setOnFocusChangeListener(TextView textView){

        try {

            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference references = FirebaseDatabase.getInstance().getReference("Usuarios");

            textView.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus) {
                    //Toast.makeText(BuscarAmigos.this, "ESTAS EN EL TECLADO", Toast.LENGTH_SHORT).show();
                    buscarAmigosEditText.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            references.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    listaUsuarios.clear();

                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                                        Usuario usuario = snapshot.getValue(Usuario.class);
                                        if(usuario !=null){
                                            Log.d("DEBUG Buscar Amigos","VALOR USUARIO "+usuario.getId());

                                            assert firebaseUser != null;
                                            if(!usuario.getId().equals(firebaseUser.getUid()) && !listaAmigos.containsKey(usuario.getId())){
                                                Log.d("DEBUG Buscar Amigos","LISTA NUEVOS AMIGOS SIN AGREGAR "+listaUsuarios.size());

                                                if(usuario.getUsuario().toLowerCase().contains(buscarAmigosEditText.getText().toString().toLowerCase())){
                                                    listaUsuarios.add(usuario);
                                                }

                                            }
                                        }

                                    }

                                    buscarAmigosAdapter = new BuscarAmigosAdapter(getContext(),listaUsuarios,peticionAmistadUsuarios);
                                    recyclerView.setAdapter(buscarAmigosAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            /*
                            if(!s.equals("") ) {

                            }

                             */
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            System.out.println("ESTAS ESCRIBIENDO");
                            /*if((cadenaTeclado = enviar_texto.getText().toString()).length() > 0){

                                //Funciones.escribiendo(firebaseUser,contexto);
                            }else{

                            }*/
                        }
                    });
                }
            });

        } catch (Throwable t) {
            t.printStackTrace();
        }
        completedFuture(null);
    }


}