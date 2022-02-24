package com.example.kapricho.cliente.fragments.clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.cliente.fragments.pedidos_cliente;
import com.example.kapricho.cocina.clases.AdaptadorGestionPedido;
import com.example.kapricho.cocina.clases.ItemGestionPedido;
import com.example.kapricho.config;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link detalle_pedido_cliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class detalle_pedido_cliente extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    Toolbar toolbar;
    ArrayList<ItemDetallePedidoCliente> arrayList;
    AdaptadorDetallePedidoCliente adaptador;
    ItemDetallePedidoCliente items;
    config server;
    String venta;
    String mesa;
    String estado;

    public detalle_pedido_cliente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment detalle_pedido_cliente.
     */
    // TODO: Rename and change types and number of parameters
    public static detalle_pedido_cliente newInstance(String param1, String param2) {
        detalle_pedido_cliente fragment = new detalle_pedido_cliente();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_detalle_pedido_cliente, container, false);

        toolbar = vista.findViewById(R.id.toolbar_detalle_pedido_cliente);
        recyclerView = vista.findViewById(R.id.detalle_del_pedido_cliente);
        arrayList = new ArrayList<>();
        adaptador = new AdaptadorDetallePedidoCliente(arrayList, getActivity());
        server = new config();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adaptador);

        venta = AdaptadorPedidoCliente.id_pedido;
        mesa = AdaptadorPedidoCliente.mesa;
        estado = AdaptadorPedidoCliente.estado;

        toolbar.setTitle("Pedido No: " + venta);
        toolbar.setSubtitle("Mesa: " + mesa);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedidos_cliente pedidos = new pedidos_cliente();
                FragmentTransaction transaction = ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container_layout_cliente, pedidos);
                transaction.commit();
            }
        });

        listarPedido(server.getServer() + "detalleFactura.php?id_pedido=" + venta, getActivity());

        return vista;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_estado, menu);
        textMenuColor(menu, Color.WHITE);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void textMenuColor(Menu menu, int color) {
        for (int i = 0; i < menu.size(); i++ ){
            MenuItem item = menu.getItem(i);
            SpannableString s = new SpannableString("Cancelar");
            s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
            item.setTitle(s);
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.cancelar_pedido){
            if(estado.equals("Entregado")){
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getActivity());
                alert.setMessage("Su pedido ya fue entregado, no se puede cancelar");
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            } else if(estado.equals("Cancelado")){
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getActivity());
                alert.setMessage("Su pedido ya fue cancelado");
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            } else if(estado.equals("Anulado")){
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getActivity());
                alert.setMessage("Su pedido fue anulado o devuelto, no se puede cancelar");
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            } else {
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getActivity());
                alert.setMessage("¿Esta seguro que desea cancelar este pedido?");
                alert.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelarPedido(server.getServer() + "actualizarPedido.php", venta, getActivity());
                    }
                });
                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void cancelarPedido(String url_server, String id_pedido, Context context){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Cancelando pedido...");
        pd.show();

        StringRequest string = new StringRequest(Request.Method.POST, url_server, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("Actualizacion exitosa")){
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getActivity());
                    alert.setMessage("Su pedido ha sido cancelado con éxito");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            pedidos_cliente pedidos = new pedidos_cliente();
                            FragmentTransaction transaction = ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.container_layout_cliente, pedidos);
                            transaction.commit();
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                } else if(response.equals("Error al actualizar")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Ocurrio un error al intentar cancelar su pedido, por favor intente nuevamente " +
                            "más tarde");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage(response);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_pedido", id_pedido);
                parametros.put("estado", "Cancelado");
                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(string);
    }

    public void listarPedido(String url_servidor, Context context) {
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++){
                    try{
                        jsonObject = response.getJSONObject(i);

                        items = new ItemDetallePedidoCliente();

                        items.setDescripcion(jsonObject.getString("descripcion"));
                        items.setCantidad(jsonObject.getString("cantidad"));
                        items.setImagen(server.getServer() + jsonObject.getString("imagen"));

                        arrayList.add(items);

                        adaptador.notifyDataSetChanged();

                    } catch (JSONException e){
                        Log.i("error catch detalle", e.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){

                } else {
                    Log.i("Error al listar detalle", error.toString());
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }
}