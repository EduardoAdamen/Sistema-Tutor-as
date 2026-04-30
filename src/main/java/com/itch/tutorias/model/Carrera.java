package com.itch.tutorias.model;

import jakarta.persistence.*;

@Entity
@Table(name = "carrera")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 120, unique = true)
    private String nombre;

    @Column(name = "clave", nullable = false, length = 20, unique = true)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCarrera estado = EstadoCarrera.activa;

    public enum EstadoCarrera {
        activa, inactiva
    }

    public Carrera() {}

    //Getters
    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public String getClave() { return clave; }
    public EstadoCarrera getEstado() { return estado; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setClave(String clave) { this.clave = clave; }
    public void setEstado(EstadoCarrera estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Carrera{id=" + id + ", nombre='" + nombre + "', clave='" + clave + "', estado=" + estado + "}";
    }
}
