package br.senai.sp.emendaai.model;

import com.google.gson.annotations.SerializedName;

public class Endereco {

    @SerializedName("cep")
    private String cep;

    @SerializedName("state")
    private String state;

    @SerializedName("city")
    private String city;

    @SerializedName("neighborhood")
    private String neighborhood;

    @SerializedName("street")
    private String street;

    @SerializedName("service")
    private String service;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
}
