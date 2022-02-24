package com.example.kapricho.administrador.clases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.config;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ViewHolder> {
    List<ItemsProductos> list;
    Context context;
    ProgressDialog pd;
    config server;

    public AdaptadorProductos(List<ItemsProductos> lista, Context context) {
        this.list = lista;
        this.context = context;
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Eliminando producto...");
        server = new config();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.productos_list_style_admin, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorProductos.ViewHolder holder, int position) {
        holder.nombre.setText(list.get(position).getNombre());
        holder.precio.setText("$" + list.get(position).getPrecio() + " COP");
        holder.disponibilidad.setText(list.get(position).getDisponibilidad());
        holder.id.setText(list.get(position).getId());

        if(holder.disponibilidad.getText().toString().equals("Disponible")){
            holder.disponibilidad.setTextColor(Color.parseColor("#008832"));
        } else if(holder.disponibilidad.getText().toString().equals("Agotado")){
            holder.disponibilidad.setTextColor(Color.parseColor("#A31116"));
        }

        Picasso.get()
                .load(list.get(position).getImagen())
                .error(R.drawable.unlinked)
                .placeholder(R.drawable.gallery_black)
                .into(holder.imagen);

        String imagen_string = list.get(position).getImagen();
        String[] imagen_array = imagen_string.split("/");
        String imagen = imagen_array[4] + "/" +imagen_array[5];

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("¿Esta seguro que desea eliminar el siguiente producto? \n\n" +
                        "Descripcion: " + holder.nombre.getText().toString());
                alert.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        eliminar(server.getServer() + "eliminarProducto.php", context,
                                holder.id.getText().toString(), imagen);
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
        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editar = new Intent(context, editar_producto.class);
                editar.putExtra("id_producto", holder.id.getText().toString());
                context.startActivity(editar);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imagen;
        TextView nombre, precio, id, disponibilidad;
        Button delete, update;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = (CircleImageView) itemView.findViewById(R.id.circle_list_product_admin);
            nombre = (TextView) itemView.findViewById(R.id.textView_nombre_producto);
            precio = (TextView) itemView.findViewById(R.id.textView_precio);
            id = (TextView) itemView.findViewById(R.id.textView_id_producto);
            disponibilidad = (TextView) itemView.findViewById(R.id.textView_disponibilidad);
            delete = (Button) itemView.findViewById(R.id.button_delete_product);
            update = (Button) itemView.findViewById(R.id.button_update_product);
        }
    }

    public void eliminar(String url_servidor, Context context , String id_producto, String imagen){
        StringRequest request = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("eliminacion exitosa")){
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("El producto se eliminó correctamente");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.startActivity(((Activity) context).getIntent());
                            ((Activity) context).finish();
                        }
                    });
                    alert.show();
                } else if(response.equals("error al eliminar")){
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("Ha ocurrido un error al intentar eliminar, por favor intente nuevamente más tarde");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    Log.i("Error response eliminar", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide();
                Toast.makeText(context, "Ha ocurrido un error al intentar eliminar", Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_producto", id_producto);
                parametros.put("imagen", imagen);
                return parametros;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
