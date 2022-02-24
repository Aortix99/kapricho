package com.example.kapricho.cocina.clases;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kapricho.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorGestionPedido extends RecyclerView.Adapter<AdaptadorGestionPedido.ViewHolder> {
    List<ItemGestionPedido> list;

    public AdaptadorGestionPedido(List<ItemGestionPedido> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AdaptadorGestionPedido.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_detalle_pedido_cocina, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorGestionPedido.ViewHolder holder, int position) {
        holder.descripcion.setText(list.get(position).getDescripcion());
        holder.cantidad.setText("Cantidad: " + list.get(position).getCantidad());

        Picasso.get()
                .load(list.get(position).getImagen())
                .error(R.drawable.unlinked)
                .placeholder(R.drawable.gallery_black)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion, cantidad;
        CircleImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            descripcion = (TextView) itemView.findViewById(R.id.producto_detalle_del_pedido);
            cantidad = (TextView) itemView.findViewById(R.id.cantidad_detalle_del_pedido);
            img = (CircleImageView) itemView.findViewById(R.id.img_detalle_del_pedido);

        }
    }
}
