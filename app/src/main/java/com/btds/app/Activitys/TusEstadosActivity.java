package com.btds.app.Activitys;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.btds.app.Adaptadores.EstadosUsuarioAdapter;
import com.btds.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alejandro Molina Louchnikov
 */

public class TusEstadosActivity extends AppCompatActivity {

    private List<String> listaTusEstados;
    GridView gridViewEstados;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tus_estados);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tus Estados");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listaTusEstados = cargarPrueba();
        System.out.println("TAMAÑO LISTA IMAGENES "+listaTusEstados.size());

        gridViewEstados = findViewById(R.id.gridview_tus_estados);
        gridViewEstados.setVerticalSpacing(1);
        gridViewEstados.setHorizontalSpacing(1);

        EstadosUsuarioAdapter estadosUsuarioAdapter = new EstadosUsuarioAdapter(getApplicationContext(),listaTusEstados);
        gridViewEstados.setAdapter(estadosUsuarioAdapter);

        gridViewEstados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(TusEstadosActivity.this, "HAS SELECCIONADO LA POSICION "+position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.estados_menu_toolbar, menu);
        return true;
    }

    private List<String> cargarPrueba(){

        List<String> listImagesUrl = new ArrayList<>();
        listImagesUrl.add("https://d24jx5gocr6em0.cloudfront.net/wp-content/uploads/2020/04/03160041/negros-con-ataud-520x350.jpg");
        listImagesUrl.add("https://upload.wikimedia.org/wikipedia/commons/thumb/9/9a/Flag_of_Spain.svg/1200px-Flag_of_Spain.svg.png");
        return listImagesUrl;
    }
}
