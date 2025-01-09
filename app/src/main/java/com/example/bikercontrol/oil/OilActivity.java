package com.example.bikercontrol.oil;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OilActivity extends AppCompatActivity {

    private EditText etOilChange, etKilometer, etOilBrand, etNextOilChange;
    private Spinner spTypeOil;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_oil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializar vistas
        spTypeOil = findViewById(R.id.spTypeOil);
        etOilChange = findViewById(R.id.etOilChange);
        etKilometer = findViewById(R.id.etKilometer);
        etOilBrand = findViewById(R.id.etOilBrand);
        etNextOilChange = findViewById(R.id.etNextOilChange);
        btnSave = findViewById(R.id.btnSave);




        //configurar el Spinner tipo de aceite

        String[] oilType ={"","Mineral", "Semisintiteco", "Sintetico" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, oilType);
        spTypeOil.setAdapter(adapter);

        spTypeOil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String oilTypeSelectecd = parent.getItemAtPosition(position).toString();


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        // Configurar el campo de fecha
        etOilChange.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, month1, dayOfMonth) -> {
                        // Formatear la fecha y establecerla en el EditText
                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        etOilChange.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        etNextOilChange.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, month1, dayOfMonth) -> {
                        // Formatear la fecha y establecerla en el EditText
                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        etNextOilChange.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Accion del boton guardar

        btnSave.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            OilDao oilDao = new OilDao(db);
            OilModel oil = new OilModel();

            oil.setOilBrand(etOilBrand.getText().toString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            try {
                // Convertir las fechas
                String oilChangeText = etOilChange.getText().toString();
                String nextOilChangeText = etNextOilChange.getText().toString();

                Date oilChangeDate = dateFormat.parse(oilChangeText);
                Date nextOilChangeDate = dateFormat.parse(nextOilChangeText);

                if (oilChangeDate == null || nextOilChangeDate == null) {
                    Toast.makeText(this, "Por favor ingresa fechas válidas.", Toast.LENGTH_SHORT).show();
                    return;
                }

                oil.setOilChange(oilChangeDate);
                oil.setNextOilChange(nextOilChangeDate);

                // Convertir el kilometraje
                String kilometerText = etKilometer.getText().toString();
                if (kilometerText.isEmpty()) {
                    Toast.makeText(this, "Por favor ingresa el kilometraje.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double kilometerValue = Double.parseDouble(kilometerText);
                oil.setKilometer(kilometerValue);

                // Obtener el valor seleccionado del Spinner
                String oilTypeSelected = spTypeOil.getSelectedItem().toString();
                oil.setTypeOil(oilTypeSelected);

                // Guardar los datos en Firestore
                oilDao.insert(oil, new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Toast.makeText(OilActivity.this, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show();
                        // Limpia los campos o realiza otra acción
                    }
                });

            } catch (ParseException e) {
                Toast.makeText(this, "Error al convertir las fechas. Usa el formato dd/MM/yyyy.", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error en el kilometraje. Ingresa un valor numérico.", Toast.LENGTH_SHORT).show();
            }

            // Limpiar los campos después de guardar
            etOilChange.setText("");
            etKilometer.setText("");
            etOilBrand.setText("");
            spTypeOil.setSelection(0);
            etNextOilChange.setText("");
        });




    }
}