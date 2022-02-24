package com.example.kapricho.cliente.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.administrador.agregar_usuario_admin;
import com.example.kapricho.cliente.fragments.clases.AdaptadorHome;
import com.example.kapricho.cliente.fragments.clases.ItemsHome;
import com.example.kapricho.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_cliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_cliente extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    Spinner filtro;
    Toolbar toolbar;
    config server;
    ArrayList<ItemsHome> arrayList;
    AdaptadorHome adaptador;
    ItemsHome items;
    SharedPreferences preferences;

    public home_cliente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_cliente.
     */
    // TODO: Rename and change types and number of parameters
    public static home_cliente newInstance(String param1, String param2) {
        home_cliente fragment = new home_cliente();
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
        View vista = inflater.inflate(R.layout.fragment_home_cliente, container, false);
        recyclerView = vista.findViewById(R.id.recicler_lista_home);
        filtro = vista.findViewById(R.id.filtro_categoria_producto);
        toolbar = vista.findViewById(R.id.toolbar_home_cliente);
        server = new config();
        arrayList = new ArrayList<>();
        adaptador = new AdaptadorHome(arrayList, getActivity());
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL,
                false));
        recyclerView.setAdapter(adaptador);

        String items[] = new String[]{"Mostrar todos", "Desayunos", "Almuerzos", "Adiciones"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.drowpdown_style, items);
        adapter.setDropDownViewResource(R.layout.checked_dropdown);
        filtro.setAdapter(adapter);



        filtro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    listarProductos(server.getServer() + "productos.php?categoria=todos", getActivity());
                } else if(i == 1){
                    listarProductos(server.getServer() + "productos.php?categoria=Desayuno", getActivity());
                } else if(i == 2){
                    listarProductos(server.getServer() + "productos.php?categoria=Almuerzo", getActivity());
                } else if(i == 3){
                    listarProductos(server.getServer() + "productos.php?categoria=Adicion", getActivity());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setHasOptionsMenu(true);

        return vista;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_carrito, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.ver_carrito_compras){
            carrito_cliente carrito = new carrito_cliente();
            FragmentTransaction transaction = ((AppCompatActivity) getActivity()).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container_layout_cliente, carrito);
            transaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }

    public void listarProductos(String url_servidor, Context context){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Cargando productos...");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                pd.hide();
                JSONObject jsonObject;
                arrayList.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);

                        DecimalFormat formatea = new DecimalFormat("###,###.##");
                        Double precio = Double.parseDouble(jsonObject.getString("precio"));

                        items = new ItemsHome();
                        items.setDescripcion(jsonObject.getString("descripcion"));
                        items.setId(jsonObject.getString("id_producto"));
                        items.setDisponibilidad(jsonObject.getString("existencia"));
                        items.setImagen(server.getServer() + jsonObject.getString("imagen"));
                        items.setPrecio(formatea.format(precio));
                        items.setId_cliente(preferences.getString("usuario", null));

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

            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }
}