package com.depi.myapplicatio.util.viewsUtil

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.depi.myapplicatio.R

fun Fragment.showDialogue(
    context: Context,
    title: String,
    message: String = "",
    alertIcon: Int = R.drawable.ic_kleine_shape,
    negativeButtonText: String,
    positiveButtonText: String,
    isCancelable: Boolean = true,
    onNegativeAction: (AlertDialog) -> Unit,
    onPositiveAction: (AlertDialog) -> Unit,
) {
    val builder = AlertDialog.Builder(context)
    var alertDialog: AlertDialog? = null
    builder.setTitle(title)
        .setMessage(message)
        .setIcon(alertIcon)
        .setNegativeButton(negativeButtonText) { _, _ ->
            alertDialog?.dismiss()
            onNegativeAction(alertDialog!!)
        }
        .setPositiveButton(positiveButtonText) { _, _ ->
            alertDialog?.dismiss()
            onPositiveAction(alertDialog!!)
        }
        .setCancelable(isCancelable)

    alertDialog = builder.create()
    alertDialog.show()

}