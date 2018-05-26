package com.fishrungames.hallyu.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.*

object FileUtil {

    fun writeToFile(data: String, filename: String, context: Context) {
        try {
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readFromFile(fileName: String, context: Context): String {
        var fileData = ""
        try {
            val inputStream = context.openFileInput(fileName)
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                var receiveString = bufferedReader.readLine()
                while (receiveString != null) {
                    stringBuilder.append(receiveString)
                    receiveString = bufferedReader.readLine()
                }
                inputStream.close()
                fileData = stringBuilder.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fileData
    }

    fun getFilesList(context: Context): List<String> {
        val filesList: MutableList<String> = mutableListOf()
        val path = context.filesDir.toString()
        val directory = File(path)
        val files = directory.listFiles()
        for (file in files) {
            filesList.add(file.name)
        }
        return filesList
    }

    fun saveBitmapOnStorage(context: Context, filename: String, bitmap: Bitmap): Boolean {
        val folderToSave = context.filesDir.toString()
        val file = File(folderToSave, filename)
        try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

}