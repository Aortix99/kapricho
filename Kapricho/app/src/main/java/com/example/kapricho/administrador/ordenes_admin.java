package com.example.kapricho.administrador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.kapricho.administrador.clases.AdaptadorOrdenes;
import com.example.kapricho.administrador.clases.ItemsOrdenes;
import com.example.kapricho.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ordenes_admin extends AppCompatActivity {
    RecyclerView recyclerView;
    Window window;
    config server;
    Toolbar toolbar;
    AdaptadorOrdenes adaptador;
    ItemsOrdenes items;
    ArrayList<ItemsOrdenes> arrayList;
    ProgressDialog pd;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenes_admin);

        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));
        toolbar = (Toolbar) findViewById(R.id.toolbar_orders_list_admin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        server = new config();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_orders_list);
        layout = (LinearLayout) findViewById(R.id.vacio_orders);
        arrayList = new ArrayList<>();
        adaptador = new AdaptadorOrdenes(arrayList, ordenes_admin.this);
        pd = new ProgressDialog(ordenes_admin.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(ordenes_admin.this));
        recyclerView.setAdapter(adaptador);

        listar(server.getServer() + "listarOrdenes.php", ordenes_admin.this);

    }

    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity_admin.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void listar(String url_servidor, Context context){
        pd.setMessage("Cargando por favor espere...");
        pd.setCancelable(false);
        pd.show();
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

                            items = new ItemsOrdenes();
                            items.setNoOrden(jsonObject.getString("id_pedido"));
                            items.setMesa(jsonObject.getString("mesa"));
                            items.setNombre_cli(jsonObject.getString("nombre_cliente") + " " +
                                    jsonObject.getString("apellido_cliente"));
                            items.setId_cliente(jsonObject.getString("Id_cliente"));
                            items.setTotal(jsonObject.getString("total"));
                            items.setEstatus(jsonObject.getString("estado"));
                            items.setMedido_pago(jsonObject.getString("medio_de_pago"));
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
                    Log.i("Error al listar", error.toString());
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }

}