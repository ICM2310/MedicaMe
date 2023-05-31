package javeriana.edu.co.medicameapp.modelos;

import com.google.android.gms.maps.model.LatLng;

public class Order {
    private String usuarioSoliciante;
    private String usuarioRepartidor;
    private LatLng ubicacion;
    private String estado;

    public Order(String usuarioSoliciante, String usuarioRepartidor, LatLng ubicacion, String estado) {
        this.usuarioSoliciante = usuarioSoliciante;
        this.usuarioRepartidor = usuarioRepartidor;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    public Order() {
    }

    public String getUsuarioSoliciante() {
        return usuarioSoliciante;
    }

    public void setUsuarioSoliciante(String usuarioSoliciante) {
        this.usuarioSoliciante = usuarioSoliciante;
    }

    public String getUsuarioRepartidor() {
        return usuarioRepartidor;
    }

    public void setUsuarioRepartidor(String usuarioRepartidor) {
        this.usuarioRepartidor = usuarioRepartidor;
    }

    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


}
