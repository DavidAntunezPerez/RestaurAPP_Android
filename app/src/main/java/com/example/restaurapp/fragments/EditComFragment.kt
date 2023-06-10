package com.example.restaurapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.restaurapp.R
import com.example.restaurapp.databinding.FragmentEditComBinding
import com.example.restaurapp.entities.Command
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

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

        binding.btnSaveEdit.setOnClickListener {
            // Get the new title and description from the text inputs
            val inputTitle = binding.txtInputTitle.text.toString()
            val inputDescription = binding.txtInputDescr.text.toString()

            val commandId = command?.id

            // Update the document in Firebase Firestore
            if (commandId != null) {
                val firestore = FirebaseFirestore.getInstance()
                val commandRef = firestore.collection("commands").document(commandId)

                val newData = hashMapOf<String, Any>(
                    "title" to inputTitle, "description" to inputDescription
                    // Add any other fields you want to update
                )

                commandRef.update(newData).addOnSuccessListener {
                    // Document updated successfully

                    Snackbar.make(
                        view, R.string.command_edited_successfully, Snackbar.LENGTH_LONG
                    ).show()

                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_main, ComListFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()

                }.addOnFailureListener { exception ->
                    // Error occurred while updating document
                    Snackbar.make(
                        view, getString(R.string.error_message_edit, exception), Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

}