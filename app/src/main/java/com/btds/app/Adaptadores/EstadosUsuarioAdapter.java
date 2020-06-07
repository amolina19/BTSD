package com.btds.app.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.btds.app.Modelos.Estados;
import com.btds.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * @author Alejandro Molina Louchnikov
 */

public class EstadosUsuarioAdapter extends BaseAdapter {

    private Context context;
    private List<Estados> listaEstadosUsuario;
    private LayoutInflater inflter;

    public EstadosUsuarioAdapter(Context context, List<Estados> listaEstadosUsuario) {
        this.context = context;
        this.listaEstadosUsuario = listaEstadosUsuario;
        inflter = (LayoutInflater.from(context));
    }



    @Override
    public int getCount() {
        return listaEstadosUsuario.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = inflter.inflate(R.layout.estados_usuario_items, null); // inflate the layout
        ImageView estadosUsuario = view.findViewById(R.id.estados_usuario_icon); // get the reference of ImageView
        if(estadosUsuario !=null){
            Picasso.with(context).load(String.valueOf(listaEstadosUsuario.get(position).estadoURL)).into(estadosUsuario);
        }
        return view;
    }
}
