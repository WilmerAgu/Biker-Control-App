package com.example.bikercontrol;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bikercontrol.oil.ListRegisterActivity;
import com.example.bikercontrol.oil.RegisterPartsActivity;

public class MenuActivity extends AppCompatActivity {
    private Button btnRegisterParts, btnlistParts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnRegisterParts = findViewById(R.id.btnRegisterParts);
        btnlistParts = findViewById(R.id.btnlistParts);

        btnRegisterParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MenuActivity.this, RegisterPartsActivity.class);
                startActivity(intent);
            }
        });

        btnlistParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ListRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}