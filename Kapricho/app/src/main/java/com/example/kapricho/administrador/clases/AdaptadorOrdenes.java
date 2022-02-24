package com.example.kapricho.administrador.clases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.widget.PopupMenu;
import android.widget.Toast;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaptadorOrdenes extends RecyclerView.Adapter<AdaptadorOrdenes.ViewHolder> {
    List<ItemsOrdenes> list;
    Context context;
    ProgressDialog pd;
    config server;

    public AdaptadorOrdenes(List<ItemsOrdenes> list, Context context) {
        this.list = list;
        this.context = context;
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        server = new config();
    }

    @NonNull
    @Override
    public AdaptadorOrdenes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list_style, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorOrdenes.ViewHolder holder, int position) {
        holder.noOrden.setText("Orden Nº: " + list.get(position).getNoOrden());
        holder.mesa.setText("Mesa: " + list.get(position).getMesa());
        holder.medio_pago.setText("Pago: " + list.get(position).getMedido_pago());
        holder.nombre_cli.setText(list.get(position).getNombre_cli());
        holder.id_cli.setText(list.get(position).getId_cliente());
        holder.total.setText("$" + list.get(position).getTotal() + " COP");
        holder.estatus.setText(list.get(position).getEstatus());

        if(holder.estatus.getText().toString().equals("Pagado")){
            holder.estatus.setTextColor(Color.parseColor("#008832"));
        } else if(holder.estatus.getText().toString().equals("Anulado") || holder.estatus.getText().toString().equals("Cancelado")){
            holder.estatus.setTextColor(Color.parseColor("#A31116"));
        } else if(holder.estatus.getText().toString().equals("Sin pagar")){
            holder.estatus.setTextColor(Color.parseColor("#D6C41F"));
        }

        String total_precio_ =  list.get(position).getTotal();
        String orden_string = list.get(position).getNoOrden();


        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.menu);
                if(holder.estatus.getText().toString().equals("Anulado") || holder.estatus.getText().toString().equals("Cancelado")){
                    popupMenu.inflate(R.menu.order_menu_3);
                } else if(holder.estatus.getText().toString().equals("Pagado")){
                    popupMenu.inflate(R.menu.menu_factura_2);
                } else {
                    popupMenu.inflate(R.menu.order_list_menu);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        switch (id){
                            case R.id.menu_order_detalles:
                                Intent intent = new Intent(context, factura.class);
                                intent.putExtra("id_pedido", orden_string);
                                intent.putExtra("id_cliente", holder.id_cli.getText().toString());
                                context.startActivity(intent);
                                ((Activity) context).finish();
                                break;
                            case R.id.menu_order_cobrar:
                                LayoutInflater inflater = LayoutInflater.from(context);
                                View vista = inflater.inflate(R.layout.dialog_cobro_pedido, null);
                                AlertDialog dialog = new AlertDialog.Builder(context).create();
                                dialog.setView(vista);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                EditText total_pagar = vista.findViewById(R.id.total_pagar_dialog);
                                EditText efectivo_cliente = vista.findViewById(R.id.efectivo_cliente_dialog);
                                LinearLayout medio_efectivo = vista.findViewById(R.id.medio_efectivo);
                                Spinner medio_pago = vista.findViewById(R.id.medio_pago_dialog);
                                Button cancelar = vista.findViewById(R.id.cancel);
                                Button cobrar = vista.findViewById(R.id.realizar_cobro);
                                TextView orden = vista.findViewById(R.id.tv_no_pedido_dialog);
                                TextView cliente = vista.findViewById(R.id.tv_cliente_dialog);

                                orden.setText(holder.noOrden.getText().toString());
                                cliente.setText(holder.nombre_cli.getText().toString());

                                String items[] = new String[]{"Efectivo", "Transaccion"};
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.drowpdown_style, items);
                                adapter.setDropDownViewResource(R.layout.checked_dropdown);
                                medio_pago.setAdapter(adapter);

                                medio_pago.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        if(i == 0){
                                            medio_efectivo.setVisibility(View.VISIBLE);
                                        } else if(i == 1){
                                            medio_efectivo.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                                Double totalPagar = Double.parseDouble(total_precio_);
                                total_pagar.setInputType(View.AUTOFILL_TYPE_NONE);
                                total_pagar.setText(String.valueOf(totalPagar));

                                cancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });
                                cobrar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(medio_pago.getSelectedItem().toString().equals("Efectivo")){
                                            if(!efectivo_cliente.getText().toString().trim().equals("")){
                                                if(Double.parseDouble(efectivo_cliente.getText().toString()) < totalPagar){
                                                    efectivo_cliente.setError("Debes ingresar un valor mayor al total a pagar");
                                                } else {
                                                    dialog.dismiss();
                                                    Double cambio = Double.parseDouble(efectivo_cliente.getText().toString()) - totalPagar;
                                                    cobrar(server.getServer() + "cobrarPedido.php", context,
                                                            orden_string, "Efectivo",
                                                            cambio);
                                                }
                                            } else {
                                                efectivo_cliente.setError("Debes ingresar un valor");
                                            }
                                        } else if(medio_pago.getSelectedItem().toString().equals("Transaccion")){
                                            dialog.dismiss();
                                            cobrar(server.getServer() + "cobrarPedido.php", context,
                                                    orden_string, "Transaccion",
                                                    0.0);
                                        }
                                    }
                                });
                                dialog.show();
                                break;
                            case R.id.menu_order_anular:
                                MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
                                alert.setMessage("¿Esta seguro que desea anular el siguiente pedido? \n\n" +
                                        "No pedido: " + holder.noOrden.getText().toString() + "\n" +
                                        "Cliente: " + holder.nombre_cli.getText().toString() + "\n" +
                                        "Total: " + holder.total.getText().toString() + "\n\n" +
                                        "Nota: Una vez anulado un pedido no se puede deshacer la acción");
                                alert.setPositiveButton("Anular", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        anular(server.getServer() + "anularPedido.php", context,
                                                orden_string);
                                    }
                                });
                                alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                alert.show();
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
        TextView noOrden;
        TextView mesa;
        TextView nombre_cli;
        TextView id_cli;
        TextView total;
        TextView estatus;
        TextView medio_pago;
        FloatingActionButton menu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noOrden = (TextView) itemView.findViewById(R.id.textView_orden);
            mesa = (TextView) itemView.findViewById(R.id.textView_mesa);
            nombre_cli = (TextView) itemView.findViewById(R.id.textView_nombre_cliente_orden);
            id_cli = (TextView) itemView.findViewById(R.id.textView_id_cliente_orden);
            total = (TextView) itemView.findViewById(R.id.textView_total_orden);
            estatus = (TextView) itemView.findViewById(R.id.textView_estatus_orden);
            medio_pago = (TextView) itemView.findViewById(R.id.textView_pago_orden);
            menu = (FloatingActionButton) itemView.findViewById(R.id.floatingActionButtonMenuOrder);
        }
    }

    public void cobrar(String url_servidor, Context context, String id_pedido, String medioPago, Double cambio){
        pd.setMessage("Realizando cobro...");

        StringRequest request = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("cobro exitoso")){
                    if(medioPago.equals("Transacción")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
                        alert.setCancelable(false);
                        alert.setMessage("El cobro se realizó correctamente");
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
                        alert.setMessage("El cobro se realizó correctamente \n\n" +
                                "Cambio: " + String.valueOf(cambio));
                        alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        });
                        alert.show();
                    }
                } else if(response.equals("error al cobrar")){
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("Ha ocurrido un error al intentar cobrar el pedido, por favor intente nuevamente más tarde");
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
                Toast.makeText(context, "Ha ocurrido un error al intentar cobrar", Toast.LENGTH_SHORT).show();
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
                parametros.put("id_pedido", id_pedido);
                parametros.put("medio_pago", medioPago);
                return parametros;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void anular(String url_servidor, Context context, String id_pedido){
        pd.setMessage("Anulando pedido...");

        StringRequest request = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.hide();
                if(response.equals("anulacion exitoso")){
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("El pedido se anuló correctamente");
                    alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
                    alert.show();
                } else if(response.equals("error al anular")){
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("Ha ocurrido un error al intentar anular el pedido, por favor intente nuevamente más tarde");
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
                Toast.makeText(context, "Ha ocurrido un error al intentar anular el pedido", Toast.LENGTH_SHORT).show();
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
                parametros.put("id_pedido", id_pedido);

                return parametros;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void onBackPressed(){
        context.startActivity(((Activity) context).getIntent());
        ((Activity) context).finish();
    }
}
