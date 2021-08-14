package com.abdullah.cooking;

import java.io.Serializable;

public class Cooking implements Serializable {

    private String generatedId;
    private String name;
    private String quantity;
    private String production;

    public Cooking(String generatedId, String name, String quantity, String production) {
        this.generatedId = generatedId;
        this.name = name;
        this.quantity = quantity;
        this.production = production;
    }

    public String getGeneratedId() {
        return generatedId;
    }

    public void setGeneratedId(String generatedId) {
        this.generatedId = generatedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }
}
