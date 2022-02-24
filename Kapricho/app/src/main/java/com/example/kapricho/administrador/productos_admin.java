package com.example.kapricho.administrador;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.administrador.clases.AdaptadorProductos;
import com.example.kapricho.administrador.clases.ItemUsuarios;
import com.example.kapricho.administrador.clases.ItemsProductos;
import com.example.kapricho.config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class productos_admin extends AppCompatActivity {
    FloatingActionButton add;
    RecyclerView recyclerView;
    LinearLayout layout;
    config server;
    Window window;
    Toolbar toolbar;
    ProgressDialog pd;
    ItemsProductos items;
    ArrayList<ItemsProductos> arrayList;
    AdaptadorProductos adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos_admin);
        add = (FloatingActionButton) findViewById(R.id.add);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_product_list);
        layout = (LinearLayout) findViewById(R.id.vacio_product);
        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));
        toolbar = (Toolbar) findViewById(R.id.toolbar_products_admin);
        arrayList = new ArrayList<>();
        adapter = new AdaptadorProductos(arrayList, productos_admin.this);
        server = new config();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pd = new ProgressDialog(productos_admin.this);
        pd.setMessage("Cargando lista de usuarios...");
        pd.setCancelable(false);
        pd.show();

        recyclerView.setLayoutManager(new LinearLayoutManager(productos_admin.this));
        recyclerView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(productos_admin.this, agregar_producto.class);
                startActivity(i);
                finish();
            }
        });

        listar(server.getServer() + "listarProductos.php", productos_admin.this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity_admin.class);
        startActivity(i);
        finish();
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
                            DecimalFormat formatea = new DecimalFormat("###,###.##");
                            Double precio = Double.parseDouble(jsonObject.getString("precio"));

                            items = new ItemsProductos();
                            items.setId(jsonObject.getString("id_producto"));
                            items.setNombre(jsonObject.getString("descripcion"));
                            items.setImagen(server.getServer() + jsonObject.getString("imagen"));
                            items.setDisponibilidad(jsonObject.getString("existencia"));
                            items.setPrecio(String.valueOf(formatea.format(precio)));
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
                    Toast.makeText(productos_admin.this, "Error al listar", Toast.LENGTH_SHORT).show();
                    Log.i("Error al listar", error.toString());
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }
}