package com.example.restaurapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.restaurapp.activities.ComListActivity
import com.example.restaurapp.activities.SelectTableActivity
import com.example.restaurapp.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SELECT TABLE OPTION
        binding.btnSelectTable.setOnClickListener {
            val intent = Intent(requireContext(), SelectTableActivity::class.java)
            startActivity(intent)
        }

        // COMMAND LIST OPTION
        binding.btnComList.setOnClickListener {
            val intent = Intent(requireContext(), ComListActivity::class.java)
            startActivity(intent)
        }


    }
}