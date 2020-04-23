package com.btds.app.Fragmentos;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Adaptadores.UsuariosAdapter;
import com.btds.app.Modelos.Usuario;
import com.btds.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BuscarAmigos extends Fragment {


    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText buscarAmigosEditText;
    private UsuariosAdapter usuariosAdapter;
    private List<Usuario> listaUsuarios;

    private boolean firstSearch;

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


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscaramigos,container,false);
        //Adaptador


        progressBar = view.findViewById(R.id.progressBar);
        buscarAmigosEditText = view.findViewById(R.id.buscar_amigos_editText);
        setOnFocusChangeListener(buscarAmigosEditText, "buscarAmigos");
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usuariosAdapter = new UsuariosAdapter(getActivity(),listaUsuarios);
        recyclerView.setAdapter(usuariosAdapter);
        firstSearch = true;

        TaskProgressBar taskProgressBar = new TaskProgressBar();
        taskProgressBar.execute();


        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //DatabaseReference referenceUserDataBase = FirebaseDatabase.getInstance().getReference("Usuarios");
        //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, referenceUserDataBase, getContext());

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

        if(firstSearch){
            firstSearch = false;
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
                    usuariosAdapter = new UsuariosAdapter(getActivity(),listaUsuarios);

                    recyclerView.setAdapter(usuariosAdapter);
                    //usuariosAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }



    private void setOnFocusChangeListener(TextView textView, String name){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference references = FirebaseDatabase.getInstance().getReference("Usuarios");

        textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
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
                                            System.out.println("VALOR USUARIO "+usuario.getId());

                                            if(!usuario.getId().equals(firebaseUser.getUid())){
                                                System.out.println("LISTA AMIGOS "+listaUsuarios.size());

                                                if(usuario.getUsuario().toLowerCase().contains(buscarAmigosEditText.getText().toString().toLowerCase())){
                                                    listaUsuarios.add(usuario);
                                                }

                                            }
                                        }

                                    }

                                    usuariosAdapter = new UsuariosAdapter(getContext(),listaUsuarios);
                                    recyclerView.setAdapter(usuariosAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            if(!s.equals("") ) {

                            }
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
            }
        });
    }


}
