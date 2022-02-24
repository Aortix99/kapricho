package com.example.kapricho.administrador;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class agregar_producto extends AppCompatActivity {
    FloatingActionButton file;
    int SELECT_PHOTO = 1;
    Uri uri;
    CircleImageView img;
    EditText nombre, precio;
    Spinner disponibilidad, categoria;
    Window window;
    config server;
    Toolbar toolbar;
    ProgressDialog pd;
    Button agregar;
    Bitmap bit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));
        toolbar = (Toolbar) findViewById(R.id.toolbar_add_product_admin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        server = new config();
        file = (FloatingActionButton) findViewById(R.id.floatingActionButtonChooseImage);
        img = (CircleImageView) findViewById(R.id.img_producto);
        nombre = (EditText) findViewById(R.id.nombre_producto);
        precio = (EditText) findViewById(R.id.precio_producto);
        disponibilidad = (Spinner) findViewById(R.id.rol_existencia_producto);
        categoria = (Spinner) findViewById(R.id.categoria_producto);
        pd = new ProgressDialog(agregar_producto.this);
        agregar = (Button) findViewById(R.id.button_agregar_producto);

        pd.setCancelable(false);

        String items[] = new String[]{"Disponible", "Agotado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(agregar_producto.this, R.layout.drowpdown_style, items);
        adapter.setDropDownViewResource(R.layout.checked_dropdown);
        disponibilidad.setAdapter(adapter);

        String items2[] = new String[]{"Desayuno", "Almuerzo", "Adicion"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(agregar_producto.this, R.layout.drowpdown_style, items2);
        adapter2.setDropDownViewResource(R.layout.checked_dropdown);
        categoria.setAdapter(adapter2);

        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_PHOTO);
            }
        });

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vacio = 0;
                vacio = comprobarCampos(nombre, "Debe ingresar un nombre");
                vacio = comprobarCampos(precio, "Debe ingresar un precio");
                if(vacio == 0){
                    agregarProducto(server.getServer() + "agregarProducto.php", agregar_producto.this);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null){
            uri = data.getData();
            try{
                bit = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                img.setImageBitmap(bit);

            }catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBackPressed(){
        Intent i = new Intent(this, productos_admin.class);
        startActivity(i);
        finish();
    }

    public int comprobarCampos(EditText campo , String errorText){
        int bandera = 0;
        if(campo.getText().toString().trim().equals("")){
            bandera++;
            campo.setError(errorText);
        }
        return bandera;
    }

    public void agregarProducto(String url_servidor, Context context){
        pd.setMessage("Guardando producto...");
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("ingreso exitoso")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("El producto se agregó correctamente");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
                    alert.show();
                } else if(response.equals("error al agregar producto")){
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("Ha ocurrido un error al intentar agregar el producto, por favor intente nuevamente " +
                            "más tarde");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
                    alert.show();
                } else {
                    Log.i("error", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide();
                Toast.makeText(context, "Ha ocurrido un error al intentar guardar un producto", Toast.LENGTH_SHORT).show();
                Log.i("error", error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("nombre", nombre.getText().toString().trim());
                parametros.put("precio", precio.getText().toString().trim());
                parametros.put("existencia", disponibilidad.getSelectedItem().toString().trim());
                parametros.put("categoria", categoria.getSelectedItem().toString().trim());
                if(uri != null){
                    String imageData = imageToString(bit);
                    parametros.put("imagen", imageData);
                } else {
                    parametros.put("imagen", "vacio");
                }
                return parametros;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodeImage;
    }
}