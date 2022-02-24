package com.example.kapricho.administrador.clases;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.kapricho.administrador.usuarios_admin;
import com.example.kapricho.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class editar_usuarios_admin extends AppCompatActivity {
    Toolbar toolbar;
    EditText nombre, apellido, usuario, clave;
    Spinner rol;
    CheckBox mostrar;
    config server;
    Button actualizar;
    ProgressDialog pd;
    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuarios_admin);

        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_users_profile_admin);
        nombre = (EditText) findViewById(R.id.nombre_edit_profle);
        usuario = (EditText) findViewById(R.id.username_edit_profle);
        clave = (EditText) findViewById(R.id.clave_edit_profle);
        nombre = (EditText) findViewById(R.id.nombre_edit_profle);
        rol = (Spinner) findViewById(R.id.rol_edit_profle);
        mostrar = (CheckBox) findViewById(R.id.checkBoxVer) ;
        actualizar = (Button) findViewById(R.id.button_actualizar);
        apellido = (EditText) findViewById(R.id.apellido_edit_profle);
        pd = new ProgressDialog(editar_usuarios_admin.this);
        pd.setCancelable(false);
        server = new config();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String id_usuario = getIntent().getStringExtra("id_usuario");
        pd.show();
        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));

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
                    editar(server.getServer() + "editarUsuario.php", editar_usuarios_admin.this, id_usuario);
                }
            }
        });

        String items[] = new String[]{"Administrador", "Cocina"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(editar_usuarios_admin.this, R.layout.drowpdown_style, items);
        adapter.setDropDownViewResource(R.layout.checked_dropdown);
        rol.setAdapter(adapter);

        listar(server.getServer() + "listarId.php?id=" + id_usuario, editar_usuarios_admin.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu2) {
        super.onCreateOptionsMenu(menu2);
        iconColor(menu2, R.color.white);
        return true;
    }
    public void iconColor(Menu menu, int color) {
        for (int i = 0; i < menu.size(); i++ ){
            Drawable drawable2 = menu.getItem(i).getIcon();
            if (drawable2 != null){
                drawable2.mutate();
                drawable2.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public void onBackPressed(){
        Intent i = new Intent(editar_usuarios_admin.this, usuarios_admin.class);
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

    public void listar(String url_servidor, Context context){
        pd.setMessage("Cargando usuario...");
        pd.show();
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    int seleccion = -1;
                    pd.hide();
                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);

                            if(jsonObject.getString("rol").equals("Administrador")){
                                seleccion = 0;
                            } else if(jsonObject.getString("rol").equals("Cocina")){
                                seleccion = 1;
                            }

                            nombre.setText(jsonObject.getString("nombre"));
                            apellido.setText(jsonObject.getString("apellido"));
                            usuario.setText(jsonObject.getString("usuario"));
                            clave.setText(jsonObject.getString("clave"));
                            rol.setSelection(seleccion);

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
                parametros.put("rol", rol.getSelectedItem().toString().trim());
                return parametros;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
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