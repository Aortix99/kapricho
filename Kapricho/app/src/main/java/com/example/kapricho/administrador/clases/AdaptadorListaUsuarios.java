package com.example.kapricho.administrador.clases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorListaUsuarios extends RecyclerView.Adapter<AdaptadorListaUsuarios.ViewHolder> implements Filterable {
    List<ItemUsuarios> list;
    List<ItemUsuarios> origList;
    Context context;
    config server;
    ProgressDialog pd;

    public AdaptadorListaUsuarios(List<ItemUsuarios> list, Context context) {
        this.list = list;
        this.origList = list;
        this.context = context;
        server = new config();
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setMessage("Eliminando usuario...");
    }

    @NonNull
    @Override
    public AdaptadorListaUsuarios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_style, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaUsuarios.ViewHolder holder, int position) {
        holder.id.setText(list.get(position).getId());
        holder.nombre.setText(list.get(position).getNombre());
        holder.rol.setText(list.get(position).getRol());
        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, editar_usuarios_admin.class);
                i.putExtra("id_usuario", holder.id.getText().toString().trim());
                context.startActivity(i);
                ((Activity) context).finish();
            }
        });
        holder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("¿Esta seguro que desea eliminar el siguiente usuario? \n\n" +
                        "Nombre: " + holder.nombre.getText().toString() + "\n" +
                        "Rol: " + holder.rol.getText().toString());
                alert.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pd.show();
                        eliminar(server.getServer() + "eliminarUsuario.php", context,
                                holder.id.getText().toString());
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<ItemUsuarios> results = new ArrayList<>();
                if (origList == null)
                    origList = new ArrayList<>(list);
                if (constraint != null && constraint.length() > 0) {
                    if (origList != null && origList.size() > 0) {
                        for (final ItemUsuarios cd : origList) {
                            if (cd.getNombre().toLowerCase()
                                    .contains(constraint.toString().toLowerCase()))
                                results.add(cd);
                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();//newly Aded by ZA
                } else {
                    oReturn.values = origList;
                    oReturn.count = origList.size();//newly added by ZA
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(final CharSequence constraint,
                                          FilterResults results) {
                list = new ArrayList<>((ArrayList<ItemUsuarios>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, rol, id;
        Button editar, borrar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.tv_id_user_item);
            nombre = (TextView) itemView.findViewById(R.id.tv_nombre_item);
            rol = (TextView) itemView.findViewById(R.id.tv_rol_item);
            editar = (Button) itemView.findViewById(R.id.button_edit_usuario);
            borrar = (Button) itemView.findViewById(R.id.button_delete_usuario);
        }
    }

    public void eliminar(String url_servidor, Context context , String id_usuario){
        StringRequest request = new StringRequest(Request.Method.POST, url_servidor, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("eliminacion exitosa")){
                    pd.hide();
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setCancelable(false);
                    alert.setMessage("El usuario se eliminó correctamente");
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
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.hide();
                Toast.makeText(context, "Ha ocurrido un error al intentar eliminar", Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("id_usuario", id_usuario);
                return parametros;

            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
