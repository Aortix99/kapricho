package com.example.kapricho.administrador;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.administrador.clases.AdaptadorListaUsuarios;
import com.example.kapricho.administrador.clases.ItemUsuarios;
import com.example.kapricho.config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class usuarios_admin extends AppCompatActivity {
    LinearLayout layout;
    RecyclerView recyclerView;
    AdaptadorListaUsuarios adaptador;
    ItemUsuarios items;
    ArrayList<ItemUsuarios> arrayList;
    Window window;
    config ip;
    ProgressDialog pd;
    Toolbar toolbar;
    FloatingActionButton add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_admin);

        layout = (LinearLayout) findViewById(R.id.vacio);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_user_list);
        arrayList = new ArrayList<>();
        adaptador = new AdaptadorListaUsuarios(arrayList, usuarios_admin.this);
        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));
        pd = new ProgressDialog(usuarios_admin.this);
        ip = new config();
        toolbar = (Toolbar) findViewById(R.id.toolbar_users_admin);
        add = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new GridLayoutManager(usuarios_admin.this, 2, GridLayoutManager.VERTICAL,
                false));
        recyclerView.setAdapter(adaptador);

        pd.setMessage("Cargando lista de usuarios...");
        pd.setCancelable(false);
        pd.show();

        listar(ip.getServer() + "listar.php", usuarios_admin.this);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ad = new Intent(usuarios_admin.this, agregar_usuario_admin.class);
                startActivity(ad);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_user_page, menu);
        iconColor(menu, R.color.white);
        MenuItem searchItem = menu.findItem(R.id.buscar_menu);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Escribe aquí para buscar...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String textoFiltrado) {
                adaptador.getFilter().filter(textoFiltrado);
                return true;
            }
        });
        return true;
    }
    public void iconColor(Menu menu, int color) {
        for (int i = 0; i < menu.size(); i++ ){
            Drawable drawable2 = menu.getItem(i).getIcon();
            if (drawable2 != null){
                drawable2.mutate();
                drawable2.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onStop() {
        super.onStop();
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }

    public void listar(String url_servidor, Context context){
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    layout.setVisibility(View.GONE);
                    pd.hide();

                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);

                            items = new ItemUsuarios();
                            items.setId(jsonObject.getString("id_usuario"));
                            items.setNombre(jsonObject.getString("nombre") + " " + jsonObject.getString("apellido"));
                            items.setRol(jsonObject.getString("rol"));
                            arrayList.add(items);

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    pd.hide();
                    recyclerView.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    recyclerView.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    pd.hide();
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Error: \n" + error);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }

    public void onBackPressed(){
        Intent back = new Intent(usuarios_admin.this, MainActivity_admin.class);
        startActivity(back);
        finish();
    }
}