package com.example.kapricho.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.Window;

import com.example.kapricho.MainActivity;
import com.example.kapricho.R;
import com.example.kapricho.cliente.fragments.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity_cliente extends AppCompatActivity {
    carrito_cliente carrito;
    home_cliente home;
    cuenta_cliente cuenta;
    pedidos_cliente pedidos;
    BottomNavigationView nav;
    SharedPreferences preferences;
    Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cliente);

        carrito = new carrito_cliente();
        home = new home_cliente();
        cuenta = new cuenta_cliente();
        pedidos = new pedidos_cliente();
        nav = (BottomNavigationView) findViewById(R.id.navView_cliente);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        window = getWindow();
        window.setStatusBarColor(Color.parseColor("#000a12"));

        nav.setSelectedItemId(R.id.inicio_cliente);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.inicio_cliente:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout_cliente, home).
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        break;
                    case R.id.pedidos_cliente:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout_cliente, pedidos).
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        break;
                    case R.id.carrito_compras_cliente:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout_cliente, carrito).
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        break;
                    case R.id.cuenta_cliente:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container_layout_cliente, cuenta).
                                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        break;
                    case R.id.logout_cliente:
                        MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(MainActivity_cliente.this);
                        alert.setMessage("¿Esta seguro que desea cerrar la sesión?");
                        alert.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.remove("usuario");
                                editor.remove("clave");
                                editor.remove("rol");
                                editor.apply();

                                Intent in = new Intent(MainActivity_cliente.this, MainActivity.class);
                                startActivity(in);
                                finish();
                            }
                        });
                        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.container_layout_cliente, home).
                                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                                nav.setSelectedItemId(R.id.inicio_cliente);
                                dialogInterface.dismiss();
                            }
                        });
                        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.container_layout_cliente, home).
                                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                                nav.setSelectedItemId(R.id.inicio_cliente);
                                dialogInterface.dismiss();
                            }
                        });
                        alert.show();
                        break;
                }
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.container_layout_cliente, home).commit();
    }
}