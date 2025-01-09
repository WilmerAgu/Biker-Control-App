package com.example.bikercontrol.oil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bikercontrol.R;
import com.example.bikercontrol.data.adapter.OilAdapter;
import com.example.bikercontrol.data.dao.OilDao;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;

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
        cargarRegistros(); // Refrescar la lista al volver
    }

    private void cargarRegistros() {
        oilDao.getAllOilRegister(oilRegistros -> {
            if (oilRegistros != null && !oilRegistros.isEmpty()) {
                Collections.sort(oilRegistros, (o1, o2) -> o2.getOilChange().compareTo(o1.getOilChange()));
                oilAdapter = new OilAdapter(oilRegistros);
                rvOilChange.setAdapter(oilAdapter);

                oilAdapter.setOnItemClickListener(oilRegistro -> {
                    Intent intent = new Intent(OilChangeActivity.this, EditOilRegisterActivity.class);
                    // Pasar todos los datos necesarios al EditOilRegisterActivity
                    intent.putExtra("id", oilRegistro.getId());
                    intent.putExtra("oilChange", oilRegistro.getOilChange().getTime());
                    intent.putExtra("kilometer", oilRegistro.getKilometer());
                    intent.putExtra("oilBrand", oilRegistro.getOilBrand());
                    intent.putExtra("typeOil", oilRegistro.getTypeOil());
                    intent.putExtra("nextOilChange", oilRegistro.getNextOilChange().getTime());
                    startActivity(intent);
                });
            } else {
                Toast.makeText(this, "No hay registros disponibles", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
