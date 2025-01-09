package com.example.bikercontrol.oil;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.bikercontrol.R;
import com.example.bikercontrol.data.dao.OilDao;
import com.example.bikercontrol.data.model.OilModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditOilRegisterActivity extends AppCompatActivity {
    private EditText etOilChange, etKilometer, etOilBrand, etNextOilChange;
    private Spinner spTypeOil;
    private Button btnDelete, btnUpdate;

    private OilDao oilDao;
    private String selectedRegisterId;
    private Calendar oilChangeCalendar, nextOilChangeCalendar;

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

        // Configurar los DatePickers para editar las fechas
        etOilChange.setOnClickListener(view -> mostrarDatePicker(true));
        etNextOilChange.setOnClickListener(view -> mostrarDatePicker(false));

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
        oilChangeCalendar = Calendar.getInstance();
        oilChangeCalendar.setTimeInMillis(intent.getLongExtra("oilChange", 0));
        etOilChange.setText(dateFormat.format(oilChangeCalendar.getTime()));

        nextOilChangeCalendar = Calendar.getInstance();
        nextOilChangeCalendar.setTimeInMillis(intent.getLongExtra("nextOilChange", 0));
        etNextOilChange.setText(dateFormat.format(nextOilChangeCalendar.getTime()));

        etKilometer.setText(String.valueOf(intent.getDoubleExtra("kilometer", 0)));
        etOilBrand.setText(intent.getStringExtra("oilBrand"));

        // Seleccionar el tipo de aceite en el Spinner
        String typeOil = intent.getStringExtra("typeOil");
        if (typeOil != null) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spTypeOil.getAdapter();
            int position = adapter.getPosition(typeOil);
            spTypeOil.setSelection(position);
        }
    }

    private void mostrarDatePicker(boolean isOilChange) {
        // Usar el DatePickerDialog para seleccionar una fecha
        Calendar calendar = isOilChange ? oilChangeCalendar : nextOilChangeCalendar;
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedYear, selectedMonth, selectedDay);

            // Actualizar el EditText con la fecha seleccionada
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate.getTime());

            if (isOilChange) {
                etOilChange.setText(formattedDate);
                oilChangeCalendar = selectedDate;
            } else {
                etNextOilChange.setText(formattedDate);
                nextOilChangeCalendar = selectedDate;
            }

        }, year, month, day);

        datePickerDialog.show();
    }

    private void actualizarRegistro() {
        if (selectedRegisterId == null || selectedRegisterId.isEmpty()) {
            Toast.makeText(this, "ID del registro no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un nuevo modelo con los datos del formulario
        try {
            // Obtener las fechas desde los campos de texto
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            Date oilChangeDate = dateFormat.parse(etOilChange.getText().toString());
            Date nextOilChangeDate = dateFormat.parse(etNextOilChange.getText().toString());

            // Crear el objeto OilModel
            OilModel updatedOil = new OilModel(
                    selectedRegisterId,
                    oilChangeDate,
                    Double.parseDouble(etKilometer.getText().toString()),
                    etOilBrand.getText().toString(),
                    spTypeOil.getSelectedItem().toString(),
                    nextOilChangeDate
            );

            // Verificar que los datos se estén asignando correctamente
            Log.d("EditOilRegister", "Updated Oil: " + updatedOil);

            oilDao.update(updatedOil, isSuccess -> {
                if (isSuccess) {
                    Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Finaliza la actividad si la actualización fue exitosa
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
