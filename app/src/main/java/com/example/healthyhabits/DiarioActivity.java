package com.example.healthyhabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.healthyhabits.models.Diario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class DiarioActivity extends AppCompatActivity {

    private EditText editText_DiarioNota;
    private Button button_DiarioGuardar;
    private Button button_DiarioCancelar;
    private SeekBar seekBar_DiarioFeel;


    private String text1 = "";
    private int seekBarValue = 0;


    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mdbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diario);

        //Instanciamos el metodo de autenticacion de firebase
        mAuth = FirebaseAuth.getInstance();
        //Instanciamos la bbdd de firebase
        mdbase = FirebaseDatabase.getInstance().getReference();


        editText_DiarioNota = (EditText) findViewById(R.id.editText_DiarioNota);
        button_DiarioGuardar = (Button) findViewById(R.id.button_DiarioGuardar);
        seekBar_DiarioFeel = (SeekBar) findViewById(R.id.seekBar_DiarioFeel);
        button_DiarioCancelar = (Button) findViewById(R.id.button_DiarioCancelar);

        editText_DiarioNota.computeScroll();

        //No entiendo por que no pilla el texto la varible text1 :( y no guada dicho campo luego en la bbdd
        text1 = editText_DiarioNota.getText().toString();

        Intent x = getIntent();
        Bundle bundlex = x.getExtras();
        if (bundlex.getString("editar").equals("si")){
            Intent c = getIntent();
            Bundle bundlec = c.getExtras();
            editText_DiarioNota.setText(bundlec.getString("text"));
        }

        button_DiarioGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = getIntent();
                Bundle bundle = i.getExtras();
                if(bundle.getString("editar").equals("si")){
                    editDiario();
                }else{
                    addDiario();
                }
            }
        });

        button_DiarioCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DiarioActivity.this, MainActivity.class));
                finish();
            }
        });

        //SeekBar
        seekBar_DiarioFeel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void addDiario(){

        //Recuperamos la varible fecha
        Intent a = getIntent();
        Bundle bundle = a.getExtras();
        String fecha = bundle.getString("fecha");

        Diario diario = new Diario();
        diario.setFecha(fecha);
        diario.setText(text1);
        diario.setFeel(seekBarValue);
        diario.setId(UUID.randomUUID().toString());
        mdbase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Diario").child(diario.getId()).setValue(diario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(DiarioActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(DiarioActivity.this, "Ha ocurrido un error a la hora de añadir los datos a la bbdd", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void editDiario(){

        //Recuperamos la varible fecha
        Intent a = getIntent();
        Bundle bundle = a.getExtras();
        String fecha = bundle.getString("fecha");

        Intent b = getIntent();
        Bundle bundleb = a.getExtras();


        Diario diario = new Diario();
        diario.setFecha(fecha);
        diario.setText(text1);
        diario.setFeel(seekBarValue);
        diario.setId(bundleb.getString("id"));
        mdbase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Diario").child(diario.getId()).setValue(diario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(DiarioActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(DiarioActivity.this, "Ha ocurrido un error a la hora de añadir los datos a la bbdd", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}