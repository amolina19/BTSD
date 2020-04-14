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
import com.btds.app.Modelos.Mensaje;
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
import java.util.HashMap;
import java.util.List;



public class Chats extends Fragment {

    private RecyclerView recyclerView;

    private UsuariosAdapter usuariosAdapter;
    private List<Usuario> ListaUsuariosObject;

    FirebaseUser fUser;
    DatabaseReference databaseReference;

    private List<String> ListaUsuariosIdString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ListaUsuariosIdString = new ArrayList<String>();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ListaUsuariosIdString.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Mensaje mensaje = snapshot.getValue(Mensaje.class);

                    if(mensaje.getEmisor().contentEquals(fUser.getUid())){
                        ListaUsuariosIdString.add(mensaje.getReceptor());
                    }

                    if(mensaje.getReceptor().contentEquals(fUser.getUid())){
                        ListaUsuariosIdString.add(mensaje.getEmisor());
                    }
                }

                leerChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return  view;
    }

    private void leerChats(){

        ListaUsuariosObject = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Usuarios");

        final HashMap<String,Usuario> hashMap = new HashMap<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ListaUsuariosObject.clear();
                hashMap.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    for (String id : ListaUsuariosIdString) {
                        if (usuario.getId().contentEquals(id)) {
                            if (ListaUsuariosObject.size() != 0) {
                                for (int i = 0;i<ListaUsuariosObject.size();i++) {
                                    if (!hashMap.containsKey(usuario.getId()) && !usuario.getId().contentEquals(fUser.getUid())) {
                                        ListaUsuariosObject.add(usuario);
                                        hashMap.put(usuario.getId(),usuario);
                                    }
                                }
                            } else if(!usuario.getId().contentEquals(fUser.getUid())){
                                hashMap.put(usuario.getId(),usuario);
                                ListaUsuariosObject.add(usuario);
                            }
                        }
                    }
                }
                usuariosAdapter = new UsuariosAdapter(getContext(),ListaUsuariosObject);
                recyclerView.setAdapter(usuariosAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
