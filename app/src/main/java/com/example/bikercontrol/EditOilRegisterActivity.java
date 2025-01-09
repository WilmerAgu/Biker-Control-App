package com.example.bikercontrol;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bikercontrol.data.dao.OilDao;
import com.example.bikercontrol.data.model.OilModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditOilRegisterActivity extends AppCompatActivity {
    private EditText etOilChange, etKilometer, etOilBrand, etNextOilChange;
    private Spinner spTypeOil;
    private Button btnDelete, btnUpdate;

    private OilDao oilDao;
    private String selectedRegisterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_oil_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        etOilChange = findViewById(R.id.etOilChange);
        etKilometer = findViewById(R.id.etKilometer);
        etOilBrand = findViewById(R.id.etOilBrand);
        etNextOilChange = findViewById(R.id.etNextOilChange);
        spTypeOil = findViewById(R.id.spTypeOil);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Inicializar DAO
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        oilDao = new OilDao(db);

        // Configurar Spinner
        configurarSpinner();

        // Obtener los datos pasados desde OilChangeActivity
        Intent intent = getIntent();
        selectedRegisterId = intent.getStringExtra("id");

        if (selectedRegisterId != null) {
            // Cargar los datos en los campos
            cargarDatos(intent);
        }

        // Botón para actualizar
        btnUpdate.setOnClickListener(view -> actualizarRegistro());

        // Botón para eliminar registro
        btnDelete.setOnClickListener(view -> eliminarRegistro());
    }

    private void configurarSpinner() {
        // Cargar las opciones del Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_oil_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeOil.setAdapter(adapter);
    }

    private void cargarDatos(Intent intent) {
        // Formato de fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Establecer los datos en los campos
        etOilChange.setText(dateFormat.format(new Date(intent.getLongExtra("oilChange", 0))));
        etKilometer.setText(String.valueOf(intent.getDoubleExtra("kilometer", 0.0)));
        etOilBrand.setText(intent.getStringExtra("oilBrand"));
        etNextOilChange.setText(dateFormat.format(new Date(intent.getLongExtra("nextOilChange", 0))));

        // Seleccionar el tipo de aceite en el Spinner
        String typeOil = intent.getStringExtra("typeOil");
        if (typeOil != null) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spTypeOil.getAdapter();
            int position = adapter.getPosition(typeOil);
            spTypeOil.setSelection(position);
        }
    }

    private void actualizarRegistro() {
        if (selectedRegisterId == null || selectedRegisterId.isEmpty()) {
            Toast.makeText(this, "ID del registro no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un nuevo modelo con los datos del formulario
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date oilChangeDate = dateFormat.parse(etOilChange.getText().toString());
            Date nextOilChangeDate = dateFormat.parse(etNextOilChange.getText().toString());

            OilModel updatedOil = new OilModel(
                    selectedRegisterId,
                    oilChangeDate,
                    Double.parseDouble(etKilometer.getText().toString()),
                    etOilBrand.getText().toString(),
                    spTypeOil.getSelectedItem().toString(),
                    nextOilChangeDate
            );

            // Actualizar el registro en la base de datos
            oilDao.update(updatedOil, isSuccess -> {
                if (isSuccess) {
                    Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Finalizar actividad si la actualización es exitosa
                } else {
                    Toast.makeText(this, "Error al actualizar el registro", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error en los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarRegistro() {
        oilDao.delete(selectedRegisterId, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al eliminar el registro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
