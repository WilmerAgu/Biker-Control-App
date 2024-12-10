package com.example.bikercontrol.data.dao;

import android.util.Log;

import com.example.bikercontrol.data.model.OilModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OilDao {

    private static final String TAG = "OilDao";
    private static final String COLLECTION_NAME = "oilChange";
    private final FirebaseFirestore db;

    public OilDao(FirebaseFirestore db) { this.db = db;}


    //Metodo para insertar un nuevo registro
    public  void insert(OilModel oil, OnSuccessListener<String> listener) {
        Map<String, Object> oilData = mapOilToData(oil);

        db.collection(COLLECTION_NAME)
                .add(oilData)
                .addOnSuccessListener(documentReference -> {
                    Log.e(TAG, "Registro Insertado: " + documentReference.getId());
                    listener.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al insertar el registro", e);
                    listener.onSuccess(null);
                });
    }

    //Metodo para actualizar un registro existente
    public void update(String id, OilModel oil, OnSuccessListener<Boolean> listener) {
        if (id == null || id.isEmpty()) {
            Log.e(TAG, "ID de factura es nulo o vacío. No se puede actualizar.");
            if (listener != null) listener.onSuccess(false);
            return;
        }
        Map<String, Object> oilData = mapOilToData(oil);

        db.collection(COLLECTION_NAME)
                .document(id)
                .update(oilData)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Registro actualizado correctamente: " + id);
                    if (listener != null) listener.onSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar el registro: " + id, e);
                    if (listener != null) listener.onSuccess(false);
                });
    }

    // metodo para obtener un registro por id
    public void getById(String id, OnSuccessListener<OilModel> listener) {
        db.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            OilModel oil = document.toObject(OilModel.class);
                            listener.onSuccess(oil);
                        } else {
                            listener.onSuccess(null);
                        }
                    } else {
                        Log.e(TAG, "Error al obtener el registro: ", task.getException());
                        listener.onSuccess(null);
                    }
                });
    }

    //metodo para obtener todos los registros

    public void getAllFacturas(OnSuccessListener<List<OilModel>> listener) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<OilModel> facturaList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        OilModel oil = documentSnapshot.toObject(OilModel.class);
                        if (oil != null) {
                            oil.setId(documentSnapshot.getId());
                            facturaList.add(oil);
                        }
                    }
                    Log.d(TAG, "Registros cargados: " + facturaList.size());
                    listener.onSuccess(facturaList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al cargar registros", e);
                    listener.onSuccess(null);
                });
    }

    //metodo para eliminar factura por si ID
    public void delete(String id, OnSuccessListener<Boolean> listener) {
        db.collection(COLLECTION_NAME)
                .document(id)
                .delete()
                .addOnSuccessListener(unused -> listener.onSuccess(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al eliminar registro: ", e);
                    listener.onSuccess(false);
                });
    }


    // metodo para convertir iolModel aun Map<String, Object>
    private Map<String, Object> mapOilToData(OilModel oil) {
        Map<String, Object> oilData = new HashMap<>();
        oilData.put("oilChange", oil.getOilChange());
        oilData.put("kilometer", oil.getKilometer());
        oilData.put("oilBrand", oil.getOilBrand());
        oilData.put("typeOil", oil.getTypeOil());
        oilData.put("nextOilChange", oil.getNextOilChange());
        return oilData;
    }


}
