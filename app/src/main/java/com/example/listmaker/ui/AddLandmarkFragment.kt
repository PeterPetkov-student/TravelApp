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
import com.example.listmaker.databinding.FragmentAddLandmarkBinding
import com.example.listmaker.models.Landmark
import com.example.listmaker.viewModels.LandmarkViewModel
import com.example.listmaker.viewModels.LandmarkViewModelFactory

class AddLandmarkFragment : Fragment() {

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.
    private lateinit var viewModel: LandmarkViewModel

    private val navigationArgs : LandmarkDetailFragmentArgs by navArgs()

    lateinit var item: Landmark

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddLandmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddLandmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.landmarkName.text.toString(),
            binding.landmarkDescription.text.toString(),
        )
    }

    /**
     * Binds views with the passed in [item] information.
     */
    private fun bind(item: Landmark) {
        binding.apply {
            landmarkName.setText(item.landmarkName, TextView.BufferType.SPANNABLE)
            landmarkDescription.setText(item.landmarkDescription, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateLandmark() }
        }
    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewLandmark() {
        if (isEntryValid()) {
            val cityId = viewModel.cityId?:0
            viewModel.addNewLandmark(
                binding.landmarkName.text.toString(),
                binding.landmarkDescription.text.toString(),
                cityId
            )
            val action = AddLandmarkFragmentDirections.actionLandmarkAddFragmentToLandmarkListFragment(cityId)
            findNavController().navigate(action)
        }
    }

    /**
     * Updates an existing Item in the database and navigates up to list fragment.
     */
    private fun updateLandmark() {
        if (isEntryValid()) {
            val landmarkId = viewModel.landmarkId?:0
            viewModel.updateLandmark(
                this.binding.landmarkName.text.toString(),
                this.binding.landmarkDescription.text.toString(),
                landmarkId
            )
            val action = AddLandmarkFragmentDirections.actionLandmarkAddFragmentToLandmarkListFragment(landmarkId)
            findNavController().navigate(action)
        }
    }

    /**
     * Called when the view is created.
     * The cityId Navigation argument determines the edit item  or add new item.
     * If the itemId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = RoomData.getDatabase(requireActivity().applicationContext)
        viewModel = ViewModelProvider(requireActivity(), LandmarkViewModelFactory(db.LandmarkDao()))[LandmarkViewModel::class.java]

        val id = viewModel.landmarkId?:0

        if (id > 0) {
            viewModel.retrieveLandmark(id).observe(this.viewLifecycleOwner) { selectedLandmark ->
                item = selectedLandmark
                bind(item)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewLandmark()
            }
        }
        binding.floatingActionButton6.setOnClickListener {
            this.findNavController().navigateUp()
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