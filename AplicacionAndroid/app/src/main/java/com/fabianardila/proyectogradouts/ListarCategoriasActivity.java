package com.fabianardila.proyectogradouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fabianardila.proyectogradouts.modelo.Categorias;
import com.fabianardila.proyectogradouts.vista.activity.BuscarLibroActivity;
import com.fabianardila.proyectogradouts.vista.activity.ListadoLibrosActivity;
import com.fabianardila.proyectogradouts.widget.SpacingItemDecoration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListarCategoriasActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;

    List<Categorias> items = null;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_categorias);

        mFirestore = FirebaseFirestore.getInstance();

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categorias de libros");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal_no_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, BuscarLibroActivity.class));
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponent() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        items = obtenerItems();
    }

    private int posicion = 0;
    private void laPosicion() {
        posicion = 1;
    }

    private List<Categorias> obtenerItems() {

        posicion = 1;
        List<Categorias> items = new ArrayList<>();
        Categorias catTodos = new Categorias("1000", "TODOS");
        items.add(catTodos);
        mFirestore.collection("categoriasDeLibros")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for(DocumentSnapshot doc:task.getResult()){
                            Categorias categ = new Categorias(doc.getString("id"),
                                    doc.getString("title"));
                            items.add(categ);
                            cargarAdaptador();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR", "No se encontraron categorias");
                        Toast.makeText(ListarCategoriasActivity.this,
                                ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        return items;
    }

    private void cargarAdaptador() {
        AdaptadorCategoriasGrid mAdapter = new AdaptadorCategoriasGrid(this, items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdaptadorCategoriasGrid.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Categorias categorias, int position) {
                Intent intent = new Intent();
                intent.putExtra("filter", "categoria");
                intent.putExtra("dataFilter", categorias.getId());
                intent.setClass(ListarCategoriasActivity.this, ListadoLibrosActivity.class);
                startActivity(intent);
            }
        });
    }
}
