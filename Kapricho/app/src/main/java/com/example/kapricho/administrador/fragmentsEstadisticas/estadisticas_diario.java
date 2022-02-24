package com.example.kapricho.administrador.fragmentsEstadisticas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.administrador.MainActivity_admin;
import com.example.kapricho.administrador.clases.AdaptadorOrdenes;
import com.example.kapricho.administrador.clases.ItemsOrdenes;
import com.example.kapricho.config;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link estadisticas_diario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class estadisticas_diario extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText fecha, total;
    RecyclerView recyclerView;
    AdaptadorOrdenes adaptadorOrdenes;
    ItemsOrdenes items;
    ArrayList<ItemsOrdenes> arrayList;
    config server;
    TextView tv;

    public estadisticas_diario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment estadisticas_diario.
     */
    // TODO: Rename and change types and number of parameters
    public static estadisticas_diario newInstance(String param1, String param2) {
        estadisticas_diario fragment = new estadisticas_diario();
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
        View vista = inflater.inflate(R.layout.fragment_estadisticas_diario, container, false);

        MaterialDatePicker.Builder date = MaterialDatePicker.Builder.datePicker();
        date.setTitleText("Seleccione una fecha");
        date.setSelection(MaterialDatePicker.todayInUtcMilliseconds());

        final MaterialDatePicker<Long> materialDatePicker = date.build();
        server = new config();
        arrayList = new ArrayList<>();
        adaptadorOrdenes = new AdaptadorOrdenes(arrayList, getActivity());
        fecha = vista.findViewById(R.id.et_fecha_diario);
        recyclerView = vista.findViewById(R.id.recicler_fecha_diario);
        total = vista.findViewById(R.id.et_total_recaudado);
        tv = vista.findViewById(R.id.tv_total_recaudad);

        recyclerView.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adaptadorOrdenes);

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection+86400000);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate  = format.format(calendar.getTime());
                fecha.setText(materialDatePicker.getHeaderText());
                listar(server.getServer()  + "listarEstadisticas.php?fecha=" + formattedDate, getActivity(), formattedDate);
            }
        });
        return vista;
    }

    public void listar(String url_servidor, Context context, String fecha){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Cargando por favor espere...");
        pd.setCancelable(false);
        pd.show();
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    recyclerView.setVisibility(View.VISIBLE);
                    total.setVisibility(View.VISIBLE);
                    tv.setVisibility(View.VISIBLE);
                    pd.hide();

                    JSONObject jsonObject;
                    recaudoDia(server.getServer() + "recaudadoDia.php?fecha=" + fecha);
                    arrayList.clear();

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

                            adaptadorOrdenes.notifyDataSetChanged();

                        } catch (JSONException e){
                            e.printStackTrace();
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
                    total.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage("No se encontraron ventas realizadas en la fecha indicada");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                    pd.hide();
                } else {
                    recyclerView.setVisibility(View.GONE);
                    total.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage("No se encontraron ventas realizadas en la fecha indicada");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                    pd.hide();
                    total.setVisibility(View.GONE);
                    pd.hide();
                    Log.i("Error al listar", error.toString());
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }

    public void recaudoDia(String url){
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Cargando por favor espere...");
        pd.setCancelable(false);
        pd.show();

        JsonArrayRequest array = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    pd.hide();
                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);
                            DecimalFormat formatea = new DecimalFormat("###,###.##");
                            Double precio;
                            if(!jsonObject.getString("recaudo").equals("")){
                                precio = Double.parseDouble(jsonObject.getString("recaudo"));
                            } else {
                                precio = 0.0;
                            }
                            String recaudo = String.valueOf(formatea.format(precio));
                            total.setText(recaudo + " COP");
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setMessage("No se encontró");
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
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setMessage("Error: \n" + error);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(array);

    }
}