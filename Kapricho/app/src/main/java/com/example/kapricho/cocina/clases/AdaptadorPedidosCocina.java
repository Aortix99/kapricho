package com.example.kapricho.cocina.clases;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kapricho.R;
import com.example.kapricho.administrador.clases.ItemFactura;

import java.util.List;

public class AdaptadorPedidosCocina extends RecyclerView.Adapter<AdaptadorPedidosCocina.ViewHolder> {
    List<ItemsPedidosCocina> list;
    Context context;

    public AdaptadorPedidosCocina(List<ItemsPedidosCocina> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorPedidosCocina.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_pedidos_cocina, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPedidosCocina.ViewHolder holder, int position) {
        holder.orden.setText("Orden No: " + list.get(position).getNoPedido());
        holder.mesa.setText("Mesa: " + list.get(position).getMesa());
        holder.nombre.setText(list.get(position).getNombreCliente());
        holder.estado.setText(list.get(position).getEstadoPedido());

        if(holder.estado.getText().toString().equals("Entregado")){
            holder.estado.setTextColor(Color.parseColor("#008832"));
        } else if(holder.estado.getText().toString().equals("Devuelto")){
            holder.estado.setTextColor(Color.parseColor("#A31116"));
        } else if(holder.estado.getText().toString().equals("Recibido") || holder.estado.getText().toString().equals("En preparación")){
            holder.estado.setTextColor(Color.parseColor("#D6C41F"));
        }

        String ordenPedido = list.get(position).getNoPedido();
        String estatusPedido = list.get(position).getEstadoPedido();
        String correo = list.get(position).getCorreo();
        String nombre = list.get(position).getNombreCliente();
        String mesa = list.get(position).getMesa();
        String total = list.get(position).getTotal();

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, gestion_pedidos.class);
                i.putExtra("id_pedido", ordenPedido);
                i.putExtra("estatus", estatusPedido);
                i.putExtra("nombre_cliente", nombre);
                i.putExtra("correo", correo);
                i.putExtra("mesa", mesa);
                i.putExtra("total", total);
                context.startActivity(i);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orden, mesa, nombre, estado;
        CardView item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orden = (TextView) itemView.findViewById(R.id.textView_orden_cocina);
            mesa = (TextView) itemView.findViewById(R.id.textView_mesa_cocina);
            nombre = (TextView) itemView.findViewById(R.id.textView_nombre_cliente_orden_cocina);
            estado = (TextView) itemView.findViewById(R.id.textView_estatus_orden_cocina);
            item = (CardView) itemView.findViewById(R.id.cardview_item_cocina);
        }
    }
}
