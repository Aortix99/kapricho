package com.example.kapricho.cocina.clases;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.administrador.MainActivity_admin;
import com.example.kapricho.administrador.clases.ItemFactura;
import com.example.kapricho.administrador.clases.factura;
import com.example.kapricho.cocina.pedidos_cocina;
import com.example.kapricho.config;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class gestion_pedidos extends AppCompatActivity {
    RecyclerView recyclerView;
    Button estado;
    Toolbar toolbar;
    ArrayList<ItemGestionPedido> arrayList;
    AdaptadorGestionPedido adaptador;
    ItemGestionPedido items;
    Window window;
    config server;
    String venta;
    String correo;
    String mesa;
    String nombre;
    String total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_pedidos);
        recyclerView = (RecyclerView) findViewById(R.id.detalle_del_pedido_cocina);
        estado = (Button) findViewById(R.id.estado_pedido_btn);
        toolbar = (Toolbar) findViewById(R.id.toolbar_detalle_gestion_pedido);
        arrayList = new ArrayList<>();
        adaptador = new AdaptadorGestionPedido(arrayList);
        window = getWindow();
        server = new config();

        window.setStatusBarColor(Color.parseColor("#000a12"));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        venta = getIntent().getStringExtra("id_pedido");
        String estatus = getIntent().getStringExtra("estatus");
        nombre = getIntent().getStringExtra("nombre_cliente");
        correo = getIntent().getStringExtra("correo");
        mesa = getIntent().getStringExtra("mesa");
        total = getIntent().getStringExtra("total");

        toolbar.setTitle("Pedido No: " + venta);
        toolbar.setSubtitle("Estado: " + estatus);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listarPedido(server.getServer() + "detalleFactura.php?id_pedido=" + venta, this);

        if(estatus.equals("Recibido")){
            estado.setText("Empezar preparación");
        } else if(estatus.equals("En preparacion")){
            estado.setText("Entregar pedido");
        } else if(estatus.equals("Entregado")){
            estado.setText("Se entregó el pedido");
            estado.setEnabled(false);
            estado.setTextColor(Color.parseColor("#787878"));
            estado.setBackgroundColor(Color.parseColor("#E0E0E0"));
        } if(estatus.equals("Devuelto")){
            estado.setText("Pedido devuelto");
            estado.setEnabled(false);
            estado.setTextColor(Color.parseColor("#787878"));
            estado.setBackgroundColor(Color.parseColor("#E0E0E0"));
        }

        estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(estado.getText().toString().equalsIgnoreCase("Entregar pedido")){
                    actualizarPedidoEntrega(server.getServer() + "actualizarPedido.php", gestion_pedidos.this,
                            venta);
                } else if(estado.getText().toString().equalsIgnoreCase("Empezar preparación")){
                    actualizarPedido(server.getServer() + "actualizarPedido.php", gestion_pedidos.this,
                            venta);
                }
            }
        });
    }

    public void enviarPreparacion(String correo, String mensaje, Context context) {
        String to = correo;

        String from = "resturante.kapricho.no.reply@gmail.com";
        final String password = "kapricho123";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        try {
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            message.setSubject("Restaurante Kapricho - Notificación pedido en preparación");
            message.setContent(mensaje, "text/html; charset=utf-8");

            Transport.send(message);

            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
            alert.setTitle("Notificación enviada");
            alert.setMessage("Se notificó que el pedido está en preparación");
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    toolbar.setSubtitle("Estado: En preparación");
                    estado.setText("Entregar pedido");
                }
            });
            alert.setCancelable(false);
            alert.show();

        } catch (MessagingException e) {
            e.printStackTrace();
            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
            alert.setTitle("Error");
            alert.setMessage(e.toString());
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            });
            alert.setCancelable(false);
            alert.show();
            throw new RuntimeException(e);
        }
    }

    public void entregarPedido(String correo, String mensaje, Context context) {
        String to = correo;

        String from = "resturante.kapricho.no.reply@gmail.com";
        final String password = "kapricho123";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        try {
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            message.setSubject("Restaurante Kapricho - Notificación pedido entregado");
            message.setContent(mensaje, "text/html; charset=utf-8");

            Transport.send(message);

            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
            alert.setTitle("Notificación enviada");
            alert.setMessage("Se notificó la entrega del pedido");
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            });
            alert.setCancelable(false);
            alert.show();

        } catch (MessagingException e) {
            e.printStackTrace();
            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
            alert.setTitle("Error");
            alert.setMessage(e.toString());
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onBackPressed();
                }
            });
            alert.setCancelable(false);
            alert.show();
            throw new RuntimeException(e);
        }

    }

    public void onBackPressed(){
        Intent i = new Intent(this, pedidos_cocina.class);
        startActivity(i);
        finish();
    }

    public void listarPedido(String url_servidor, Context context) {
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);

                            items = new ItemGestionPedido();

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

    public void actualizarPedido(String url_servidor, Context context, String id_pedido){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Cargando por favor espere...");
        pd.setCancelable(false);
        pd.show();
        StringRequest string = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Actualizacion exitosa")){
                    pd.hide();
                    Toast.makeText(context, "Enviando notificación", Toast.LENGTH_LONG).show();
                    enviarPreparacion(correo, "<table style='width: 500px; margin: auto; padding: 15px 20px; background: #efefef; border-radius: 10px;'>" +
                            "  <tbody>" +
                            "    <tr>" +
                            "      <td>" +
                            "        <table style='width: 100%; margin: auto;'>" +
                            "  <tbody>" +
                            "    <tr>" +
                            "      <td style='display: flex; align-items: center; padding: 5px 15px; padding-bottom: 20px; font-size: 18px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>" +
                            "        <img src='https://cdn-icons-png.flaticon.com/512/45/45605.png' alt='logo' width='50px'> &nbsp; <b>Restaurante Kapricho</b>" +
                            "      </td>" +
                            "    </tr>" +
                            "    <tr>" +
                            "      <td style='padding: 15px 20px; font-size: 17px; border-left: 5px solid #008832; background: #fefefe; border-top-left-radius: 5px; border-bottom-left-radius: 5px;'>" +
                            "        <p style='margin: 5px 0px; padding:0; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>¡Tu pedido ya se encuentra en preparación!</p>" +
                            "        <p style='margin: 5px 0px; padding:0; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>En breve será llevado a su mesa</p>" +
                            "      </td>" +
                            "    </tr>" +
                            "    <tr>" +
                            "      <td><br></td>" +
                            "    </tr>" +
                            "    <tr>" +
                            "      <td style='padding: 10px 20px; font-size: 17px; border-left: 5px solid #008832; background: #fefefe; border-bottom-left-radius: 5px; border-top-left-radius: 5px;' colspan='2'>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-bottom: 20px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Datos de tu pedido:</p>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-bottom: 5px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Orden No:</b> " + venta + "</p>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-bottom: 5px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Mesa:</b> " + mesa + "</p>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-bottom: 5px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Pedido a nombre de:</b> " + nombre + "</p>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-top: 30px; text-align: center; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Puedes ver el estado de tu pedido y más detalles en nuestra app móvil, dando click en la opción mis pedidos y seleccionando el pedido.</p>" +
                            "        <p>" +
                            "          <img src='https://cdni.iconscout.com/illustration/premium/thumb/preparing-breakfast-3760557-3136043.png' alt='logo' width='250px' style='display: block; margin: auto;'>" +
                            "        </p>" +
                            "      </td>" +
                            "    </tr>" +
                            "  </tbody>" +
                            "</table>" +
                            "      </td>" +
                            "    </tr>" +
                            "  </tbody>" +
                            "</table>" +
                            "<p style='margin-top: 10px; text-align: center; font-size: 13px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Este mensaje es generado automáticamente, por favor no responder. <br> Todos los derechos reservados Restaurante Kapricho S.A. <br> Si este mensaje le llegó por error haga caso omiso</p>", gestion_pedidos.this);
                } else if(response.equals("Error al actualizar")){
                    pd.hide();
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage("Ocurrio un error al intentar notificar que el pedido esta en preparación," +
                            " por favor intente nuevamente más tarde");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                } else {
                    pd.hide();
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
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
                Toast.makeText(context, "Error al actualizar pedido", Toast.LENGTH_SHORT).show();
                Log.i("Error actualizar pedido", error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_pedido", id_pedido);
                parametros.put("estado", "En preparación");
                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(string);
    }

    public void actualizarPedidoEntrega(String url_servidor, Context context, String id_pedido){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Cargando por favor espere...");
        pd.setCancelable(false);
        pd.show();
        StringRequest string = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Actualizacion exitosa")){
                    DecimalFormat formatea = new DecimalFormat("###,###.##");
                    Double precio = Double.parseDouble(total);
                    String recaudo = String.valueOf(formatea.format(precio));
                    pd.hide();
                    Toast.makeText(context, "Enviando notificación", Toast.LENGTH_LONG).show();
                    entregarPedido(correo, "<table style='width: 500px; margin: auto; padding: 15px 20px; background: #efefef; border-radius: 10px;'>" +
                            "  <tbody>" +
                            "    <tr>" +
                            "      <td>" +
                            "        <table style='width: 100%; margin: auto;'>" +
                            "  <tbody>" +
                            "    <tr>" +
                            "      <td style='display: flex; align-items: center; padding: 5px 15px; padding-bottom: 20px; font-size: 18px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>" +
                            "        <img src='https://cdn-icons-png.flaticon.com/512/45/45605.png' alt='logo' width='50px'> &nbsp; <b>Restaurante Kapricho</b>" +
                            "      </td>" +
                            "    </tr>" +
                            "    <tr>" +
                            "      <td style='padding: 15px 20px; font-size: 17px; border-left: 5px solid #008832; background: #fefefe; border-top-left-radius: 5px; border-bottom-left-radius: 5px;'>" +
                            "        <p style='margin: 5px 0px; padding:0; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>¡Tu pedido ya fue entregado!</p>" +
                            "        <p style='margin: 5px 0px; padding:0; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Esperamos que disfrutes de tu comida, fue un gusto servirte</p>" +
                            "      </td>" +
                            "    </tr>" +
                            "    <tr>" +
                            "      <td><br></td>" +
                            "    </tr>" +
                            "    <tr>" +
                            "      <td style='padding: 10px 20px; font-size: 17px; border-left: 5px solid #008832; background: #fefefe; border-bottom-left-radius: 5px; border-top-left-radius: 5px;' colspan='2'>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-bottom: 20px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Datos de tu pedido:</p>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-bottom: 5px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Orden No:</b> " + venta + "</p>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-bottom: 5px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Mesa:</b> " + mesa + "</p>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-bottom: 5px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Pedido a nombre de:</b> " + nombre + "</p>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-bottom: 5px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Total a pagar:</b> $" + recaudo + " COP</p>" +
                            "        <p style='margin: 5px 0px; padding:0; margin-top: 30px; text-align: center; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Puedes ver el estado de tu pedido y más detalles en nuestra app móvil, dando click en la opción mis pedidos y seleccionando el pedido.</p>" +
                            "        <p>" +
                            "          <img src='https://cdn.iconscout.com/icon/free/png-256/check-verified-successful-accept-tick-yes-success-2516.png' alt='logo' width='97px' style='display: block; margin: auto;'>" +
                            "        </p>" +
                            "      </td>" +
                            "    </tr>" +
                            "  </tbody>" +
                            "</table>" +
                            "      </td>" +
                            "    </tr>" +
                            "  </tbody>" +
                            "</table>" +
                            "<p style='margin-top: 10px; text-align: center; font-size: 13px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Este mensaje es generado automáticamente, por favor no responder. <br> Todos los derechos reservados Restaurante Kapricho S.A. <br> Si este mensaje le llegó por error haga caso omiso</p>", gestion_pedidos.this);
                } else if(response.equals("Error al actualizar")){
                    pd.hide();
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage("Ocurrio un error al intentar notificar que el pedido esta en preparación," +
                            " por favor intente nuevamente más tarde");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                } else {
                    pd.hide();
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
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
                Toast.makeText(context, "Error al actualizar pedido", Toast.LENGTH_SHORT).show();
                Log.i("Error actualizar pedido", error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_pedido", id_pedido);
                parametros.put("estado", "Entregado");
                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(string);
    }
}