package com.example.kapricho.cliente.fragments.clases;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kapricho.R;
import com.example.kapricho.cliente.MainActivity_cliente;
import com.example.kapricho.cliente.fragments.carrito_cliente;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AdaptadorPedidoCliente extends RecyclerView.Adapter<AdaptadorPedidoCliente.ViewHolder> {
    ArrayList<ItemsPedido> list;
    Context context;
    public static String id_pedido;
    public static String total;
    public static String estado;
    public static String mesa;

    public AdaptadorPedidoCliente(ArrayList<ItemsPedido> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorPedidoCliente.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedidos_cliente_list_style, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPedidoCliente.ViewHolder holder, int position) {
        holder.id_pedido.setText("Orden No: " + list.get(position).getId_pedido());
        holder.mesa.setText("Mesa: " + list.get(position).getMesa());
        holder.estado.setText(list.get(position).getEstado());
        holder.total.setText("Total: $" + list.get(position).getTotal() + " COP");

        if(list.get(position).getEstado().toString().equals("Entregado")){
            holder.estado.setTextColor(Color.parseColor("#008832"));
        } else if(list.get(position).getEstado().toString().equals("Devuelto") || list.get(position).getEstado().toString().equals("Cancelado")){
            holder.estado.setTextColor(Color.parseColor("#A31116"));
        } else if(list.get(position).getEstado().toString().equals("Recibido") || list.get(position).getEstado().toString().equals("En preparación")){
            holder.estado.setTextColor(Color.parseColor("#D6C41F"));
        }

        String pedido_id = list.get(position).getId_pedido();
        String pedido_total = list.get(position).getTotal();
        String estado_pedido = list.get(position).getEstado();
        String pedido_mesa = list.get(position).getMesa();

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.menu);
                popupMenu.inflate(R.menu.menu_pedido_cliente);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        switch (id){
                            case R.id.ver_pedido_cliente:
                                id_pedido = pedido_id;
                                mesa = pedido_mesa ;
                                detalle_pedido_cliente detalle = new detalle_pedido_cliente();
                                FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.container_layout_cliente, detalle);
                                transaction.commit();
                                break;
                            case R.id.ver_estado_cliente:
                                id_pedido = pedido_id;
                                total = pedido_total;
                                estado = estado_pedido;
                                estado_pedido_cliente carrito = new estado_pedido_cliente();
                                FragmentTransaction transactio = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                                transactio.replace(R.id.container_layout_cliente, carrito);
                                transactio.commit();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id_pedido, mesa, estado, total;
        FloatingActionButton menu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id_pedido = (TextView) itemView.findViewById(R.id.textView_orden_cliente);
            mesa = (TextView) itemView.findViewById(R.id.textView_mesa_cliente);
            estado = (TextView) itemView.findViewById(R.id.textView_estatus_orden_cliente);
            total = (TextView) itemView.findViewById(R.id.textView_total_orden_cliente);
            menu = (FloatingActionButton) itemView.findViewById(R.id.floatingActionButtonMenuOrderCliente);
        }
    }
}
