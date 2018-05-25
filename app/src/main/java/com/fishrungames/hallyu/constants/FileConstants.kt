package com.fishrungames.hallyu.constants

object FileConstants {

    private val FILE_COMICS_DATA: String = "fileComicsData"

    fun getComicsFilename(comicsId: String): String {
        return FILE_COMICS_DATA + comicsId
    }

}