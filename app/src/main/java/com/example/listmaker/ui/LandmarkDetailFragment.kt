package com.example.listmaker.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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

    private lateinit var viewModel: LandmarkViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        observeLandmark()
        setupFab()
        bindViews()
    }

    private fun setupViewModel() {
        val db = RoomData.getDatabase(requireActivity().applicationContext)
        viewModel = ViewModelProvider(requireActivity(), LandmarkViewModelFactory(db.LandmarkDao()))[LandmarkViewModel::class.java]
    }

    private fun observeLandmark() {
        viewModel.retrieveLandmark(navigationArgs.landmarkId).observe(viewLifecycleOwner) { selectedItem ->
            item = selectedItem
            bind(item)
        }
    }

    private fun setupFab() {
        binding.floatingActionButton3.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindViews() {
        with(binding) {
            deleteLandmark.setOnClickListener { showConfirmationDialog() }
            editLandmark.setOnClickListener { editLandmark() }
        }
    }

    private fun bind(item: Landmark) {
        binding.apply {
            landmarkName.text = item.landmarkName
            landmarkDescription.text = item.landmarkDescription
        }
    }
    /**
     * Navigate to the Edit item screen.
     */
    private fun editLandmark() {
        viewModel.landmarkId = navigationArgs.landmarkId

        viewModel.retrieveLandmark(viewModel.landmarkId!!).observeOnce(viewLifecycleOwner, Observer { currentLandmark ->
            if (currentLandmark != null) {
                // Inflate the layout for AlertDialog
                val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_add_landmark, null)
                val landmarkNameInput = dialogView.findViewById<EditText>(R.id.landmark_name)
                val landmarkDescriptionInput = dialogView.findViewById<EditText>(R.id.landmark_description)

                // Populate the EditTexts with the current landmark data
                landmarkNameInput.setText(currentLandmark.landmarkName)
                landmarkDescriptionInput.setText(currentLandmark.landmarkDescription)

                // Build the AlertDialog
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.edit_landmark_title))
                    .setView(dialogView)
                    .setPositiveButton(getString(R.string.save_action)) { _, _ ->
                        val landmarkName = landmarkNameInput.text.toString()
                        val landmarkDescription = landmarkDescriptionInput.text.toString()
                        if (viewModel.isEntryValid(landmarkName, landmarkDescription)) {
                            viewModel.updateLandmark(viewModel.landmarkId!!,landmarkName, landmarkDescription, viewModel.cityId!!)
                        } else {
                            // Handle case where entries are invalid. This could be a Toast, SnackBar, etc.
                            Toast.makeText(requireContext(), "Please enter valid data", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
                    .create()
                    .show()
            } else {
                Toast.makeText(requireContext(), "Unable to retrieve Landmark", Toast.LENGTH_SHORT).show()
            }
        })
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

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}