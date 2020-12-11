package com.example.healthyhabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.healthyhabits.models.Diario;
import com.example.healthyhabits.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText editText_RegisterUserName;
    private EditText editText_RegisterUserEmail;
    private EditText editText_RegisterUserPassword1;
    private EditText editText_RegisterUserPassword2;
    private Button button_RegisterUserRegister;


    //Instanciamos el obb Ususario
    private Usuario usuario = new Usuario();

    //Variables de los datos que queremos registrar

    private String user = "";
    private String email = "";
    private String password = "";

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference mdbase;



    public RegisterActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Instanciamos el metodo de autenticacion de firebase
        mAuth = FirebaseAuth.getInstance();
        //Instanciamos la bbdd de firebase
        mdbase = FirebaseDatabase.getInstance().getReference(); //Hace referencia al nodo principal de la bbdd


        editText_RegisterUserName = (EditText) findViewById(R.id.editText_RegisterUserName);
        editText_RegisterUserEmail = (EditText) findViewById(R.id.editText_RegisterUserEmail);
        editText_RegisterUserPassword1 = (EditText) findViewById(R.id.editText_RegisterUserPassword1);
        editText_RegisterUserPassword2 = (EditText) findViewById(R.id.editText_RegisterUserPassword2);
        button_RegisterUserRegister = (Button) findViewById(R.id.button_RegisterUserRegister);



        button_RegisterUserRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = editText_RegisterUserName.getText().toString();
                email = editText_RegisterUserEmail.getText().toString();
                password = editText_RegisterUserPassword1.getText().toString();

                if (!user.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    if (password.equals(editText_RegisterUserPassword2.getText().toString())){
                        if (password.length() >= 6){
                            usuario.setUser(user);
                            usuario.setEmail(email);
                            usuario.setPassword(password);
                            registerUser();
                        }else{
                            Toast.makeText(RegisterActivity.this, "La contraseña tiene que tener 6 o mas caracteres", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegisterActivity.this, "Los dos contraseñas tiene que ser iguales", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Tienes que completar tododos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void  registerUser(){
        mAuth.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    //Obtenemos el id para el obb usuario
                    usuario.setId(mAuth.getCurrentUser().getUid());
                    //Creamos un hijo "Users" y dentro de user añadimos un hijo por id al que le añadimos los paramentros obtenidos en register
                    //Creamos un OnCompleteListener para comprobar que se han añadido los datos y ha salido bien, en cuyo caso nos mandara al MainActivity
                    mdbase.child("Users").child(usuario.getId()).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            }else {
                                Toast.makeText(RegisterActivity.this, "No se puedieron crear los datos en la bbdd", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    //Datos default diario
                    Diario defaultt = new Diario();
                    defaultt.setFecha("0/0/0");
                    defaultt.setText("default");
                    defaultt.setFeel(0);
                    defaultt.setId(UUID.randomUUID().toString());
                    mdbase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Diario").child(defaultt.getId()).setValue(defaultt).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });


                }else{
                    Toast.makeText(RegisterActivity.this, "No se puedo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}