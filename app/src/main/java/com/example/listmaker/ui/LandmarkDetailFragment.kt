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
import com.example.listmaker.databinding.FragmentLandmarkDetailBinding
import com.example.listmaker.models.Landmark
import com.example.listmaker.viewModels.LandmarkViewModel
import com.example.listmaker.viewModels.LandmarkViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LandmarkDetailFragment : Fragment() {
    private val navigationArgs: LandmarkDetailFragmentArgs by navArgs()
    lateinit var item: Landmark

    private var viewModel: LandmarkViewModel

    init {
        val db = RoomData.getDatabase(requireActivity().applicationContext)
        viewModel = ViewModelProvider(requireActivity(), LandmarkViewModelFactory(db.LandmarkDao()))[LandmarkViewModel::class.java]
    }

    private var _binding: FragmentLandmarkDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLandmarkDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Binds views with the passed in item data.
     */
    private fun bind(item: Landmark) {
        binding.apply {
            landmarkName.text = item.landmarkName
            landmarkDescription.text = item.landmarkDescription
            deleteLandmark.setOnClickListener { showConfirmationDialog() }
            editLandmark.setOnClickListener { editLandmark() }
        }
    }

    /**
     * Navigate to the Edit item screen.
     */
    private fun editLandmark() {
        val action = LandmarkDetailFragmentDirections.actionLandmarkDetailFragmentToAddLandmarkFragment(
            getString(R.string.edit_landmark_title),
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
                deleteLandmark()
            }
            .show()
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteLandmark() {
        viewModel.deleteLandmark(item)
        findNavController().navigateUp()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.landmarkId
        // Retrieve the item details using the itemId.
        // Attach an observer on the data (instead of polling for changes) and only update the
        // the UI when the data actually changes.
        viewModel.retrieveLandmark(id).observe(this.viewLifecycleOwner) { selectedItem ->
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