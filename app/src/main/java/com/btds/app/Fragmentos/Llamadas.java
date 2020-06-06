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

import com.btds.app.Adaptadores.LlamadasAdapter;
import com.btds.app.Modelos.Llamada;
import com.btds.app.R;
import com.btds.app.Utils.Funciones;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Alejandro Molina Louchnikov
 */

public class Llamadas extends Fragment {

    private RecyclerView recyclerView;
    private LlamadasAdapter llamadasAdapter;
    private List<Llamada> listaLlamadas;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_llamadas,container,false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        DividerItemDecoration itemDecorator = new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(getActivity(), R.drawable.divider_recycler_view)));
        recyclerView.addItemDecoration(itemDecorator);

        llamadasAdapter = new LlamadasAdapter(getActivity(),listaLlamadas);
        recyclerView.setAdapter(llamadasAdapter);

        //Preffy preffy = Preffy.getInstance(getContext());
        //preffy.putString("FragmentHome", "Amigos");

        //FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //DatabaseReference referenceUserDataBase = FirebaseDatabase.getInstance().getReference("Usuarios");
        //Funciones.actualizarConexion(getResources().getString(R.string.online), firebaseUser, referenceUserDataBase, getContext());


        //new TaskProgressBar().execute();

        listaLlamadas = new ArrayList<>();
        obtenerLlamadas();

        //System.out.println("FRAGMENTO CREADO");
        //listaAmigos = new ArrayList<>();

        //registerForContextMenu(recyclerView);
        //obtenerAmigos();
        return view;
    }



    private void obtenerLlamadas(){
        //final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //DatabaseReference references = FirebaseDatabase.getInstance().getReference("Usuarios");


        assert firebaseUser != null;
        Funciones.getLlamadasReferenceFragment().child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaLlamadas.clear();
                for (DataSnapshot data:dataSnapshot.getChildren()){
                    Llamada llamada = data.getValue(Llamada.class);
                    listaLlamadas.add(llamada);
                }

                llamadasAdapter = new LlamadasAdapter(getContext(),listaLlamadas);
                //usuariosAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(llamadasAdapter);
                Collections.reverse(listaLlamadas);
                llamadasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
