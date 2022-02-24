package com.example.kapricho.cocina;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.example.kapricho.R;
import com.example.kapricho.administrador.clases.editar_usuarios_admin;
import com.example.kapricho.administrador.usuarios_admin;
import com.example.kapricho.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class perfil_cocina extends AppCompatActivity {
    Toolbar toolbar;
    EditText nombre, apellido, usuario, clave;
    CheckBox mostrar;
    config server;
    Button actualizar;
    ProgressDialog pd;
    Window window;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_cocina);
        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_users_profile_cocina);
        nombre = (EditText) findViewById(R.id.nombre_edit_profle_cocina);
        usuario = (EditText) findViewById(R.id.username_edit_profle_cocina);
        clave = (EditText) findViewById(R.id.clave_edit_profle_cocina);
        nombre = (EditText) findViewById(R.id.nombre_edit_profle_cocina);
        mostrar = (CheckBox) findViewById(R.id.checkBoxVerCocina) ;
        actualizar = (Button) findViewById(R.id.button_actualizar_cocina);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        apellido = (EditText) findViewById(R.id.apellido_edit_profle_cocina);
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        server = new config();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String id_usuario = getIntent().getStringExtra("id_usuario");
        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));

        listar(server.getServer() + "listarId.php?id=" + id_usuario, perfil_cocina.this);

        mostrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    mostrar.setButtonDrawable(R.drawable.visible_off);
                    clave.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mostrar.setButtonDrawable(R.drawable.visible_on);
                    clave.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vacio = 0;
                vacio = comprobarCampos(usuario, "Debe ingresar un usuario");
                vacio = comprobarCampos(clave, "Debe ingresar una clave");
                vacio = comprobarCampos(nombre, "Debe ingresar un nombre");
                vacio = comprobarCampos(apellido, "Debe ingresar un apellido");
                if(vacio == 0){
                    editar(server.getServer() + "editarUsuario.php", perfil_cocina.this, id_usuario);
                }
            }
        });
    }

    public int comprobarCampos(EditText campo , String error){
        int bandera = 0;
        if(campo.getText().toString().trim().equals("")){
            bandera++;
            campo.setError(error);
        }
        return bandera;
    }

    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity_cocina.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void editar(String url_servidor, Context context , String id_usuario){
        pd.setMessage("Guardando datos de usuario...");
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("actualizacion exitosa")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("El usuario se actualizó correctamente");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor update = preferences.edit();
                            update.putString("usuario", usuario.getText().toString().trim());
                            update.commit();
                            onBackPressed();
                        }
                    });
                    alert.show();
                } else if(response.equals("usuario existe")){
                    pd.hide();
                    usuario.setError("Usuario en uso");
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("El usuario ya se encuentra en uso, intente con otro");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                } else if(response.equals("error al actualizar")){
                    pd.hide();
                    usuario.setError("Usuario en uso");
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("Ha ocurrido un error al intentar actualizar, por favor intente nuevamente más tarde");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
                    alert.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide();
                Toast.makeText(context, "Ha ocurrido un error al intentar actualizar", Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_usuario", id_usuario);
                parametros.put("nombre", nombre.getText().toString().trim());
                parametros.put("apellido", apellido.getText().toString().trim());
                parametros.put("usuario", usuario.getText().toString().trim());
                parametros.put("clave", clave.getText().toString().trim());
                parametros.put("rol", "Cocina");
                return parametros;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void listar(String url_servidor, Context context){
        pd.setMessage("Cargando usuario...");
        pd.show();
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    pd.hide();
                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);


                            nombre.setText(jsonObject.getString("nombre"));
                            apellido.setText(jsonObject.getString("apellido"));
                            usuario.setText(jsonObject.getString("usuario"));
                            clave.setText(jsonObject.getString("clave"));

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    pd.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.toString().equals("com.android.volley.ParseError: org.json.JSONException: End of input at character 0 of ")){
                    pd.hide();
                } else {
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }
}