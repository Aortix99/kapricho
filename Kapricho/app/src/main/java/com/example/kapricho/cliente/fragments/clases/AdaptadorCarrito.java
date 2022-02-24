package com.example.kapricho.cliente.fragments.clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kapricho.R;
import com.example.kapricho.cliente.fragments.carrito_cliente;
import com.example.kapricho.config;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorCarrito extends RecyclerView.Adapter<AdaptadorCarrito.ViewHolder> {
    List<ItemsCarrito> list;
    Context context;
    config server;

    public AdaptadorCarrito(List<ItemsCarrito> list, Context context) {
        this.list = list;
        this.context = context;
        server = new config();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.productos_cliente_carrito_list_style, parent,
                false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.descripcion.setText(list.get(position).getDescripcion());
        holder.existencia.setText(list.get(position).getExistencia());
        holder.cantidad.setText("Cantidad: " + list.get(position).getCantidad());
        holder.precio.setText("Precio: $" + list.get(position).getPrecio() + " COP");
        holder.total.setText("Total: $" + list.get(position).getTotal() + " COP");

        Picasso.get()
                .load(list.get(position).getImagen())
                .error(R.drawable.unlinked)
                .placeholder(R.drawable.gallery_black)
                .into(holder.imagen);

        if(list.get(position).getExistencia().equals("Disponible")){
            holder.existencia.setTextColor(Color.parseColor("#008832"));
        } else if (list.get(position).getExistencia().equals("Agotado")){
            holder.existencia.setTextColor(Color.parseColor("#A31116"));
        }

        String url_imagen = list.get(position).getImagen();
        String token_user = list.get(position).getToken();
        String id_producto = list.get(position).getId_producto();
        int posicion = position;

        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View vista = inflater.inflate(R.layout.image_dialog_layout, null);
                AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.setView(vista);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView imagen_dialog = vista.findViewById(R.id.your_image);
                Picasso.get()
                        .load(url_imagen)
                        .error(R.drawable.unlinked2)
                        .placeholder(R.drawable.placeholder)
                        .into(imagen_dialog);
                imagen_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                alert.setMessage("¿Esta seguro que desea eliminar este producto del carrito?");
                alert.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eliminarDelCarrito(server.getServer() + "eliminarItemCarrito.php", token_user, id_producto,
                                context, posicion);
                    }
                });
                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.setCancelable(false);
                alert.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion, existencia, precio, cantidad, total;
        FloatingActionButton delete;
        CircleImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            descripcion = (TextView) itemView.findViewById(R.id.descripcion_carrito);
            existencia = (TextView) itemView.findViewById(R.id.existencia_carrito);
            precio = (TextView) itemView.findViewById(R.id.precio_carrito);
            cantidad = (TextView) itemView.findViewById(R.id.cantidad_carrito);
            delete = (FloatingActionButton) itemView.findViewById(R.id.delete_carrito);
            imagen = (CircleImageView) itemView.findViewById(R.id.imagen_carrito);
            total = (TextView) itemView.findViewById(R.id.total_item_carrito);
        }
    }

    private void eliminarDelCarrito(String server_url, String token_user, String id_producto, Context context, int position) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Eliminando producto...");
        pd.setCancelable(false);
        pd.show();
        StringRequest string = new StringRequest(Request.Method.POST, server_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("exitoso")){
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage("El producto se eliminó correctamente del carrito");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            carrito_cliente carrito = new carrito_cliente();
                            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.container_layout_cliente, carrito);
                            transaction.commit();
                        }
                    });
                    alert.show();
                } else if (response.equals("error")){
                    AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                    alerta.setMessage("Ocurrio un error al intentar eliminar el producto");
                    alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                } else {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                    alerta.setMessage(response);
                    alerta.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("token", token_user);
                parametros.put("id_producto", id_producto);
                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(string);
    }
}
