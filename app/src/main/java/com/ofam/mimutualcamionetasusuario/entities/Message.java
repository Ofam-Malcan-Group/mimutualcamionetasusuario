package com.ofam.mimutualcamionetasusuario.entities;

public class Message {
    private String mensaje;
    private String nombre;
    private boolean taxi;
    private boolean read;
    private Long hora;

    public Message() {
        //NotUsed
    }

    public Message(String mensaje, String nombre, boolean isTaxi, Long hora) {
        this.mensaje = mensaje;
        this.nombre = nombre;
        this.taxi = isTaxi;
        this.read = false;
        this.hora = hora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isTaxi() {
        return taxi;
    }

    public boolean isRead() {
        return read;
    }

    public Long getHora() {
        return hora;
    }
}
