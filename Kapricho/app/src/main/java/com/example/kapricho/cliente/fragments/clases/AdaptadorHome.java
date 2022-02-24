package com.example.kapricho.cliente.fragments.clases;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorHome extends RecyclerView.Adapter<AdaptadorHome.ViewHolder> {
    List<ItemsHome> list;
    Context context;
    config server;

    public AdaptadorHome(List<ItemsHome> list, Context context) {
        this.list = list;
        this.context = context;
        server = new config();
    }

    @NonNull
    @Override
    public AdaptadorHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.productos_cliente_list_style, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorHome.ViewHolder holder, int position) {

        holder.precio.setText("$" + list.get(position).getPrecio() + " COP");
        holder.descripcion.setText(list.get(position).getDescripcion());
        holder.disponibilidad.setText(list.get(position).getDisponibilidad());

        Picasso.get()
                .load(list.get(position).getImagen())
                .error(R.drawable.unlinked)
                .placeholder(R.drawable.gallery_black)
                .into(holder.imagen);

        String id_producto = list.get(position).getId();
        String id_cliente = list.get(position).getId_cliente();

        holder.mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cant = Integer.parseInt(holder.cantidad.getText().toString().trim());
                holder.cantidad.setText(String.valueOf(++cant));

                if(cant <= 1){
                    holder.menos.setEnabled(false);
                    holder.menos.setTextColor(Color.parseColor("#787878"));
                    holder.menos.setBackgroundColor(Color.parseColor("#E0E0E0"));
                } else {
                    holder.menos.setEnabled(true);
                    holder.menos.setTextColor(Color.parseColor("#ffffff"));
                    holder.menos.setBackgroundColor(Color.parseColor("#263238"));
                }
            }
        });


        holder.menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cant = Integer.parseInt(holder.cantidad.getText().toString().trim());
                holder.cantidad.setText(String.valueOf(--cant));

                if(cant <= 1){
                    holder.menos.setEnabled(false);
                    holder.menos.setTextColor(Color.parseColor("#787878"));
                    holder.menos.setBackgroundColor(Color.parseColor("#E0E0E0"));
                } else {
                    holder.menos.setEnabled(true);
                    holder.menos.setTextColor(Color.parseColor("#ffffff"));
                    holder.menos.setBackgroundColor(Color.parseColor("#263238"));
                }
            }
        });

        String url_imagen = list.get(position).getImagen();

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

        if(list.get(position).getDisponibilidad().equals("Disponible")){
            holder.disponibilidad.setBackgroundResource(R.drawable.background_disponible);

            holder.add_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String cantidad = holder.cantidad.getText().toString();
                    agregarAlCarrito(server.getServer() + "agregarCarrito.php", id_producto, id_cliente, cantidad, context);
                    holder.cantidad.setText("1");
                }
            });

        } else if (list.get(position).getDisponibilidad().equals("Agotado")){

            holder.disponibilidad.setBackgroundResource(R.drawable.background_agotado);
            holder.add_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage("Lo sentimos, por el momento no tenemos suficientes ingredientes " +
                            "para elaborar este producto, agredecemos su comprensión.\n\n" +
                            "Nota: Se esta haciendo lo posible por obtener los ingredientes faltantes, por lo que" +
                            " puede que este producto este disponible los próximos minutos");
                    alert.setPositiveButton("Escoger otro producto", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion, precio, disponibilidad;
        CircleImageView imagen;
        FloatingActionButton add_cart;
        Button mas, menos;
        EditText cantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            descripcion = (TextView) itemView.findViewById(R.id.tv_descripcion_producto_cliente);
            precio = (TextView) itemView.findViewById(R.id.tv_precio_producto_cliente);
            disponibilidad = (TextView) itemView.findViewById(R.id.tv_disponibilidad_cliente);
            imagen = (CircleImageView) itemView.findViewById(R.id.img_producto_cliente);
            add_cart = (FloatingActionButton) itemView.findViewById(R.id.btn_add_to_cart);
            mas = (Button) itemView.findViewById(R.id.mas_uno_cliente);
            menos = (Button) itemView.findViewById(R.id.menos_uno_cliente);
            cantidad = (EditText) itemView.findViewById(R.id.cantidad);
        }
    }

    private void agregarAlCarrito(String url_server, String id_producto, String id_cliente, String cantidad, Context context) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Agregando al carrito");
        pd.setCancelable(false);
        pd.show();
        StringRequest string = new StringRequest(Request.Method.POST, url_server, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("Agregado correctamente")){
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage("El producto se agrego correctamente al carrito de compras");
                    alert.setPositiveButton("Seguir comprando", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.setNegativeButton("Ver carrito", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            carrito_cliente carrito = new carrito_cliente();
                            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.container_layout_cliente, carrito);
                            transaction.commit();
                        }
                    });
                    alert.show();

                } else if(response.equals("Error al agregar")){
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage("Ocurrio un error al intentar agregar al carrito, por favor intente nuevamente más" +
                            "tarde");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.show();
                } else if(response.equals("item agregado")){
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage("Este producto ya lo agregaste al carrito de compras");
                    alert.setPositiveButton("Seguir comprando", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.setNegativeButton("Ver carrito", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            carrito_cliente carrito = new carrito_cliente();
                            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.container_layout_cliente, carrito);
                            transaction.commit();
                        }
                    });
                    alert.show();
                } else {
                    MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                    alert.setMessage(response);
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alert.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide();
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();

                parametros.put("token_user", id_cliente);
                parametros.put("id_producto", id_producto);
                parametros.put("cantidad", cantidad);
                return parametros;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(string);
    }
}
