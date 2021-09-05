package com.chametla.localizacioncombitizayuca;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;


public class SelectOptionLogin extends AppCompatActivity {
    Button ToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_login);

        ToLogin = findViewById(R.id.bInciarSesion);
        ToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(SelectOptionLogin.this, UserLogin.class);
        startActivity(intent);
    }
}