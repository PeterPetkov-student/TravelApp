package com.example.listmaker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.listmaker.R
import com.example.listmaker.appDatabase.RoomData
import com.example.listmaker.databinding.FragmentCityDetailBinding
import com.example.listmaker.models.City
import com.example.listmaker.viewModels.CityViewModel
import com.example.listmaker.viewModels.CityViewModelFactory
import com.example.listmaker.viewModels.LandmarkViewModel
import com.example.listmaker.viewModels.LandmarkViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CityDetailFragment : Fragment() {
    private val navigationArgs: CityDetailFragmentArgs by navArgs()

    lateinit var item: City

    private lateinit var viewModel: CityViewModel
    private lateinit var landmarkViewModel: LandmarkViewModel

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
            getString(R.string.edit_city_title),
            item.cityId
        )
        this.findNavController().navigate(action)
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        landmarkViewModel.getLandmarksByCityId(navigationArgs.cityId).observe(viewLifecycleOwner) { landmarks ->
            val hasAssociatedLandmarks = landmarks.isNotEmpty()
            if (hasAssociatedLandmarks) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.cannot_delete_city_with_landmarks_title))
                    .setMessage(getString(R.string.cannot_delete_city_with_landmarks_message))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes)) { _, _ -> }
                    .show()
            } else {
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
        }
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

        val db = RoomData.getDatabase(requireActivity().applicationContext)
        landmarkViewModel = ViewModelProvider(requireActivity(), LandmarkViewModelFactory(db.LandmarkDao()))[LandmarkViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity(), CityViewModelFactory(db.CityDao(), landmarkViewModel))[CityViewModel::class.java]

        val id = navigationArgs.cityId
        // Retrieve the item details using the itemId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        viewModel.retrieveCity(id).observe(this.viewLifecycleOwner) { selectedItem ->
            item = selectedItem
            bind(item)
        }
        binding.floatingActionButton4.setOnClickListener {
            this.findNavController().navigateUp()
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