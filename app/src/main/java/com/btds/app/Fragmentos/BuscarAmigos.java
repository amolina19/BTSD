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
import androidx.fragment.app.Fragment;
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
    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private boolean firstSearch;

    /*
    class TaskProgressBar extends AsyncTask<Void, Void, Void> {


        @SuppressLint("WrongThread")
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            for(int i=0;i<100;i+=3){
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setProgress(i);
            }
            progressBar.setVisibility(View.INVISIBLE);
            return null;
        }
    }
    */



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

        //peticionAmistadUsuarios = Funciones.obtenerPeticionesAmistadEnviadas(firebaseUser);

        buscarAmigosAdapter = new BuscarAmigosAdapter(getActivity(),listaUsuarios,peticionAmistadUsuarios);
        recyclerView.setAdapter(buscarAmigosAdapter);

        firstSearch = true;

        //TaskProgressBar taskProgressBar = new TaskProgressBar();
        //taskProgressBar.execute();


        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //DatabaseReference referenceUserDataBase = FirebaseDatabase.getInstance().getReference("Usuarios");
        //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, referenceUserDataBase, getContext());


        obtenerUsuarios();

        //System.out.println("FRAGMENTO CREADO");
        //listaAmigos = new ArrayList<>();

        //registerForContextMenu(recyclerView);
        //obtenerAmigos();
        return view;
    }


    /*
    public String limpiarCadenaBaseDatos(String valor){

        int position = valor.lastIndexOf("=");
        String valorFinal = valor.substring(position+1,valor.length()-1);
        return valorFinal;
    }
     */


    private void obtenerUsuarios(){

            Funciones.getPeticionesAmistadReference().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    peticionAmistadUsuarios.clear();
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        PeticionAmistadUsuario peticion = data.getValue(PeticionAmistadUsuario.class);
                        assert firebaseUser != null;
                        if(peticion.getUsuarioAccionPeticion().contentEquals(firebaseUser.getUid())){
                            peticionAmistadUsuarios.put(data.getKey(),peticion);
                        }
                    }

                    if(firstSearch){
                        firstSearch = false;
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
                                        if(!usuario.getId().equals(firebaseUser.getUid())){
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
                                            if(!usuario.getId().equals(firebaseUser.getUid())){
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