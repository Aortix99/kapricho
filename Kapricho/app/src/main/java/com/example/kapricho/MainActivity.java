package com.example.kapricho;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.administrador.MainActivity_admin;
import com.example.kapricho.cliente.MainActivity_cliente;
import com.example.kapricho.cocina.MainActivity_cocina;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class MainActivity extends AppCompatActivity {
    EditText usuario, clave;
    Button login, register, recuperar;
    Window window;
    config ip;
    ConstraintLayout vista;
    SharedPreferences preferencias;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = (EditText) findViewById(R.id.username_login);
        clave = (EditText) findViewById(R.id.password_login);
        login = (Button) findViewById(R.id.btn_iniciar_sesion_main);
        register = (Button) findViewById(R.id.btn_registrar_main);
        recuperar = (Button) findViewById(R.id.btn_recuperar_clave);
        ip = new config();
        vista = (ConstraintLayout) findViewById(R.id.mainView);
        preferencias = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Validando usuario");
        pd.setCancelable(false);

        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));

        session();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(conexion()){
                    int vacio = 0;

                    vacio = comprobarCampos(usuario, "Debe ingresar el usuario");
                    vacio = comprobarCampos(clave, "Debe ingresar la contraseña");

                    if(vacio == 0) {
                        pd.show();
                        login( ip.getServer() + "login.php?usuario=" + usuario.getText().toString().trim() +
                                "&clave=" + clave.getText().toString().trim());
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("Oops, parece que no estás conectado a internet, por favor verifica tu conexión para continuar");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    });
                    alert.show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MainActivity.this, registro_cliente.class);
                startActivity(in);
                finish();
            }
        });
        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View vista = inflater.inflate(R.layout.edittext_dialog, null);
                TextInputEditText correo = vista.findViewById(R.id.editText_dialog);
                TextInputLayout cont = vista.findViewById(R.id.editText_dialog_container);
                cont.setHint("Correo electrónico");
                correo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(MainActivity.this);
                alert.setView(vista);
                alert.setTitle("Olvidaste tu contraseña");
                alert.setMessage("Para recuperar tu contraseña o sus datos de acceso ingrese los siguientes datos");
                alert.setPositiveButton("Recuperar contraseña", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(correo.getText().toString().trim().equals("")){
                            correo.setError("Ingrese un correo electrónico");
                            Toast.makeText(MainActivity.this, "Debe ingresar un correo electrónico",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            dialogInterface.dismiss();
                            ProgressDialog pd = new ProgressDialog(MainActivity.this);
                            pd.setCancelable(false);
                            pd.setMessage("Validando usuario");
                            pd.show();
                            StringRequest string = new StringRequest(Request.Method.POST, ip.getServer() + "recuperarClave.php",
                                    new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    pd.hide();
                                    if(response.contains("existe")){
                                        String[] datos = response.split(";");
                                        String user_res = datos[1];
                                        String clave_res = datos[2];
                                        recuperarClave(correo.getText().toString().trim(),"<table style='width: 500px; margin: auto; padding: 15px 20px; background: #efefef; border-radius: 10px;'>" +
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
                                                "        <p style='margin: 5px 0px; padding:0; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Recuperación de datos de acceso</b><br></p>" +
                                                "        <p style='margin: 5px 0px; padding:0; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Hemos recibido un solicitud para recuperar tus datos de acceso, a continuación se muestra toda su información. <br><br></p>" +
                                                "        <p style='margin: 5px 0px; padding:0; margin-bottom: 5px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Usuario:</b> " + user_res + "</p>" +
                                                "        <p style='margin: 5px 0px; padding:0; margin-bottom: 5px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'><b>Contraseña:</b> " + clave_res + "</p>" +
                                                "      </td>" +
                                                "    </tr>" +
                                                "  </tbody>" +
                                                "</table>" +
                                                "      </td>" +
                                                "    </tr>" +
                                                "  </tbody>" +
                                                "</table>" +
                                                "<p style='margin-top: 10px; text-align: center; font-size: 13px; font-family: adobe-clean,Helvetica Neue,Helvetica,Verdana,Arial,sans-serif;'>Este mensaje es generado automáticamente, por favor no responder. <br> Todos los derechos reservados Restaurante Kapricho S.A. <br> Si este mensaje le llegó por error haga caso omiso</p>",  MainActivity.this);
                                    } else if(response.equals("error usuario")){
                                        Toast.makeText(MainActivity.this, "El correo electrónico no se encuentra " +
                                                        "registrado",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        AlertDialog.Builder build = new AlertDialog.Builder(MainActivity.this);
                                        build.setMessage(response);
                                        build.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    pd.hide();
                                    Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                                }
                            }){
                                @Nullable
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> parametros = new HashMap<String, String>();
                                    parametros.put("correo", correo.getText().toString().trim());
                                    return parametros;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                            queue.add(string);
                        }
                    }
                });
                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alert.show();
            }
        });
    }

    public void recuperarClave(String correo, String mensaje, Context context){
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
            alert.setMessage("Se envió un correo electronico con los datos de acceso, si no lo encuentra en la bandeja" +
                    " principal revise la carpeta de SPAM o correo no deseado");
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

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

    public boolean conexion(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void login(String url_servidor){
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);
                            String rol = jsonObject.getString("rol");

                            SharedPreferences.Editor editor = preferencias.edit();
                            editor.putString("usuario", usuario.getText().toString().trim());
                            editor.putString("clave", clave.getText().toString().trim());
                            editor.putString("rol", rol);
                            editor.commit();

                            switch (rol){
                                case "Administrador":
                                    Intent admin = new Intent(MainActivity.this, MainActivity_admin.class);
                                    startActivity(admin);
                                    finish();
                                    break;
                                case "Cocina":
                                    Intent cocina = new Intent(MainActivity.this, MainActivity_cocina.class);
                                    startActivity(cocina);
                                    finish();
                                    break;
                                case "Cliente":
                                    editor.putString("id_cliente", jsonObject.getString("Id_cliente"));
                                    editor.commit();
                                    Intent cliente = new Intent(MainActivity.this, MainActivity_cliente.class);
                                    startActivity(cliente);
                                    finish();
                                    break;
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("Usuario o contraseña incorrectas, por favor verifique sus datos");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            recuperar.setVisibility(View.VISIBLE);
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide();
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("Usuario o contraseña incorrectas, por favor verifique sus datos");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            recuperar.setVisibility(View.VISIBLE);
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setMessage("Error: \n" + error);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                }
            }
        });
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(array);
    }

    public void session(){

        String usuario = preferencias.getString("usuario", null);
        String clave = preferencias.getString("clave", null);
        String rol= preferencias.getString("rol", null);

        if(usuario != null && clave != null && rol != null){
            switch (rol){
                case "Administrador":
                    Intent admin = new Intent(MainActivity.this, MainActivity_admin.class);
                    startActivity(admin);
                    finish();
                    break;
                case "Cocina":
                    Intent cocina = new Intent(MainActivity.this, MainActivity_cocina.class);
                    startActivity(cocina);
                    finish();
                    break;
                case "Cliente":
                    Intent cliente = new Intent(MainActivity.this, MainActivity_cliente.class);
                    startActivity(cliente);
                    finish();
                    break;
            }
        }
    }

    // Verficando conexión

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onStop() {
        super.onStop();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            onNetworkChange(ni);
        }
    };

    private void onNetworkChange(NetworkInfo networkInfo) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            Snackbar.make(vista, "Conectado a internet", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor("#ffffff"))
                    .setTextColor(Color.parseColor("#000a12"))
                    .show();
        } else {
            Snackbar.make(vista, "No estás conectado a internet", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Aceptar", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setActionTextColor(Color.parseColor("#000a12"))
                    .setBackgroundTint(Color.parseColor("#ffffff"))
                    .setTextColor(Color.parseColor("#000a12"))
                    .show();
        }
    }
    public int comprobarCampos(EditText campo , String error){
        int bandera = 0;
        if(campo.getText().toString().trim().equals("")){
            bandera++;
            campo.setError(error);
        }
        return bandera;
    }

}