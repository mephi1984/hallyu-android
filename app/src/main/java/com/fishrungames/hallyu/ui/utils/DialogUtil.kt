package com.fishrungames.hallyu.ui.utils

import android.app.AlertDialog
import android.content.Context

object DialogUtil {

    fun showAlertDialog(context: Context, message: String){
        val dialog = AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", { dialog, _ ->  dialog.dismiss() })
                .create()
        dialog.show()
    }

}