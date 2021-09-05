package com.chametla.localizacioncombitizayuca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button botonInciar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonInciar = findViewById(R.id.iniciar);

        botonInciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSelectOptionLogin();
            }
        });

    }

    private void goToSelectOptionLogin() {
        Intent intent = new Intent(MainActivity.this, SelectOptionLogin.class);
        startActivity(intent);
    }
}