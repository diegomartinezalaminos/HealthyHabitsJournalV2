package com.example.healthyhabits.models;

public class Diario {

    private String id;
    private String fecha;
    private String text;
    private int feel;

    public Diario() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFeel() {
        return feel;
    }

    public void setFeel(int feel) {
        this.feel = feel;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        String feelformat = "";
        if (feel <= 30){
            feelformat = "Triste";
        }else if (feel > 30 && feel < 40){
            feelformat = "Normal";
        } else if (feel >= 40){
            feelformat = "Feliz";
        }
        return fecha + "\n" + text + "\n" + "Puntuacion felicidad: " + feel + " (" + feelformat + ")";
    }
}
