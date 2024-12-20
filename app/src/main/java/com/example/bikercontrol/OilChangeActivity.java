package com.example.bikercontrol;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikercontrol.data.adapter.OilAdapter;
import com.example.bikercontrol.data.dao.OilDao;
import com.google.firebase.firestore.FirebaseFirestore;

public class OilChangeActivity extends AppCompatActivity {
    private RecyclerView rvOilChange;
    private OilAdapter oilAdapter;
    private OilDao oilDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_oil_change);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvOilChange = findViewById(R.id.rvOilChange);
        rvOilChange.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        oilDao = new OilDao(db);

        cargarRegistros();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRegistros(); // Refrescar la lista de facturas al volver
    }

    private void cargarRegistros() {
        oilDao.getAllOilRegister(oilRegistros -> {
            if (oilRegistros != null && !oilRegistros.isEmpty()) {
                oilAdapter = new OilAdapter(oilRegistros);
                rvOilChange.setAdapter((oilAdapter));

                oilAdapter.setOnItemClickListener(oilRegistro -> {
                    Intent intent = new Intent(OilChangeActivity.this, OilChangeActivity.class);
                    intent.putExtra("id", oilRegistro.getId());
                    intent.putExtra("oilChange", oilRegistro.getOilChange());
                    intent.putExtra("kilometer", oilRegistro.getKilometer());
                    intent.putExtra("oilBrand", oilRegistro.getOilBrand());
                    intent.putExtra("typeOil", oilRegistro.getTypeOil());
                    intent.putExtra("nextOilChange", oilRegistro.getNextOilChange());
                    startActivity(intent);

                });
            }
        });
    }
}