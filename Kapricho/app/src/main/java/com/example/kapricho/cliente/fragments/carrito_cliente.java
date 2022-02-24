package com.example.kapricho.cliente.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.administrador.productos_admin;
import com.example.kapricho.cliente.fragments.clases.AdaptadorCarrito;
import com.example.kapricho.cliente.fragments.clases.ItemsCarrito;
import com.example.kapricho.config;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link carrito_cliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class carrito_cliente extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SharedPreferences preferences;
    Toolbar toolbar;
    RecyclerView recyclerView;
    LinearLayout layout;
    AdaptadorCarrito adaptador;
    ItemsCarrito items;
    ArrayList<ItemsCarrito> arrayList;
    Button realizar_pedido;
    TextView total_pagar;
    config server;

    public carrito_cliente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment carrito_cliente.
     */
    // TODO: Rename and change types and number of parameters
    public static carrito_cliente newInstance(String param1, String param2) {
        carrito_cliente fragment = new carrito_cliente();
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
        View vista = inflater.inflate(R.layout.fragment_carrito_cliente, container, false);

        toolbar = vista.findViewById(R.id.toolbar_carrito_cliente);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        recyclerView = vista.findViewById(R.id.recicler_carrito_de_compras);
        layout = vista.findViewById(R.id.vacio_carrito_de_compras);
        total_pagar = vista.findViewById(R.id.text_total_a_pagar);
        realizar_pedido = vista.findViewById(R.id.button_realizar_pedido_carrito);
        arrayList = new ArrayList<>();
        adaptador = new AdaptadorCarrito(arrayList, getActivity());
        server = new config();

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adaptador);

        listarCarrito(server.getServer() + "listarCarrito.php?token=" + preferences.getString("usuario", null),
                getActivity());
        totalCarrito(server.getServer() + "totalCarrito.php?token=" + preferences.getString("usuario", null),
                getActivity());


        realizar_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realizarPedido(server.getServer() + "realizarPedido.php", getActivity());
            }
        });

        return vista;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_vaciar_carrito, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.vaciar_carrito){
            if(arrayList.size() <= 0){
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(getActivity());
                alert.setMessage("Vaya! Parece que tu carrito esta vacío, no puedes vaciar algo que ya esta vacío");
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.setCancelable(false);
                alert.show();
            } else {
                MaterialAlertDialogBuilder alerta = new MaterialAlertDialogBuilder(getActivity());
                alerta.setMessage("¿Esta seguro que desea vaciar su carrito de compras?");
                alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        vaciarCarrito(server.getServer() + "vaciarCarrito.php", getActivity());
                    }
                });
                alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alerta.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void vaciarCarrito(String server_url, Context context) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Vaciando carrito...");
        pd.setCancelable(false);
        pd.show();

        StringRequest string = new StringRequest(Request.Method.POST, server_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("exitoso")){
                    adaptador.notifyDataSetChanged();
                    toolbar.setSubtitle("Productos agregados: " + 0);
                    total_pagar.setText("Total a pagar: $0 COP");
                    realizar_pedido.setEnabled(false);
                    realizar_pedido.setTextColor(Color.parseColor("#787878"));
                    realizar_pedido.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
                    MaterialAlertDialogBuilder alerta = new MaterialAlertDialogBuilder(context);
                    alerta.setMessage("Su carrito de compras se vació correctamente");
                    alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            recyclerView.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            carrito_cliente carrito = new carrito_cliente();
                            FragmentTransaction transaction = ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.container_layout_cliente, carrito);
                            transaction.commit();
                        }
                    });
                    alerta.show();
                } else if (response.equals("error")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Ocurrio un error al intentar vaciar su carrito, por favor intente " +
                            "nuevamente más tarde");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(response);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    builder.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("token", preferences.getString("usuario", null));
                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(string);
    }

    public void realizarPedido(String server_url, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View vista = inflater.inflate(R.layout.edittext_dialog, null);
        TextInputEditText mesa = vista.findViewById(R.id.editText_dialog);
        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
        alert.setView(vista);
        alert.setMessage("Para realizar el pedido indique su número de mesa:");
        alert.setPositiveButton("Realizar pedido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mesa_string = mesa.getText().toString().trim();

                if(mesa_string.equals("")){
                    mesa.setError("Indique un número de mesa");
                    Toast.makeText(context, "Indique un número de mesa", Toast.LENGTH_SHORT).show();
                } else {
                    ProgressDialog pd = new ProgressDialog(context);
                    pd.setMessage("Realizando pedido...");
                    pd.setCancelable(false);
                    pd.show();
                    StringRequest string = new StringRequest(Request.Method.POST, server_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pd.hide();
                            if(response.equals("exitoso")){
                                dialogInterface.dismiss();
                                adaptador.notifyDataSetChanged();
                                toolbar.setSubtitle("Productos agregados: " + 0);
                                total_pagar.setText("Total a pagar: $0 COP");
                                realizar_pedido.setEnabled(false);
                                realizar_pedido.setTextColor(Color.parseColor("#787878"));
                                realizar_pedido.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
                                MaterialAlertDialogBuilder alerta = new MaterialAlertDialogBuilder(context);
                                alerta.setMessage("Su pedido fue enviado y ha sido recibido satisfactoriamente, " +
                                        "puede ver el estado de su pedido en la sección mis pedidos. De igual forma" +
                                        " se le notificará el estado de su pedido");
                                alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        recyclerView.setVisibility(View.GONE);
                                        layout.setVisibility(View.VISIBLE);
                                    }
                                });
                                alerta.show();
                            } else if (response.equals("error")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Ocurrio un error al intentar enviar su pedido, por favor intente " +
                                        "nuevamente más tarde");
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                builder.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(response);
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                builder.show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pd.hide();
                            Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parametros = new HashMap<String, String>();
                            parametros.put("token", preferences.getString("usuario", null));
                            parametros.put("mesa", mesa_string);
                            return parametros;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(string);
                }
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

    public  void listarCarrito(String server_url, Context context){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Cargando carrito...");
        pd.setCancelable(false);
        pd.show();

        JsonArrayRequest array = new JsonArrayRequest(server_url, new Response.Listener<JSONArray>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(JSONArray response) {
                pd.hide();
                recyclerView.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                JSONObject jsonObject;
                for (int i = 0; i < response.length(); i++) {
                    try {

                        jsonObject = response.getJSONObject(i);
                        DecimalFormat formatea = new DecimalFormat("###,###.##");
                        Double precio_unit = Double.parseDouble(jsonObject.getString("p_unitario"));
                        Double precio_total = Double.parseDouble(jsonObject.getString("total_pagar"));

                        items = new ItemsCarrito();

                        items.setId_producto(jsonObject.getString("id_producto"));
                        items.setDescripcion(jsonObject.getString("descripcion"));
                        items.setExistencia(jsonObject.getString("existencia"));
                        items.setPrecio(String.valueOf(formatea.format(precio_unit)));
                        items.setTotal(String.valueOf(formatea.format(precio_total)));
                        items.setCantidad(jsonObject.getString("cantidad_carrito"));
                        items.setImagen(server.getServer() + jsonObject.getString("imagen"));
                        items.setToken(preferences.getString("usuario", null));

                        arrayList.add(items);

                        adaptador.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                toolbar.setSubtitle("Productos agregados: " + arrayList.size());
                realizar_pedido.setEnabled(true);
                realizar_pedido.setTextColor(Color.parseColor("#263238"));
                realizar_pedido.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    recyclerView.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    realizar_pedido.setEnabled(false);
                    realizar_pedido.setTextColor(Color.parseColor("#787878"));
                    realizar_pedido.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
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

    public  void totalCarrito(String server_url, Context context){

        JsonArrayRequest array = new JsonArrayRequest(server_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject;
                if(response.length() > 0){
                    for (int i = 0; i < response.length(); i++) {
                        try {

                            jsonObject = response.getJSONObject(i);
                            DecimalFormat formatea = new DecimalFormat("###,###.##");
                            Double precio_total;
                            if(jsonObject.getString("total_carrito").equals("")){
                                precio_total = 0.0;
                            } else {
                                precio_total = Double.parseDouble(jsonObject.getString("total_carrito"));
                            }

                            total_pagar.setText("Total a pagar: $" + String.valueOf(formatea.format(precio_total)) + " COP");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    toolbar.setSubtitle("Productos agregados: " + arrayList.size());
                } else {
                    toolbar.setSubtitle("Productos agregados: " + 0);
                    total_pagar.setText("Total a pagar: $0 COP");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    total_pagar.setText("Total a pagar: $0 COP");
                } else {
                    Log.i("Error al listar", error.toString());
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }
}