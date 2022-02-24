package com.example.kapricho.cliente.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.kapricho.cocina.perfil_cocina;
import com.example.kapricho.config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link cuenta_cliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class cuenta_cliente extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Toolbar toolbar;
    EditText nombre, apellido, usuario, correo, clave;
    CheckBox mostrar;
    config server;
    Button actualizar;
    SharedPreferences preferences;

    public cuenta_cliente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cuenta_cliente.
     */
    // TODO: Rename and change types and number of parameters
    public static cuenta_cliente newInstance(String param1, String param2) {
        cuenta_cliente fragment = new cuenta_cliente();
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
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_cuenta_cliente, container, false);

        nombre = vista.findViewById(R.id.nombre_edit_profle_cliente);
        usuario = vista.findViewById(R.id.username_edit_profle_cliente);
        correo = vista.findViewById(R.id.correo_edit_profle_cliente);
        clave = vista.findViewById(R.id.clave_edit_profle_cliente);
        nombre = vista.findViewById(R.id.nombre_edit_profle_cliente);
        mostrar = vista.findViewById(R.id.checkBoxVerCliente);
        actualizar = vista.findViewById(R.id.button_actualizar_cliente);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        apellido = vista.findViewById(R.id.apellido_edit_profle_cliente);
        server = new config();

        String id_usuario = preferences.getString("id_cliente", null);

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
                    editar(server.getServer() + "editarCliente.php", getActivity(), id_usuario);
                }
            }
        });

        listar(server.getServer() + "listarIdCliente.php?id=" + id_usuario, getActivity());

        return vista;
    }

    public void listar(String url_servidor, Context context){
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Cargando usuario...");
        pd.setCancelable(false);
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


                            nombre.setText(jsonObject.getString("nombre_cliente"));
                            apellido.setText(jsonObject.getString("apellido_cliente"));
                            usuario.setText(jsonObject.getString("usuario_cliente"));
                            correo.setText(jsonObject.getString("correo_cliente"));
                            clave.setText(jsonObject.getString("clave_cliente"));

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
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Guardando datos de usuario...");
        pd.setCancelable(false);
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
                parametros.put("correo", correo.getText().toString().trim());
                parametros.put("clave", clave.getText().toString().trim());
                parametros.put("rol", "Cliente");
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