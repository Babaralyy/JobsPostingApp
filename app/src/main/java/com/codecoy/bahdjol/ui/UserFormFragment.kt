package com.codecoy.bahdjol.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.codecoy.bahdjol.R


class UserFormFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // get reference to the string array that we just created
        val types = arrayOf("House/Office Cleaning","Car Washing", "Clothes Washing", "Others")
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = ArrayAdapter(requireActivity(), R.layout.dropdown_item, types)
        // get reference to the autocomplete text view
        val autocompleteTV = requireActivity().findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)


        return inflater.inflate(R.layout.fragment_user_form, container, false)
    }


}