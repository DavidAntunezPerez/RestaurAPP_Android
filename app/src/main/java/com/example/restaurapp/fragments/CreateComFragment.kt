package com.example.restaurapp.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.entities.Dish
import com.example.restaurapp.adapters.DishCCMoreAdapter

class CreateComFragment : Fragment() {

    private lateinit var dataPasser: OnDataPass
    private lateinit var recyclerView: RecyclerView
    private lateinit var dishCCMoreAdapter: DishCCMoreAdapter
    private lateinit var dishList: ArrayList<Dish>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_com, container, false)

        // INITIALIZING DISH LIST
        dishList = ArrayList()

        // SET UP RECYCLER VIEW
        recyclerView = view.findViewById(R.id.recyclerViewFragmentCreateCom)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
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

        dishCCMoreAdapter = DishCCMoreAdapter(dishList)
        recyclerView.adapter = dishCCMoreAdapter
    }

    interface OnDataPass {
        fun onDataPass(title: String, description: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    fun setDishList(dishes: List<Dish>) {
        dishList.clear()
        dishList.addAll(dishes)

        dishCCMoreAdapter.notifyDataSetChanged()
    }

    fun hideFragment(): MutableList<Dish> {
        return dishCCMoreAdapter.getCCMoreList()
    }


}
