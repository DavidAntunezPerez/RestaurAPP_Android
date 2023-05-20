package com.example.restaurapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.restaurapp.R
import com.example.restaurapp.databinding.FragmentComListBinding
import com.example.restaurapp.databinding.FragmentEditComBinding
import com.example.restaurapp.entities.Command

class EditComFragment : Fragment() {

    private var _binding: FragmentEditComBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_COMMAND = "command"
        private const val ARG_TABLE_NUMBER = "table number"

        fun newInstance(command: Command?, tableNumber: Long?) = EditComFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_COMMAND, command)
                putLong(ARG_TABLE_NUMBER, tableNumber ?: 0)
            }
        }
    }

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditComBinding.inflate(inflater, container, false)
        rootView = binding.root

        // Apply touch interceptor to the root view
        rootView.setOnTouchListener { _, event ->
            // Consume touch events to prevent propagation to the underlying activity
            true
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RETRIEVE COMMAND ITEM
        val command: Command? = arguments?.getParcelable(ARG_COMMAND)
        val tableNumber: Long? = arguments?.getLong(ARG_TABLE_NUMBER)

        // BTN RETURN FUNCTION
        binding.btnReturn.setOnClickListener {
            // Close the fragment
            parentFragmentManager.popBackStack()
        }

        // SET THE RETRIEVED COMMAND VALUES
        binding.txtInputTitle.setText(command?.title)
        binding.txtInputDescr.setText(command?.description)
        binding.txtInputTotalPrice.setText(command?.totalPrice.toString())
        binding.txtInputTable.setText(tableNumber.toString())


    }

}