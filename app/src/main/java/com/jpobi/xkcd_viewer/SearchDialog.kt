package com.jpobi.xkcd_viewer

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.jpobi.xkcd_viewer.databinding.SearchDialogBinding

class SearchDialog(private val OnSubmitClickListener:(comicId : String) -> Unit) : DialogFragment() {
    private lateinit var binding: SearchDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding=SearchDialogBinding.inflate(LayoutInflater.from(context))
        val builder =AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        var inputView=binding.dialogComicIdInput

        //TODO: chequear q sea un num y q sea menor al ultimo comic creado
        binding.dialogSearchButton.setOnClickListener{
            var inputValue=inputView.text.toString()
            OnSubmitClickListener.invoke(inputValue)
        }

        val dialog= builder.create()
//        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }






}