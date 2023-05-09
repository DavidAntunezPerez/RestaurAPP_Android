package com.example.restaurapp.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.restaurapp.R

class CreateComFragment : Fragment() {

    private lateinit var dataPasser: OnDataPass

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_com, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleEditText = view.findViewById<EditText>(R.id.txtInputTitle)
        val descriptionEditText = view.findViewById<EditText>(R.id.txtInputDescr)

        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val title = s.toString()
                val description = descriptionEditText.text.toString()
                dataPasser.onDataPass(title, description)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val title = titleEditText.text.toString()
                val description = s.toString()
                dataPasser.onDataPass(title, description)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    interface OnDataPass {
        fun onDataPass(title: String, description: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }
}
