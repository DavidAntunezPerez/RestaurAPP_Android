package com.example.restaurapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.adapters.SelectTableAdapter
import com.example.restaurapp.databinding.FragmentSelectTableBinding
import com.example.restaurapp.entities.Table
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SelectTableFragment : Fragment() {
    private var _binding: FragmentSelectTableBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var tableList: ArrayList<Table>
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // GET SHARED PREFERENCES ITEM
        val sharedPreference =
            requireActivity().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userUID = sharedPreference.getString("userUID", "userUID")

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
        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tableList = arrayListOf()

        db.collection("tables").get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                for (documentSnapshot in querySnapshot.documents) {
                    val table: Table? = documentSnapshot.toObject(Table::class.java)
                    if ((table != null) && (table.idRestaurant == userUID)) {
                        table.documentId = documentSnapshot.id // Set the document ID
                        tableList.add(table)
                    }
                }
                recyclerView.adapter = SelectTableAdapter(tableList)

                // ADD ANIMATION WHEN LOADING ALL THE RV
                recyclerView.startLayoutAnimation()

                // HIDE LOADING ANIMATION
                stopAnimation(loadingImageView)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), exception.toString(), Toast.LENGTH_SHORT).show()

            // HIDE LOADING ANIMATION
            stopAnimation(loadingImageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // FUNCTION TO STOP THE ANIMATION WHEN THE RV LOADS
    private fun stopAnimation(imageView: ImageView) {
        imageView.clearAnimation() // Clear the animation
        imageView.visibility = View.GONE // Hide the ImageView
    }
}
