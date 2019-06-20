package com.xuber_for_services.provider.Models;

import java.io.Serializable;

public class SubServiceModel implements Serializable {

    private String name;

    public SubServiceModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
