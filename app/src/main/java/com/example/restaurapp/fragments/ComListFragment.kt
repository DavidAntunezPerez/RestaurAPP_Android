package com.example.restaurapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.example.restaurapp.databinding.FragmentComListBinding
import com.example.restaurapp.entities.Command

class ComListFragment : Fragment() {
    private var _binding: FragmentComListBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_COMMAND = "command"

        fun newInstance(command: Command) = ComListFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_COMMAND, command)
            }
        }
    }

    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComListBinding.inflate(inflater, container, false)
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

        val command: Command? = arguments?.getParcelable(ARG_COMMAND)

        binding.btnClose.setOnClickListener {
            // Enable touch events on the window
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            // Close the fragment
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
