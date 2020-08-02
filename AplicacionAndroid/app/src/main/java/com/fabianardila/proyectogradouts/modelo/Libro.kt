package com.fabianardila.proyectogradouts.modelo

import java.io.Serializable

class Libro : Serializable{

    var id: String? = null
    var titulo: String? = null
    var autores: String? = null
    var fechaPublicacion: String? = null
    var edicion: String? = null
    var isbn: List<String>? = null
    var otrosTitulos: List<String>? = null
    var idioma: String? = null
    var categoria: String? = null
    var disponibles = 0
    var imageUrl: String? = null
    var status: List<Map<String, String>>? = null

    constructor(
        id: String?,
        titulo: String?,
        autores: String?,
        fechaPublicacion: String?,
        edicion: String?,
        isbn: List<String>?,
        otrosTitulos: List<String>?,
        idioma: String?,
        categoria: String?,
        disponibles: Int,
        imageUrl: String?,
        status: List<Map<String, String>>?
    ) {
        this.id = id
        this.titulo = titulo
        this.autores = autores
        this.fechaPublicacion = fechaPublicacion
        this.edicion = edicion
        this.isbn = isbn
        this.otrosTitulos = otrosTitulos
        this.idioma = idioma
        this.categoria = categoria
        this.disponibles = disponibles
        this.imageUrl = imageUrl
        this.status = status
    }

    constructor() {}

}