package com.example.kapricho.cliente.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.cliente.fragments.clases.AdaptadorPedidoCliente;
import com.example.kapricho.cliente.fragments.clases.ItemsPedido;
import com.example.kapricho.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link pedidos_cliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pedidos_cliente extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    LinearLayout layout;
    AdaptadorPedidoCliente adaptador;
    ItemsPedido items;
    ArrayList<ItemsPedido> arrayList;
    config server;
    SharedPreferences preferences;

    public pedidos_cliente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment pedidos_cliente.
     */
    // TODO: Rename and change types and number of parameters
    public static pedidos_cliente newInstance(String param1, String param2) {
        pedidos_cliente fragment = new pedidos_cliente();
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
        View vista = inflater.inflate(R.layout.fragment_pedidos_cliente, container, false);

        recyclerView = vista.findViewById(R.id.recicler_pedidos_cliente);
        layout = vista.findViewById(R.id.vacio_pedidos_cliente);
        arrayList = new ArrayList<>();
        adaptador = new AdaptadorPedidoCliente(arrayList, getActivity());
        server = new config();
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adaptador);

        listar(server.getServer() + "listarPedidoCliente.php?id_cliente=" + preferences.getString("id_cliente", null),
                getActivity());

        return vista;
    }

    private void listar(String url_servidor, Context context) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Cargando pedido...");
        pd.show();

        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                pd.hide();
                recyclerView.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
                JSONObject jsonObject;
                try {
                    for (int i = 0; i < response.length(); i++) {
                        jsonObject = response.getJSONObject(i);

                        DecimalFormat formatea = new DecimalFormat("###,###.##");

                        items = new ItemsPedido();

                        items.setId_pedido(jsonObject.getString("id_pedido"));
                        items.setMesa(jsonObject.getString("mesa"));
                        items.setTotal(jsonObject.getString("total"));
                        items.setEstado(jsonObject.getString("estado_cocina"));

                        arrayList.add(items);

                        adaptador.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide();
                recyclerView.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);

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