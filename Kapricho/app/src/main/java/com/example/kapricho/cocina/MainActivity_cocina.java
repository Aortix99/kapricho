package com.example.kapricho.cocina;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
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
import com.example.kapricho.MainActivity;
import com.example.kapricho.R;
import com.example.kapricho.administrador.MainActivity_admin;
import com.example.kapricho.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity_cocina extends AppCompatActivity {
    Window window;
    Toolbar toolbar;
    CardView menu_usuarios, menu_pedidos, menu_logout;
    TextView noPedidos, id_usuario;
    SharedPreferences preferences;
    config server;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cocina);

        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));
        toolbar = (Toolbar) findViewById(R.id.toolbar_main_cocina);
        menu_usuarios = (CardView) findViewById(R.id.menu_edit_profle_cocina);
        menu_pedidos = (CardView) findViewById(R.id.menu_pedidos_main_cocina);
        menu_logout = (CardView) findViewById(R.id.menu_logout_main_cocina);
        noPedidos = (TextView) findViewById(R.id.textView_cantidad_pedidos);
        id_usuario = (TextView) findViewById(R.id.textView_id_usuario_cocina);
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity_cocina.this);
        server = new config();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        menu_usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity_cocina.this, perfil_cocina.class);
                i.putExtra("id_usuario", id_usuario.getText().toString().trim());
                startActivity(i);
                finish();

            }
        });
        menu_pedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarActivity(pedidos_cocina.class);
            }
        });

        obtenerNombre(server.getServer() + "obtenerUsuarioAdmin.php?usuario=" + preferences.getString("usuario", null));
        cantidadPedidos(server.getServer() + "cantidadPedidos.php", MainActivity_cocina.this);
    }

    public void cantidadPedidos(String url_servidor, Context context){
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    JSONObject jsonObject;

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            noPedidos.setText(" " + jsonObject.getString("pedidos") + " ");
                        } catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(context, "catch: " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(MainActivity_cocina.this);
        queue.add(array);
    }

    public void obtenerNombre(String url){
        JsonArrayRequest array = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);
                            String nombre = jsonObject.getString("nombre");
                            String apellido = jsonObject.getString("apellido");
                            toolbar.setTitle(nombre + " " + apellido);
                            id_usuario.setText(jsonObject.getString("id_usuario"));
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_cocina.this);
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
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_cocina.this);
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
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity_cocina.this);
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
        RequestQueue queue = Volley.newRequestQueue(MainActivity_cocina.this);
        queue.add(array);
    }

    public void logout(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity_cocina.this);
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

    public void cambiarActivity(Class activity){
        Intent i = new Intent(MainActivity_cocina.this, activity);
        startActivity(i);
        finish();
    }
}