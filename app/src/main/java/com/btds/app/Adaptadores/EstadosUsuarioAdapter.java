package com.btds.app.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.btds.app.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EstadosUsuarioAdapter extends BaseAdapter {


    Context context;
    List<String> listaEstadosUsuario;
    LayoutInflater inflter;

    public EstadosUsuarioAdapter(Context applicationContext, List<String> listaEstadosUsuario) {
        this.context = applicationContext;
        this.listaEstadosUsuario = listaEstadosUsuario;
        System.out.println("TAMAÑO IMANGES DENTRO ADAPTADOR: " +listaEstadosUsuario.size());
        inflter = (LayoutInflater.from(applicationContext));
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
            System.out.println(listaEstadosUsuario.get(position));
            Picasso.with(context).load(String.valueOf(listaEstadosUsuario.get(position))).into(estadosUsuario);
            System.out.println(listaEstadosUsuario.get(position));
            //estadosUsuario.setImageResource(logos[i]); // set logo images
        }


        return view;

    }
}
