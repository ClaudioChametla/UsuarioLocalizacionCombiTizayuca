package com.chametla.localizacioncombitizayuca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserLogin extends AppCompatActivity {

    TextInputEditText TextInputEmail;
    TextInputEditText TextInputPassword;
    Button botonLogin;

    FirebaseAuth mAtuh;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        TextInputEmail = findViewById(R.id.textInputEmail);
        TextInputPassword = findViewById(R.id.textInputPassword);
        botonLogin = findViewById(R.id.bLogin);

        mAtuh = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        String email = TextInputEmail.getText().toString();
        String password = TextInputPassword.getText().toString();

        if(email.isEmpty() && !password.isEmpty()){
            if (password.length() >= 6){
                mAtuh.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(UserLogin.this, "Login Realizado con Exito",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(UserLogin.this, "La correo o la contraseña son incorrectos",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        }
    }
}