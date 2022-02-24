package com.example.kapricho.cocina;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.kapricho.cocina.clases.AdaptadorPedidosCocina;
import com.example.kapricho.cocina.clases.ItemsPedidosCocina;
import com.example.kapricho.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class pedidos_cocina extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    LinearLayout layout;
    ArrayList<ItemsPedidosCocina> arrayList;
    AdaptadorPedidosCocina adaptador;
    Window window;
    config server;
    ItemsPedidosCocina items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_cocina);
        toolbar = (Toolbar) findViewById(R.id.toolbar_orders_list_cocina);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_list_pedidos_cocina);
        layout = (LinearLayout) findViewById(R.id.vacio_pedidos__cocina);
        arrayList = new ArrayList<>();
        adaptador = new AdaptadorPedidosCocina(arrayList, this);
        server = new config();
        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(pedidos_cocina.this));
        recyclerView.setAdapter(adaptador);


        recyclerView.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);

        listarPedidos(server.getServer() + "listarPedidos.php", this);

    }

    public void onBackPressed(){
        Intent volver = new Intent(this, MainActivity_cocina.class);
        startActivity(volver);
        finish();
    }

    public void listarPedidos(String url_servidor, Context context){
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                recyclerView.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);

                JSONObject jsonObject;

                for (int i = 0; i < response.length(); i++){
                    try{
                        jsonObject = response.getJSONObject(i);

                        items = new ItemsPedidosCocina();

                        items.setNoPedido(jsonObject.getString("id_pedido"));
                        items.setMesa(jsonObject.getString("mesa"));
                        items.setNombreCliente(jsonObject.getString("nombre_cliente") + " " + jsonObject.getString("apellido_cliente"));
                        items.setEstadoPedido(jsonObject.getString("estado_cocina"));
                        items.setCorreo(jsonObject.getString("correo_cliente"));
                        items.setTotal(jsonObject.getString("total"));
                        arrayList.add(items);
                        adaptador.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recyclerView.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }
}