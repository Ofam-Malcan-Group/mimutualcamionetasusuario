package com.ofam.mimutualcamionetasusuario.entities;

import java.io.Serializable;

public class UserMiMutual implements Serializable {
    String id;
    String documento;
    String typeDoc;
    String nombre;
    String celular;
    String email;
    String fotoPerfil;
    String rol;

    public String getId() {
        return id;
    }

    public String getDocumento() {
        return documento;
    }

    public String getTypeDoc() {
        return typeDoc;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCelular() {
        return celular;
    }

    public String getEmail() {
        return email;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public String getRol() {
        return rol;
    }
}
