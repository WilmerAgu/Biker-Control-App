package com.example.bikercontrol;

import android.content.Intent;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditOilRegisterActivity extends AppCompatActivity {
    private EditText etOilChange, etKilometer, etOilBrand, etNextOilChange;
    private Spinner spTypeOil;
    private Button btnDelete, btnUpDate;

    private OilModel selectedRegister;
    private String selectedRegisterId;
    private OilDao oilDao;

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
        btnUpDate = findViewById(R.id.btnUpDate);

        // Inicializar DAO
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        oilDao = new OilDao(db);

        // Obtener datos del Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedRegisterId = extras.getString("id");

            if (selectedRegisterId == null || selectedRegisterId.isEmpty()) {
                Toast.makeText(this, "ID del registro no válido", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Cargar datos en los campos
            cargarDatos(extras);
        }

        // Botón para actualizar registro
        btnUpDate.setOnClickListener(view -> actualizarRegistro());

        // Botón para eliminar registro
        btnDelete.setOnClickListener(view -> eliminarRegistro());
    }

    private void cargarDatos(Bundle extras) {
        try {
            String oilChangeString = extras.getString("oilChange", "");
            String nextOilChangeString = extras.getString("nextOilChange", "");
            double kilometer = extras.getDouble("kilometer", 0.0);
            String oilBrand = extras.getString("oilBrand", "");
            String typeOil = extras.getString("typeOil", "");

            // Formateadores para manejar fechas
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Configurar fechas
            if (!oilChangeString.isEmpty()) {
                Date oilChangeDate = inputFormat.parse(oilChangeString);
                etOilChange.setText(outputFormat.format(oilChangeDate));
            }

            if (!nextOilChangeString.isEmpty()) {
                Date nextOilChangeDate = inputFormat.parse(nextOilChangeString);
                etNextOilChange.setText(outputFormat.format(nextOilChangeDate));
            }

            // Configurar otros valores
            etKilometer.setText(String.valueOf(kilometer));
            etOilBrand.setText(oilBrand);

            // Configurar selección del Spinner
            setSpinnerSelection(spTypeOil, typeOil);

            // Cargar el registro completo desde la base de datos
            oilDao.getById(selectedRegisterId, register -> {
                if (register != null) {
                    selectedRegister = register;
                } else {
                    Toast.makeText(this, "No se pudo recuperar el registro", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar los datos del registro", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarRegistro() {
        if (selectedRegister == null) {
            Toast.makeText(this, "Por favor, espera a que se cargue el registro.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Obtener valores
            String oilChangeString = etOilChange.getText().toString().trim();
            String nextOilChangeString = etNextOilChange.getText().toString().trim();
            String kilometerString = etKilometer.getText().toString().trim();
            String oilBrand = etOilBrand.getText().toString().trim();
            String typeOil = spTypeOil.getSelectedItem().toString().trim();

            // Validar campos
            if (oilChangeString.isEmpty() || nextOilChangeString.isEmpty() || kilometerString.isEmpty() || oilBrand.isEmpty() || typeOil.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parsear fechas y kilometraje
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date oilChangeDate = dateFormat.parse(oilChangeString);
            Date nextOilChangeDate = dateFormat.parse(nextOilChangeString);
            double kilometer = Double.parseDouble(kilometerString);

            // Actualizar modelo
            selectedRegister.setOilChange(oilChangeDate);
            selectedRegister.setNextOilChange(nextOilChangeDate);
            selectedRegister.setKilometer(kilometer);
            selectedRegister.setOilBrand(oilBrand);
            selectedRegister.setTypeOil(typeOil);

            // Guardar cambios en la base de datos
            oilDao.update(selectedRegisterId, selectedRegister, isSuccess -> {
                if (isSuccess) {
                    Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Error al actualizar el registro", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (ParseException | NumberFormatException e) {
            Toast.makeText(this, "Error en los datos ingresados", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarRegistro() {
        if (selectedRegisterId == null || selectedRegisterId.isEmpty()) {
            Toast.makeText(this, "ID del registro no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        oilDao.delete(selectedRegisterId, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error al eliminar el registro", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        boolean found = false;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                found = true;
                break;
            }
        }
        if (!found) {
            Toast.makeText(this, "Valor no válido para el Spinner: " + value, Toast.LENGTH_SHORT).show();
        }
    }
}
