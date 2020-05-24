package com.btds.app.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.btds.app.Adaptadores.ChatsAdapter;
import com.btds.app.Modelos.ListaMensajesChat;
import com.btds.app.Modelos.Mensaje;
import com.btds.app.Modelos.Usuario;
import com.btds.app.Modelos.UsuarioBloqueado;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import in.shrinathbhosale.preffy.Preffy;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * @author Alejandro Molina Louchnikov
 */
public class Chats extends Fragment {

    private RecyclerView recyclerView;
    private HashMap<String, UsuarioBloqueado> listaUsuariosBloqueados = new HashMap<>();
    private HashMap<String,Usuario > usuarioHashMap = new HashMap<>();


    private ChatsAdapter chatsAdapter;
    private List<Usuario> ListaUsuariosObject;

    final private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference usersDatabaseReference;

    private List<String> ListaUsuariosIdString;
    private List<ListaMensajesChat> listaMensajesChats = new ArrayList<>();
    private List<Mensaje> ListaMensajes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ListaUsuariosIdString = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Preffy preffy = Preffy.getInstance(getContext());
        preffy.putString("FragmentHome", "Chats");

        //Genera las Lineas en el Recycler View
        DividerItemDecoration itemDecorator = new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getActivity(), R.drawable.divider_recycler_view)));
        recyclerView.addItemDecoration(itemDecorator);

        listaMensajesChats = Funciones.ordernarChat(ListaUsuariosObject,ListaMensajes);
        chatsAdapter = new ChatsAdapter(getActivity(),listaMensajesChats);
        recyclerView.setAdapter(chatsAdapter);


        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ListaUsuariosIdString.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Mensaje mensaje = snapshot.getValue(Mensaje.class);

                    assert mensaje != null;
                    assert firebaseUser != null;
                    if(mensaje.getEmisor().contentEquals(firebaseUser.getUid())){
                        ListaUsuariosIdString.add(mensaje.getReceptor());
                        ListaMensajes.add(mensaje);
                    }

                    if(mensaje.getReceptor().contentEquals(firebaseUser.getUid())){
                        ListaUsuariosIdString.add(mensaje.getEmisor());
                        ListaMensajes.add(mensaje);
                    }
                }
                Collections.reverse(ListaMensajes);
                leerChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return  view;
    }

    private void leerChats(){

        try{

            ListaUsuariosObject = new ArrayList<>();
            usersDatabaseReference = Funciones.getUsersDatabaseReference();

            final HashMap<String,Usuario> hashMap = new HashMap<>();

            usersDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ListaUsuariosObject.clear();
                    hashMap.clear();

                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Usuario usuario = snapshot.getValue(Usuario.class);
                        for (String id : ListaUsuariosIdString) {
                            assert usuario != null;
                            if (usuario.getId().contentEquals(id)) {
                                if (ListaUsuariosObject.size() != 0) {
                                    for (int i = 0;i<ListaUsuariosObject.size();i++) {
                                        assert firebaseUser != null;
                                        if (!hashMap.containsKey(usuario.getId()) && !usuario.getId().contentEquals(firebaseUser.getUid())) {
                                            ListaUsuariosObject.add(usuario);
                                            hashMap.put(usuario.getId(),usuario);
                                        }
                                    }
                                } else {
                                    assert firebaseUser != null;
                                    if(!usuario.getId().contentEquals(firebaseUser.getUid())){
                                        hashMap.put(usuario.getId(),usuario);
                                        ListaUsuariosObject.add(usuario);
                                    }
                                }
                            }
                        }
                    }

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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    listaMensajesChats = Funciones.ordernarChat(ListaUsuariosObject,ListaMensajes);
                    Collections.sort(listaMensajesChats);
                    chatsAdapter = new ChatsAdapter(getActivity(),listaMensajesChats);
                    //usuariosAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(chatsAdapter);
                    chatsAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
        completedFuture(null);
    }
}
