package com.itch.tutorias.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero_identificacion", nullable = false, length = 30, unique = true)
    private String numeroIdentificacion;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "correo", nullable = false, length = 120, unique = true)
    private String correo;

    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String contrasenaHash;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_perfil",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "perfil_id")
    )
    private Set<Perfil> perfiles = new HashSet<>();

    @Column(name = "foto_perfil", length = 255)
    private String fotoPerfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoUsuario estado = EstadoUsuario.activo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public boolean hasPerfil(String nombrePerfil) {
        return perfiles.stream().anyMatch(p -> p.getNombre().equalsIgnoreCase(nombrePerfil));
    }

    public String getPrimerPerfil() {
        if (perfiles != null && !perfiles.isEmpty()) {
            return perfiles.iterator().next().getNombre();
        }
        return "";
    }

    public enum EstadoUsuario {
        activo, inactivo
    }

    public Usuario() {}

    //Getters
    public Integer getId() { return id; }
    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreo() { return correo; }
    public String getContrasenaHash() { return contrasenaHash; }
    public Set<Perfil> getPerfiles() { return perfiles; }
    public String getFotoPerfil() { return fotoPerfil; }
    public EstadoUsuario getEstado() { return estado; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setNumeroIdentificacion(String numeroIdentificacion) { this.numeroIdentificacion = numeroIdentificacion; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }
    public void setPerfiles(Set<Perfil> perfiles) { this.perfiles = perfiles; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", numero_identificacion='" + numeroIdentificacion +
               "', nombre_completo='" + nombreCompleto + "', estado=" + estado + "}";
    }
}
