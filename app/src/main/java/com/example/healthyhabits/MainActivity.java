package com.example.healthyhabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.healthyhabits.models.Diario;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Boolean exist;

    //Calendario
    private CalendarView cal;
    //private String fecha = "";

    //Firebas
    //Creamos un obb firebaseOut para poder desconectarse
    private FirebaseAuth mAusth;
    private DatabaseReference mDatabase;

    // Cuadro de dialogo de Material Design
    private MaterialAlertDialogBuilder madb;

    //Varibles donde se guardan los datos de la bbdd
    private String user = "";
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instanciamos obb Firebas
        mAusth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Calendario
        cal = (CalendarView) findViewById(R.id.calendarView);

        //llamamos al metodo para que nos de los datos
        getUserInfo();

        cal.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
            Toast.makeText(MainActivity.this, fecha, Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(MainActivity.this, DiarioActivity.class).putExtra("fecha", fecha));
            addDeleteEditDiario(fecha);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflador = getMenuInflater();
        inflador.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_loginOut:
                mAusth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.menu_profile:
                // Se crea el cuadro de diálogo de Material Design y se le asigna titulo, mensaje..
                madb = new MaterialAlertDialogBuilder(this);
                madb.setTitle("Profile:");
                madb.setMessage("User: " + user + " \n Email: " + email);
                // Para que la ventana desaparezca al pulsar fuera de ella
                madb.setCancelable(true | false);
                madb.create().show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void siguiente(View view){
        Intent prueba = new Intent(this, LoginActivity.class);
        startActivity(prueba);
    }

    private void getUserInfo(){
        //Obtenemos el id del usuario con el que iniciamos sesión
        String id = mAusth.getCurrentUser().getUid();
        mDatabase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    user = snapshot.child("user").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addDeleteEditDiario(String fecha){
        exist = false;
        Diario diarioMain = new Diario();
        mDatabase.child("Users").child(mAusth.getCurrentUser().getUid()).child("Diario").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot obbSnapshot: snapshot.getChildren()){
                    Diario diario = obbSnapshot.getValue(Diario.class);
                    if (diario.getFecha().equals(fecha)){
                        exist = true;
                        // Se crea el cuadro de diálogo de Material Design y se le asigna titulo, mensaje..
                        madb = new MaterialAlertDialogBuilder(MainActivity.this);
                        madb.setTitle("Diario:");
                        madb.setMessage(diario.toString());
                        // Para que la ventana desaparezca al pulsar fuera de ella
                        madb.setCancelable(true | false);

                        madb.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(MainActivity.this, DiarioActivity.class)
                                        .putExtra("editar", "si")
                                        .putExtra("id", diario.getId())
                                        .putExtra("text", diario.getText())
                                        .putExtra("fecha", diario.getFecha())
                                        .putExtra("feel", diario.getFeel())
                                );
                            }
                        });
                        madb.setNegativeButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.child("Users").child(mAusth.getCurrentUser().getUid()).child("Diario").child(diario.getId()).removeValue();
                                Toast.makeText(MainActivity.this, "Eliminando...", Toast.LENGTH_SHORT).show();
                            }
                        });
                        madb.create().show();
                    }

                }
                //No entiendo por que no cambia el valor booleano de exist
                if (!exist){
                    startActivity(new Intent(MainActivity.this, DiarioActivity.class).putExtra("fecha", fecha).putExtra("editar", "no"));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}