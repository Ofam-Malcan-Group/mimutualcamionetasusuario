package com.ofam.mimutualcamionetasusuario.entities;

import java.io.Serializable;

public class Service implements Serializable {
   private int codigoRespuesta;
    String mensajeRespuesta;
    String idServicio;
    String idUsuario;
    String idConductor;
    String dirOrigen;
    String dirDestino;
    String lonOrigen;
    String latOrigen;
    String lonDestino;
    String latDestino;
    String estado;
    String valor;
    String placa;
    String marca;
    String nombreConductor;
    String fotoConductor;
    String nombreUsuario;
    String fotoUsuario;
    String fechaServicio;
    String barrioOrigen;
    String tcsCalificacion;

    public int getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public String getMensajeRespuesta() {
        return mensajeRespuesta;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public String getIdConductor() {
        return idConductor;
    }

    public String getDirOrigen() {
        return dirOrigen;
    }

    public String getDirDestino() {
        return dirDestino;
    }

    public String getLonOrigen() {
        return lonOrigen;
    }

    public String getLatOrigen() {
        return latOrigen;
    }

    public String getLonDestino() {
        return lonDestino;
    }

    public String getLatDestino() {
        return latDestino;
    }

    public String getEstado() {
        return estado;
    }

    public String getValor() {
        return valor;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public String getFotoConductor() {
        return fotoConductor;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public String getFechaServicio() {
        return fechaServicio;
    }

    public String getBarrioOrigen() {
        return barrioOrigen;
    }

    public String getTcsCalificacion() {
        return tcsCalificacion;
    }
}
