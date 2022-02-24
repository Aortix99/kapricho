package com.example.kapricho.administrador;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import com.example.kapricho.MainActivity;
import com.example.kapricho.R;
import com.example.kapricho.config;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity_admin extends AppCompatActivity {
    Window window;
    Toolbar toolbar;
    TextView tv_date_dia, tv_date_mes, tv_recaudado_dia, tv_recaudado_mes;
    CardView menu_usuarios, menu_productos, menu_ordenes, menu_logout, menu_estadisticas;
    SharedPreferences preferences;
    ConstraintLayout vista;
    config ip;
    ProgressDialog pd;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));

        vista = (ConstraintLayout) findViewById(R.id.vista_admin);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main_admin);
        tv_date_dia = (TextView) findViewById(R.id.tv_date_dia);
        tv_date_mes = (TextView) findViewById(R.id.tv_date_mes);
        tv_recaudado_dia = (TextView) findViewById(R.id.tv_recaudado_dia);
        tv_recaudado_mes = (TextView) findViewById(R.id.tv_recaudado_mes);
        menu_usuarios = (CardView) findViewById(R.id.menu_users_main_adm);
        menu_productos = (CardView) findViewById(R.id.menu_products_main_adm);
        menu_ordenes = (CardView) findViewById(R.id.menu_orders_main_adm);
        menu_logout = (CardView) findViewById(R.id.menu_logout_main_adm);
        menu_estadisticas = (CardView) findViewById(R.id.menu_estadisticas_main_adm);
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity_admin.this);
        ip = new config();
        pd = new ProgressDialog(MainActivity_admin.this);
        pd.setCancelable(false);

        obtenerNombre(ip.getServer() + "obtenerUsuarioAdmin.php?usuario=" + preferences.getString("usuario", null));

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaConsulta = sdf.format(c.getTime());

        recaudoDia(ip.getServer() + "recaudadoDia.php?fecha=" + fechaConsulta);

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
        String fechaConsulta2 = sdf2.format(c.getTime());

        recaudoMes(ip.getServer() + "recaudadoMes.php?fecha=" + fechaConsulta2);

        menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        menu_usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarActivity(usuarios_admin.class);
            }
        });
        menu_productos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarActivity(productos_admin.class);
            }
        });
        menu_ordenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarActivity(ordenes_admin.class);
            }
        });
        menu_estadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarActivity(estadisticas_admin.class);
            }
        });
    }

    public void cambiarActivity(Class activity){
        Intent i = new Intent(MainActivity_admin.this, activity);
        startActivity(i);
        finish();
    }

    public void logout(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity_admin.this);
        alerta.setMessage("Esta seguro que desea cerrar la sesión?");
        alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("usuario");
                editor.remove("clave");
                editor.remove("rol");
                editor.apply();
                cambiarActivity(MainActivity.class);
            }
        });
        alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alerta.show();
    }

    public void obtenerNombre(String url){
        JsonArrayRequest array = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    pd.hide();
                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);
                            String nombre = jsonObject.getString("nombre");
                            String apellido = jsonObject.getString("apellido");
                            toolbar.setTitle(nombre + " " + apellido);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_admin.this);
                    alert.setMessage("No se encontró el usuario");
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
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_admin.this);
                    alert.setMessage("Error al encontrar usuario");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            logout();
                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_admin.this);
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
        RequestQueue queue = Volley.newRequestQueue(MainActivity_admin.this);
        queue.add(array);
    }

    public String recaudoDia(String url){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(c.getTime());

        tv_date_dia.setText(strDate);

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
                            tv_recaudado_dia.setText("$" + recaudo + " COP");
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_admin.this);
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
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_admin.this);
                    alert.setMessage("Error al encontrar fecha");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_admin.this);
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
        RequestQueue queue = Volley.newRequestQueue(MainActivity_admin.this);
        queue.add(array);

        String recaudo = "";
        return recaudo;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String recaudoMes(String url){
        long ahora = System.currentTimeMillis();
        Date fecha = new Date(ahora);
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(ahora);
        int year = calendario.get(Calendar.YEAR);
        Month mes = LocalDate.now().getMonth();
        String nombre = mes.getDisplayName(TextStyle.FULL, new Locale("es", "CO"));
        String primeraLetra = nombre.substring(0,1);
        String mayuscula = primeraLetra.toUpperCase();
        String demasLetras = nombre.substring(1, nombre.length());
        nombre = mayuscula + demasLetras;

        tv_date_mes.setText(nombre + " "+ year);

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
                            tv_recaudado_mes.setText("$" + recaudo + " COP");
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_admin.this);
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
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_admin.this);
                    alert.setMessage("Error al encontrar fecha");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.setCancelable(false);
                    alert.show();
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_admin.this);
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
        RequestQueue queue = Volley.newRequestQueue(MainActivity_admin.this);
        queue.add(array);

        String recaudo = "";
        return recaudo;
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
}