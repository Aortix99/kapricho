package com.example.kapricho.cliente.fragments.clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.cliente.fragments.carrito_cliente;
import com.example.kapricho.cliente.fragments.pedidos_cliente;
import com.example.kapricho.config;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link estado_pedido_cliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class estado_pedido_cliente extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Toolbar toolbar;
    TextView total, noPedido, text_devuelto_1, text_devuelto_2, text_recibido, text_preparacion_1, text_preparacion_2,
            text_despacho_1, text_despacho_2;
    CircleImageView circle_recibido, circle_preparacion, circle_despacho;
    ImageView img_recibido, img_preparacion, img_despacho;
    LinearLayout linear_cancel, line_preparacion, line_despacho;
    ScrollView timeline;
    String id_pedido;
    config server;
    String estado;

    public estado_pedido_cliente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment estado_pedido_cliente.
     */
    // TODO: Rename and change types and number of parameters
    public static estado_pedido_cliente newInstance(String param1, String param2) {
        estado_pedido_cliente fragment = new estado_pedido_cliente();
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
        View vista = inflater.inflate(R.layout.fragment_estado_pedido_cliente, container, false);

        toolbar = vista.findViewById(R.id.toolbardetalle_pedido_cliente);
        total = vista.findViewById(R.id.total_pagar_detalle_cliente);
        noPedido = vista.findViewById(R.id.noPedido_detalle_cliente);
        text_devuelto_1 = vista.findViewById(R.id.text_devuelto_1);
        text_devuelto_2 = vista.findViewById(R.id.text_devuelto_2);
        text_recibido = vista.findViewById(R.id.text_recibido);
        text_preparacion_1 = vista.findViewById(R.id.text_preparacion_1);
        text_preparacion_2 = vista.findViewById(R.id.text_preparacion_2);
        text_despacho_1 = vista.findViewById(R.id.text_despacho_1);
        text_despacho_2 = vista.findViewById(R.id.text_despacho_2);
        circle_recibido = vista.findViewById(R.id.dot_recibido);
        circle_preparacion = vista.findViewById(R.id.dot_preparacion);
        circle_despacho = vista.findViewById(R.id.dot_despacho);
        img_recibido = vista.findViewById(R.id.img_recibido);
        img_preparacion = vista.findViewById(R.id.img_preparacion);
        img_despacho = vista.findViewById(R.id.img_despacho);
        linear_cancel = vista.findViewById(R.id.linearLayout_cancel);
        line_preparacion = vista.findViewById(R.id.line_preparacion);
        line_despacho = vista.findViewById(R.id.line_despacho);
        timeline = vista.findViewById(R.id.scrollView_timelapse);
        id_pedido = AdaptadorPedidoCliente.id_pedido;
        String total_string = AdaptadorPedidoCliente.total;
        estado = AdaptadorPedidoCliente.estado;
        server = new config();

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

        DecimalFormat formatea = new DecimalFormat("###,###.##");

        noPedido.setText(id_pedido);
        total.setText("$" + String.valueOf(formatea.format(Double.parseDouble(total_string))) + " COP");

        switch (estado){
            case "Recibido":
                timeline.setVisibility(View.VISIBLE);
                linear_cancel.setVisibility(View.GONE);

                circle_recibido.setBackgroundResource(R.drawable.circle_active);
                img_recibido.setBackgroundResource(R.drawable.circle_active);
                text_recibido.setTextColor(Color.parseColor("#263238"));

                line_preparacion.setBackgroundColor(Color.parseColor("#B3B2B2"));

                circle_preparacion.setBackgroundResource(R.drawable.circle_unabled);
                circle_preparacion.setBackgroundResource(R.drawable.circle_unabled);
                text_preparacion_1.setTextColor(Color.parseColor("#B3B2B2"));
                text_preparacion_2.setTextColor(Color.parseColor("#B3B2B2"));

                line_despacho.setBackgroundColor(Color.parseColor("#B3B2B2"));

                circle_despacho.setBackgroundResource(R.drawable.circle_unabled);
                img_despacho.setBackgroundResource(R.drawable.circle_unabled);
                text_despacho_1.setTextColor(Color.parseColor("#B3B2B2"));
                text_despacho_2.setTextColor(Color.parseColor("#B3B2B2"));
                break;
            case "En preparacion":
                timeline.setVisibility(View.VISIBLE);
                linear_cancel.setVisibility(View.GONE);

                circle_recibido.setBackgroundResource(R.drawable.circle_passed);
                img_recibido.setBackgroundResource(R.drawable.circle_passed);
                text_recibido.setTextColor(Color.parseColor("#008832"));

                line_preparacion.setBackgroundColor(Color.parseColor("#008832"));

                circle_preparacion.setBackgroundResource(R.drawable.circle_active);
                img_preparacion.setBackgroundResource(R.drawable.circle_active);
                text_preparacion_1.setTextColor(Color.parseColor("#263238"));
                text_preparacion_2.setTextColor(Color.parseColor("#263238"));

                line_despacho.setBackgroundColor(Color.parseColor("#B3B2B2"));

                circle_despacho.setBackgroundResource(R.drawable.circle_unabled);
                img_despacho.setBackgroundResource(R.drawable.circle_unabled);
                text_despacho_1.setTextColor(Color.parseColor("#B3B2B2"));
                text_despacho_2.setTextColor(Color.parseColor("#B3B2B2"));
                break;
            case "Entregado":
                timeline.setVisibility(View.VISIBLE);
                linear_cancel.setVisibility(View.GONE);

                circle_recibido.setBackgroundResource(R.drawable.circle_passed);
                img_recibido.setBackgroundResource(R.drawable.circle_passed);
                text_recibido.setTextColor(Color.parseColor("#008832"));

                line_preparacion.setBackgroundColor(Color.parseColor("#008832"));

                circle_preparacion.setBackgroundResource(R.drawable.circle_passed);
                img_preparacion.setBackgroundResource(R.drawable.circle_passed);
                text_preparacion_1.setTextColor(Color.parseColor("#008832"));
                text_preparacion_2.setTextColor(Color.parseColor("#008832"));

                line_despacho.setBackgroundColor(Color.parseColor("#008832"));

                circle_despacho.setBackgroundResource(R.drawable.circle_passed);
                img_despacho.setBackgroundResource(R.drawable.circle_passed);
                text_despacho_1.setTextColor(Color.parseColor("#008832"));
                text_despacho_2.setTextColor(Color.parseColor("#008832"));
                break;
            case "Devuelto":
                timeline.setVisibility(View.GONE);
                linear_cancel.setVisibility(View.VISIBLE);
                text_devuelto_1.setText("Pedido devuelto");
                text_devuelto_2.setText("Su pedido fue devuelto o anulado");
                break;
            case "Cancelado":
                timeline.setVisibility(View.GONE);
                linear_cancel.setVisibility(View.VISIBLE);
                text_devuelto_1.setText("Pedido cancelado");
                text_devuelto_2.setText("Cancelaste tu pedido");
                break;
        }

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
                        cancelarPedido(server.getServer() + "actualizarPedido.php", id_pedido, getActivity());
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
}