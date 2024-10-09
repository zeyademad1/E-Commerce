package com.depi.myapplicatio.dialog

import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.fragment.app.Fragment
import com.depi.myapplicatio.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)

    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancelResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.buttonsendResetPassword)

    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }

    buttonSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }
}
