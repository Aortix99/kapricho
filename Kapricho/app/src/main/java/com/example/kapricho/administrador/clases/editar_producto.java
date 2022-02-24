package com.example.kapricho.administrador.clases;

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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.kapricho.administrador.agregar_producto;
import com.example.kapricho.administrador.productos_admin;
import com.example.kapricho.config;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class editar_producto extends AppCompatActivity {
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
    Button update;
    Bitmap bit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));
        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_product_admin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        file = (FloatingActionButton) findViewById(R.id.floatingActionButtonChooseImageEdit);
        img = (CircleImageView) findViewById(R.id.img_producto_edit);
        nombre = (EditText) findViewById(R.id.nombre_producto_edit);
        precio = (EditText) findViewById(R.id.precio_producto_edit);
        disponibilidad = (Spinner) findViewById(R.id.rol_existencia_producto_edit);
        categoria = (Spinner) findViewById(R.id.categoria_producto_edit);
        pd = new ProgressDialog(editar_producto.this);
        update = (Button) findViewById(R.id.button_editar_producto);
        String id_producto = getIntent().getStringExtra("id_producto");

        pd.setCancelable(false);
        server = new config();

        String items[] = new String[]{"Disponible", "Agotado"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(editar_producto.this, R.layout.drowpdown_style, items);
        adapter.setDropDownViewResource(R.layout.checked_dropdown);
        disponibilidad.setAdapter(adapter);

        String items2[] = new String[]{"Desayuno", "Almuerzo", "Adicion"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(editar_producto.this, R.layout.drowpdown_style, items2);
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

        listar(server.getServer() + "listarProductoId.php?id_producto=" + id_producto, editar_producto.this);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vacio = 0;
                vacio = comprobarCampos(precio, "Debe ingresar un precio");
                vacio = comprobarCampos(nombre, "Debe ingresar un nombre");

                if(vacio == 0){
                    editar(server.getServer() + "editarProducto.php", editar_producto.this, id_producto);
                }
            }
        });
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

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        String encodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodeImage;
    }

    public void listar(String url_servidor, Context context){
        pd.setMessage("Cargando usuario...");
        pd.show();
        JsonArrayRequest array = new JsonArrayRequest(url_servidor, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response.length() > 0){
                    int seleccion = -1;
                    int seleccion_categoria = -1;
                    pd.hide();
                    JSONObject jsonObject;
                    for (int i = 0; i < response.length(); i++){
                        try{
                            jsonObject = response.getJSONObject(i);

                            if(jsonObject.getString("existencia").equals("Disponible")){
                                seleccion = 0;
                            } else if(jsonObject.getString("existencia").equals("Agotado")){
                                seleccion = 1;
                            }

                            if(jsonObject.getString("categoria").equals("Desayuno")){
                                seleccion_categoria = 0;
                            } else if(jsonObject.getString("categoria").equals("Almuerzo")){
                                seleccion_categoria = 1;
                            } else if(jsonObject.getString("categoria").equals("Adicion")){
                                seleccion_categoria = 2;
                            }

                            nombre.setText(jsonObject.getString("descripcion"));
                            precio.setText(jsonObject.getString("precio"));
                            disponibilidad.setSelection(seleccion);
                            categoria.setSelection(seleccion_categoria);
                            Picasso.get()
                                    .load(server.getServer() + jsonObject.getString("imagen"))
                                    .error(R.drawable.unlinked)
                                    .placeholder(R.drawable.gallery_black)
                                    .into(img);;

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(editar_producto.this, "Error: " + response.toString(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(editar_producto.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(array);
    }

    public void editar(String url_servidor, Context context , String id_producto){
        pd.setMessage("Guardando datos del producto...");
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("actualizacion exitosa")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("El producto se actualizó correctamente");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
                    alert.show();
                } else if(response.equals("error al actualizar")){
                    pd.hide();
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
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage(response);
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
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setCancelable(false);
                alert.setMessage(error.toString());
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
                alert.show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_producto", id_producto);
                parametros.put("descripcion", nombre.getText().toString().trim());
                parametros.put("precio", precio.getText().toString().trim());
                parametros.put("disponibilidad", disponibilidad.getSelectedItem().toString().trim());
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

}