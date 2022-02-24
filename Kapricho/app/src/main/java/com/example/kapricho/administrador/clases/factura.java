package com.example.kapricho.administrador.clases;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.administrador.ordenes_admin;
import com.example.kapricho.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class factura extends AppCompatActivity {
    TextView pedido, cliente, fecha, usuario, total, estado;
    RecyclerView recyclerView;
    Window window;
    config server;
    Toolbar toolbar;
    AdaptadorFactura adaptador;
    ItemFactura items;
    ArrayList<ItemFactura> arrayList;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura);

        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));
        toolbar = (Toolbar) findViewById(R.id.toolbar_factura_admin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        server = new config();
        recyclerView = (RecyclerView) findViewById(R.id.detalle_factura_cocina);
        pedido = (TextView) findViewById(R.id.pedido_factura);
        cliente = (TextView) findViewById(R.id.cliente_factura);
        fecha = (TextView) findViewById(R.id.fecha_factura);
        usuario = (TextView) findViewById(R.id.usario_factura);
        estado = (TextView) findViewById(R.id.textView_estado_factura);
        total = (TextView) findViewById(R.id.total_factura);
        arrayList = new ArrayList<>();
        adaptador = new AdaptadorFactura(arrayList, factura.this);

        pd = new ProgressDialog(this);
        pd.setMessage("Cargando por favor espere...");
        pd.setCancelable(false);
        pd.show();

        recyclerView.setLayoutManager(new LinearLayoutManager(factura.this));
        recyclerView.setAdapter(adaptador);


        String venta = getIntent().getStringExtra("id_pedido");
        String cliente = getIntent().getStringExtra("id_cliente");

        listarDetalle(server.getServer() + "detalleFactura.php?id_pedido=" + venta, factura.this);

        listarDatosFactura(server.getServer() + "datosPedido.php?id_pedido=" + venta +
                "&id_cliente=" + cliente, factura.this);

    }

    public void onBackPressed(){
        Intent i = new Intent(this, ordenes_admin.class);
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


    public void listarDatosFactura(String url_servidor, Context context) {
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    pd.hide();

                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);

                            DecimalFormat formatea = new DecimalFormat("###,###.##");
                            Double precio = Double.parseDouble(jsonObject.getString("total"));
                            String recaudo = String.valueOf(formatea.format(precio));

                            pedido.setText("Pedido No: " + jsonObject.getString("id_pedido"));
                            getSupportActionBar().setSubtitle("Pedido No: " + jsonObject.getString("id_pedido"));
                            cliente.setText("Cliente: " +jsonObject.getString("nombre_cliente") + " " +
                                    jsonObject.getString("apellido_cliente"));
                            fecha.setText("Fecha: " + jsonObject.getString("fecha") + " " +
                                    jsonObject.getString("hora"));
                            total.setText("Total a pagar: $" + recaudo + " COP");

                            if(jsonObject.getString("estado").toString().equals("Anulado")){
                                estado.setText(jsonObject.getString("estado"));
                                estado.setVisibility(View.VISIBLE);
                                estado.setTextColor(Color.parseColor("#32A31116"));
                            } else if(jsonObject.getString("estado").toString().equals("Sin pagar")){
                                estado.setText("No paga");
                                estado.setVisibility(View.VISIBLE);
                                estado.setTextColor(Color.parseColor("#3ECCAD14"));
                            } else if(jsonObject.getString("estado").toString().equals("Pagado")){
                                estado.setVisibility(View.GONE);
                            } else if(jsonObject.getString("estado").toString().equals("Cancelado")){
                                estado.setText(jsonObject.getString("estado"));
                                estado.setVisibility(View.VISIBLE);
                                estado.setTextColor(Color.parseColor("#32A31116"));
                            }
                        } catch (JSONException e){
                            Log.i("error catch datos", e.toString());
                        }
                    }
                } else {
                    pd.hide();
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    recyclerView.setVisibility(View.GONE);
                    pd.hide();
                } else {
                    pd.hide();
                    Toast.makeText(context, "Error al listar", Toast.LENGTH_SHORT).show();
                    Log.i("Error al listar", error.toString());
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }

    public void listarDetalle(String url_servidor, Context context) {
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    pd.hide();
                    recyclerView.setVisibility(View.VISIBLE);
                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);

                            DecimalFormat formatea = new DecimalFormat("###,###.##");
                            Double precio = Double.parseDouble(jsonObject.getString("precio_total"));
                            String recaudo = String.valueOf(formatea.format(precio));
                            Double unit_d = Double.parseDouble(jsonObject.getString("total"));
                            String unit = String.valueOf(formatea.format(unit_d));

                            items = new ItemFactura();

                            items.setProducto(jsonObject.getString("descripcion"));
                            items.setCantidad(jsonObject.getString("cantidad"));
                            items.setUnit(unit);
                            items.setTotal(recaudo);

                            arrayList.add(items);

                            adaptador.notifyDataSetChanged();

                        } catch (JSONException e){
                            Log.i("error catch detalle", e.toString());
                        }
                    }
                } else {
                    pd.hide();
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    recyclerView.setVisibility(View.GONE);
                    pd.hide();
                } else {
                    pd.hide();
                    Toast.makeText(context, "Error al listar", Toast.LENGTH_SHORT).show();
                    Log.i("Error al listar detalle", error.toString());
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }

}