package com.fishrungames.hallyu.utils

import android.content.Context
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.InputStreamReader

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

}