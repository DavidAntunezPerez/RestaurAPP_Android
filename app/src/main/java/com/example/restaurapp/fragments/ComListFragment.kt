package com.example.restaurapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.adapters.CommandAdapter
import com.example.restaurapp.databinding.FragmentComListBinding
import com.example.restaurapp.entities.Command
import com.example.restaurapp.entities.Dish
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class ComListFragment : Fragment(), CommandAdapter.OnCommandLongClickListener,
    CommandAdapter.OnItemClickListener {

    private var _binding: FragmentComListBinding? = null
    private val binding get() = _binding!!

    private lateinit var commandRecyclerView: RecyclerView
    private lateinit var commandList: ArrayList<Command>
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GET SHARED PREFERENCES ITEM
        val sharedPreference =
            requireActivity().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userUID = sharedPreference.getString("userUID", "userUID")
        val language = sharedPreference.getString("language", Locale.getDefault().language)


        // LOADING ANIMATIONS
        // LOAD RV
        val loadingImageView: ImageView = binding.loadingAnimation
        // LOADING SPINNER
        binding.loadingAnimation.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(), R.anim.loading_anim
            )
        )

        // SETTING UP THE RECYCLERVIEW
        commandRecyclerView = binding.commandrecyclerview
        commandRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        commandList = arrayListOf()

        db.collection("commands").get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                for (documentSnapshot in querySnapshot.documents) {
                    val command: Command? = documentSnapshot.toObject(Command::class.java)
                    if ((command != null) && (command.idRestaurant == userUID)) {
                        command.id = documentSnapshot.id // Set the document ID

                        // Retrieve the dishesList property as a List<Map<String, Any>>
                        val dishesList =
                            documentSnapshot.get("dishesList") as? List<Map<String, Any>>

                        // Convert each dish map to a Dish object
                        val dishList = dishesList?.map { dishMap ->
                            Dish(
                                id = dishMap["id"] as? String,
                                idRestaurant = dishMap["idRestaurant"] as? String,
                                image = dishMap["image"] as? String,
                                name = dishMap["name"] as? String,
                                price = dishMap["price"] as? Double ?: 0.0
                            )
                        }

                        // Set the dishList in the command object
                        command.dishesList = dishList

                        // Check if the title is null or empty or contains only whitespace and set it
                        // based on the language
                        when (language) {
                            "es" -> {
                                if (command.title.isNullOrBlank()) {
                                    command.title = "Comanda sin Título"
                                }
                            }

                            else -> {
                                if (command.title.isNullOrBlank()) {
                                    command.title = "Untitled Command"
                                }
                            }
                        }

                        // Check if the description is null or empty or contains only whitespace and
                        // set it based on the language
                        when (language) {
                            "es" -> {
                                if (command.description.isNullOrBlank()) {
                                    command.description = "Sin descripción"
                                }
                            }

                            else -> {
                                if (command.description.isNullOrBlank()) {
                                    command.description = "No description provided"
                                }
                            }
                        }

                        commandList.add(command)
                    }
                }
                commandRecyclerView.adapter = CommandAdapter(commandList, this, this)

                // ADD ANIMATION WHEN LOADING ALL THE RV
                commandRecyclerView.startLayoutAnimation()

                // HIDE LOADING ANIMATION
                stopAnimation(loadingImageView)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), exception.toString(), Toast.LENGTH_SHORT).show()

            // HIDE LOADING ANIMATION
            stopAnimation(loadingImageView)
        }
    }

    // FUNCTION TO STOP THE ANIMATION WHEN THE RV LOADS
    private fun stopAnimation(imageView: ImageView) {
        imageView.clearAnimation() // Clear the animation
        imageView.visibility = View.GONE // Hide the ImageView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCommandLongClick(command: Command) {
        val position = commandList.indexOf(command)
        if (position != -1) {
            val itemView = commandRecyclerView.findViewHolderForAdapterPosition(position)?.itemView
            itemView?.let { view ->
                showContextMenu(view, command)
            }
        }
    }

    private fun showContextMenu(view: View, command: Command) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_delete_command, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_delete -> {
                    showDeleteConfirmationDialog(command)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showDeleteConfirmationDialog(command: Command) {
        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.delete_confirmation_title))
            .setMessage(getString(R.string.delete_confirmation_message))
            .setPositiveButton(getString(R.string.delete_confirmation_positive_button)) { dialog, _ ->
                // Delete the command here
                deleteCommand(command)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.delete_confirmation_negative_button)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun deleteCommand(command: Command) {
        // Perform the deletion
        db.collection("commands").document(command.id!!).delete().addOnSuccessListener {
            Snackbar.make(
                binding.root, getString(R.string.command_deleted_success), Snackbar.LENGTH_SHORT
            ).show()

            // Remove the command from the list
            val removedIndex = commandList.indexOf(command)
            if (removedIndex != -1) {
                commandList.removeAt(removedIndex)
                commandRecyclerView.adapter?.notifyItemRemoved(removedIndex)
            }
        }.addOnFailureListener { exception ->
            val errorMessage = getString(
                R.string.failed_to_delete_command, exception.message
            )
            Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            // Handle the failure to delete the command
        }
    }

    override fun onItemClick(command: Command) {
        // Display the full-screen fragment
        val fragment = ComShowFragment.newInstance(command)
        parentFragmentManager.beginTransaction().replace(R.id.ComListDisplay, fragment)
            .addToBackStack(null).commit()
    }

}
