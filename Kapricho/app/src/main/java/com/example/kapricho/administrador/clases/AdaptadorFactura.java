package com.example.kapricho.administrador.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kapricho.R;

import java.util.List;

public class AdaptadorFactura extends RecyclerView.Adapter<AdaptadorFactura.ViewHolder> {
    List<ItemFactura> list;
    Context context;

    public AdaptadorFactura(List<ItemFactura> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorFactura.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.detalle_factura, parent, false);

        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorFactura.ViewHolder holder, int position) {
        holder.producto.setText(list.get(position).getProducto());
        holder.cantidad.setText(list.get(position).getCantidad());
        holder.unit.setText(list.get(position).getUnit());
        holder.total.setText(list.get(position).getTotal());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView producto, cantidad, total, unit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            producto = (TextView) itemView.findViewById(R.id.producto_detalle_pedido);
            cantidad = (TextView) itemView.findViewById(R.id.cantidad_detalle_pedido);
            unit = (TextView) itemView.findViewById(R.id.total_unit_pedido);
            total = (TextView) itemView.findViewById(R.id.total_detalle_pedido);
        }
    }
}
