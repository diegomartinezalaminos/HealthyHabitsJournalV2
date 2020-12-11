package com.example.healthyhabits;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    //creamos las varibles de los componeneste del layout (activity_login.xml)
    private EditText editText_LoginUserEmail;
    private EditText editText_LoginUserPassword;
    private Button button_LoginUserLogin;
    private Button button_LoginUserRegister;

    //Varibles de datos que vamos a logear
    private String email = "";
    private String password = "";

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase instancia
        mAuth = FirebaseAuth.getInstance();

        //Inicializamos las varibles de los componentes layout
        editText_LoginUserEmail = (EditText) findViewById(R.id.editText_LoginUserEmail);
        editText_LoginUserPassword = (EditText) findViewById(R.id.editText_LoginUserPassword);
        button_LoginUserLogin = (Button) findViewById(R.id.button_LoginUserLogin);
        button_LoginUserRegister = (Button) findViewById(R.id.button_LoginUserRegister);



        //Creamos los listener para los dos botones
        button_LoginUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editText_LoginUserEmail.getText().toString();
                password = editText_LoginUserPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()){
                    doLoginUser();
                }else{
                    Toast.makeText(LoginActivity.this, "Tiene que completar los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Nos manda a la actividad RegisterActivity
        button_LoginUserRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void doLoginUser(){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Comprobamos que ha salido bien
                if (task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(LoginActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //En el caso que el usuario siga conectado lo mandamos al MainActivity directamen ya que no se tiene que loguear
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
