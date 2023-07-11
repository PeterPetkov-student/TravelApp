package com.example.listmaker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.listmaker.R
import com.example.listmaker.appDatabase.RoomData
import com.example.listmaker.databinding.FragmentCityDetailBinding
import com.example.listmaker.models.City
import com.example.listmaker.viewModels.CityViewModel
import com.example.listmaker.viewModels.CityViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CityDetailFragment : Fragment() {
    private val navigationArgs: CityDetailFragmentArgs by navArgs()
    lateinit var item: City

    private lateinit var viewModel: CityViewModel

    init {
        val db = RoomData.getDatabase(requireActivity().applicationContext)
        viewModel = ViewModelProvider(requireActivity(), CityViewModelFactory(db.CityDao()))[CityViewModel::class.java]
    }

    private var _binding: FragmentCityDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCityDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Binds views with the passed in item data.
     */
    private fun bind(item: City) {
        binding.apply {
            cityName.text = item.cityName
            cityDescription.text = item.cityDescription
            deleteCity.setOnClickListener { showConfirmationDialog() }
            editCity.setOnClickListener { editCity() }
        }
    }

    /**
     * Navigate to the Edit item screen.
     */
    private fun editCity() {
        val action = CityDetailFragmentDirections.actionCityDetailFragmentToAddCityFragment(
            getString(R.string.edit_city_title)
        )
        this.findNavController().navigate(action)
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteCity()
            }
            .show()
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteCity() {
        viewModel.deleteCity(item)
        findNavController().navigateUp()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.cityId
        // Retrieve the item details using the itemId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        viewModel.retrieveCity(id).observe(this.viewLifecycleOwner) { selectedItem ->
            item = selectedItem
            bind(item)
        }
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}