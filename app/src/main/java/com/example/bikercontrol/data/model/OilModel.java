package com.example.bikercontrol.data.model;

import java.util.Date;

public class OilModel {

    private String id;
    private Date OilChange;
    private Double kilometer;
    private String oilBrand;
    private String typeOil;
    private Date NextOilChange;

    public OilModel() {
    }

    public OilModel(String id, Date oilChange, Double kilometer, String oilBrand, String typeOil, Date nextOilChange) {
        this.id = id;
        OilChange = oilChange;
        this.kilometer = kilometer;
        this.oilBrand = oilBrand;
        this.typeOil = typeOil;
        NextOilChange = nextOilChange;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getOilChange() {
        return OilChange;
    }

    public void setOilChange(Date oilChange) {
        OilChange = oilChange;
    }

    public Double getKilometer() {
        return kilometer;
    }

    public void setKilometer(Double kilometer) {
        this.kilometer = kilometer;
    }

    public String getOilBrand() {
        return oilBrand;
    }

    public void setOilBrand(String oilBrand) {
        this.oilBrand = oilBrand;
    }

    public Date getNextOilChange() {
        return NextOilChange;
    }

    public void setNextOilChange(Date nextOilChange) {
        NextOilChange = nextOilChange;
    }

    public String getTypeOil() {
        return typeOil;
    }

    public void setTypeOil(String typeOil) {
        this.typeOil = typeOil;
    }
}

