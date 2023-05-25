package com.example.restaurapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurapp.R
import com.example.restaurapp.adapters.ComListDishAdapter
import com.example.restaurapp.databinding.FragmentComShowBinding
import com.example.restaurapp.entities.Command
import com.example.restaurapp.entities.Dish
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ComShowFragment : Fragment() {
    private var _binding: FragmentComShowBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_COMMAND = "command"

        fun newInstance(command: Command) = ComShowFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_COMMAND, command)
            }
        }
    }

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComShowBinding.inflate(inflater, container, false)
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

        // SET BUTTON CLOSE FUNCTION
        binding.btnClose.setOnClickListener {
            // Enable touch events on the window
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            // Close the fragment
            parentFragmentManager.popBackStack()
        }

        val command: Command? = arguments?.getParcelable(ARG_COMMAND)
        val dishesList: List<Dish> = command?.dishesList ?: emptyList()
        var tableNumberInt: Long? = 0

        val adapter = ComListDishAdapter(dishesList)
        binding.rvDishes.adapter = adapter
        binding.rvDishes.layoutManager = LinearLayoutManager(requireContext())

        // SHOW THE DATA OF THE COMMAND
        binding.tvTitle.text = command?.title
        binding.tvDescription.text = command?.description
        binding.tvTotalPrice.text = command?.totalPrice.toString()

        // SHOW TABLE NUMBER
        val firestore = FirebaseFirestore.getInstance()
        val tableRef = command?.idTable?.let { firestore.collection("tables").document(it) }
        tableRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot?.exists() == true) {
                val tableNumber = documentSnapshot.getLong("number")
                tableNumberInt = tableNumber
                val tableText = when (Locale.getDefault().language) {
                    "es" -> "MESA: ${tableNumber ?: "N/A"}"
                    else -> "TABLE: ${tableNumber ?: "N/A"}"
                }
                binding.tvTableLabel.text = tableText
            } else {
                val tableText = when (Locale.getDefault().language) {
                    "es" -> "MESA: N/A"
                    else -> "TABLE: N/A"
                }
                binding.tvTableLabel.text = tableText
            }
        }?.addOnFailureListener { exception ->
            val tableText = when (Locale.getDefault().language) {
                "es" -> "MESA: N/A"
                else -> "TABLE: N/A"
            }
            binding.tvTableLabel.text = tableText
            Log.e("CommandAdapter", "Failed to fetch table document: $exception")
        }

        binding.btnEdit.setOnClickListener {
            // Create the new fragment instance and pass the command item
            val editFragment = EditComFragment.newInstance(command, tableNumberInt)
            // Perform the fragment transaction to replace the current fragment
            parentFragmentManager.beginTransaction().replace(R.id.ComListDisplay, editFragment)
                .addToBackStack(null).commit()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
