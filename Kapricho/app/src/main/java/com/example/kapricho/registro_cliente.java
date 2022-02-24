package com.example.kapricho;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.administrador.agregar_usuario_admin;
import com.example.kapricho.administrador.usuarios_admin;

import java.util.HashMap;
import java.util.Map;

public class registro_cliente extends AppCompatActivity {
    Toolbar toolbar;
    EditText nombre, apellido, usuario, clave, correo;
    CheckBox mostrar;
    config server;
    Button agregar;
    Window window;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cliente);

        toolbar = (Toolbar) findViewById(R.id.toolbar_regitro_cliente);
        nombre = (EditText) findViewById(R.id.nombre_regitro_cliente);
        usuario = (EditText) findViewById(R.id.username_regitro_cliente);
        clave = (EditText) findViewById(R.id.clave_regitro_cliente);
        nombre = (EditText) findViewById(R.id.nombre_regitro_cliente);
        mostrar = (CheckBox) findViewById(R.id.checkBoxVerRegistroCliente) ;
        agregar = (Button) findViewById(R.id.button_regitro_cliente);
        apellido = (EditText) findViewById(R.id.apellido_regitro_cliente);
        correo = (EditText) findViewById(R.id.correo_regitro_cliente);
        server = new config();
        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vacio = 0;
                vacio = comprobarCampos(usuario, "Debe ingresar un usuario");
                vacio = comprobarCampos(correo, "Debe ingresar un correo");
                vacio = comprobarCampos(clave, "Debe ingresar una clave");
                vacio = comprobarCampos(nombre, "Debe ingresar un nombre");
                vacio = comprobarCampos(apellido, "Debe ingresar un apellido");
                if(vacio == 0){
                    agregarUsuario(server.getServer() + "registrarCliente.php", registro_cliente.this);
                }
            }
        });
    }

    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity.class);
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

    public int comprobarCampos(EditText campo , String errorText){
        int bandera = 0;
        if(campo.getText().toString().trim().equals("")){
            bandera++;
            campo.setError(errorText);
        }
        return bandera;
    }

    public void agregarUsuario(String url_servidor, Context context){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Guardando datos de usuario...");
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("ingreso exitoso")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("El usuario se registró correctamente");
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
                } else if(response.equals("error al agregar usuario")){
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("Ha ocurrido un error al intentar agregar el usuario, por favor intente nuevamente " +
                            "más tarde");
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
                Toast.makeText(context, "Ha ocurrido un error al intentar agregar", Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("nombre", nombre.getText().toString().trim());
                parametros.put("apellido", apellido.getText().toString().trim());
                parametros.put("usuario", usuario.getText().toString().trim());
                parametros.put("correo", correo.getText().toString().trim());
                parametros.put("clave", clave.getText().toString().trim());
                parametros.put("rol", "Cliente");
                return parametros;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}