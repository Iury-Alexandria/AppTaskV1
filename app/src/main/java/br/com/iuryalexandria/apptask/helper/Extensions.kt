package br.com.iuryalexandria.apptask.helper

import android.graphics.Color


import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import br.com.iuryalexandria.apptask.R
import br.com.iuryalexandria.apptask.databinding.BottonSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.initToolbar(toolbar: Toolbar){

    (activity as AppCompatActivity).setSupportActionBar(toolbar)
    (activity as AppCompatActivity).title = ""
    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
}

fun Fragment.showBottonSheet(
    titleDialog: Int? = null,
    titleButton: Int? = null,
    message: Int,
    onClick: () -> Unit = {}
){

    val bottomSheetDialog =  BottomSheetDialog(requireContext(), R.style.ButtonSheetDialog)
    val bottomSheetBinding: BottonSheetBinding = BottonSheetBinding.inflate(layoutInflater, null, false)

    bottomSheetBinding.textTitle.text = getText(titleDialog ?: R.string.txt_title_bt_sheet)
    bottomSheetBinding.btButtonSheet.text = getText(titleButton ?:  R.string.txt_button_bt_sheet)
    bottomSheetBinding.textDescription.text = getText(message)
    bottomSheetBinding.btButtonSheet.setOnClickListener {
        onClick()
        bottomSheetDialog.dismiss()
    }

    bottomSheetDialog.setContentView(bottomSheetBinding.root)
    bottomSheetDialog.show()

}