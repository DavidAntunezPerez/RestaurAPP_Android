package com.example.restaurapp.fragments

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurapp.R
import com.example.restaurapp.adapters.ComListDishAdapter
import com.example.restaurapp.databinding.FragmentComShowBinding
import com.example.restaurapp.entities.Command
import com.example.restaurapp.entities.Dish
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.material.snackbar.Snackbar
import java.io.File

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

    data class CommandData(
        var title: String?,
        var description: String?,
        var totalPrice: Double?,
        val dishesList: List<Dish>?,
        var tableNumber: Int? = 0
    )

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

        // GENERATE COMMAND DATA CLASS
        val commandData = CommandData(
            command?.title, command?.description, command?.totalPrice, command?.dishesList, 0
        )

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

                // SAVE THE TABLE NUMBER RETRIEVED IN THE DATA CLASS COMMAND
                commandData.tableNumber = tableNumber?.toInt()

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

        binding.btnExportCSV.setOnClickListener {

            // Serialize data to JSON
            val gson: Gson = GsonBuilder().create()
            val json: String = gson.toJson(commandData)

            // Write JSON to a file
            val fileName = "command.json"
            val fileContents = json.toByteArray()
            val fileOutputStream = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(fileContents)
            fileOutputStream.close()

            // Get the JSON file path
            val filePath = requireContext().getFileStreamPath(fileName).absolutePath

            // Initialize Chaquopy
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(requireContext()))
            }

            // Execute the Python script
            val python = Python.getInstance()
            val script = python.getModule("export_command_csv")
            val result = script.callAttr("main", filePath)?.toString()

            // Log the result
            Log.d("PythonScript", "Result: $result")

            // Process the result (CSV file path)
            if (!result.isNullOrEmpty()) {
                // Get the CSV file path
                val csvFilePath = result

                // Determine the unique CSV file name
                val baseFileName = "command.csv"
                val csvDestination = generateUniqueFileName(baseFileName)

                // Copy the CSV file to external storage
                val csvFile = File(csvFilePath)
                csvFile.copyTo(csvDestination, overwrite = true)

                // Show a Snackbar message to indicate the file has been downloaded
                val snackbar = Snackbar.make(
                    requireView(),
                    "CSV file downloaded: ${csvDestination.absolutePath}",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }

            // Delete the temporary JSON file
            requireContext().deleteFile(fileName)
        }

    }

    // Function to generate a unique file name
    private fun generateUniqueFileName(baseFileName: String): File {
        var fileNumber = 1
        var uniqueFileName: String
        var csvDestination: File
        val downloadsDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        do {
            uniqueFileName = if (fileNumber == 1) {
                baseFileName
            } else {
                val fileNumberString = fileNumber.toString()
                val fileExtension = baseFileName.substringAfterLast('.')
                val fileNameWithoutExtension = baseFileName.substringBeforeLast('.')
                val incrementedFileName =
                    "${fileNameWithoutExtension}_$fileNumberString.$fileExtension"
                incrementedFileName
            }

            csvDestination = File(downloadsDirectory, uniqueFileName)

            fileNumber++
        } while (csvDestination.exists())

        return csvDestination
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
