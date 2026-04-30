package com.itch.tutorias.service;

import org.springframework.web.multipart.MultipartFile;

public interface IServicioAlmacenamiento {
    /// Guarda el archivo en la subcarpeta especificada y devuelve la ruta o URL del archivo guardado
    String guardar(MultipartFile archivo, String subcarpeta);

    /**
     * Elimina el archivo indicado de la subcarpeta.
     */
    void eliminar(String nombreArchivo, String subcarpeta);
}
