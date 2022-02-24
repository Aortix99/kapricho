package com.example.kapricho.cliente.fragments.clases;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kapricho.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorDetallePedidoCliente extends RecyclerView.Adapter<AdaptadorDetallePedidoCliente.ViewHolder> {
    List<ItemDetallePedidoCliente> list;
    Context context;

    public AdaptadorDetallePedidoCliente(List<ItemDetallePedidoCliente> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorDetallePedidoCliente.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detalle_pedido_cocina, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDetallePedidoCliente.ViewHolder holder, int position) {
        holder.descripcion.setText(list.get(position).getDescripcion());
        holder.cantidad.setText("Cantidad: " + list.get(position).getCantidad());

        Picasso.get()
                .load(list.get(position).getImagen())
                .error(R.drawable.unlinked)
                .placeholder(R.drawable.gallery_black)
                .into(holder.imagen);

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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView descripcion, cantidad;
        CircleImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            descripcion = (TextView) itemView.findViewById(R.id.producto_detalle_del_pedido);
            cantidad = (TextView) itemView.findViewById(R.id.cantidad_detalle_del_pedido);
            imagen = (CircleImageView) itemView.findViewById(R.id.img_detalle_del_pedido);

        }
    }
}
