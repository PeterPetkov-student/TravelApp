package com.example.listmaker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.listmaker.appDatabase.RoomData
import com.example.listmaker.databinding.FragmentAddCityBinding
import com.example.listmaker.models.City
import com.example.listmaker.viewModels.CityViewModel
import com.example.listmaker.viewModels.CityViewModelFactory

class AddCityFragment : Fragment() {

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.

    private val navigationArgs: CityDetailFragmentArgs by navArgs()

    lateinit var item: City

    private  var viewModel: CityViewModel
    init {
        val db = RoomData.getDatabase(requireActivity().applicationContext)
        viewModel = ViewModelProvider(requireActivity(), CityViewModelFactory(db.CityDao()))[CityViewModel::class.java]
    }

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddCityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.cityName.text.toString(),
            binding.cityDescription.text.toString(),
        )
    }

    /**
     * Binds views with the passed in [item] information.
     */
    private fun bind(item: City) {

        binding.apply {
            cityName.setText(item.cityName, TextView.BufferType.SPANNABLE)
            cityDescription.setText(item.cityDescription, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateCity() }
        }
    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewCity() {
        if (isEntryValid()) {
            viewModel.addNewCity(
                binding.cityName.text.toString(),
                binding.cityDescription.text.toString()
            )
            val action = AddCityFragmentDirections.actionCityAddFragmentToCityListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Updates an existing Item in the database and navigates up to list fragment.
    */
    private fun updateCity() {
        if (isEntryValid()) {
            viewModel.updateCity(
                this.navigationArgs.cityId,
                this.binding.cityName.text.toString(),
                this.binding.cityDescription.text.toString()
            )
            val action = AddCityFragmentDirections.actionCityAddFragmentToCityListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Called when the view is created.
     * The cityId Navigation argument determines the edit item  or add new item.
     * If the cityId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.cityId
        if (id > 0) {
            viewModel.retrieveCity(id).observe(this.viewLifecycleOwner) { selectedCity ->
                item = selectedCity
                bind(item)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewCity()
            }
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
