package com.example.listmaker.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listmaker.R
import com.example.listmaker.appDatabase.RoomData
import com.example.listmaker.databinding.FragmentListLandmarkBinding
import com.example.listmaker.adapters.LandmarkListAdapter
import com.example.listmaker.viewModels.LandmarkViewModel
import com.example.listmaker.viewModels.LandmarkViewModelFactory


    class LandmarkListFragment : Fragment() {

        private lateinit var viewModel: LandmarkViewModel
        private val navigationArgs: LandmarkListFragmentArgs by navArgs()

        private var _binding: FragmentListLandmarkBinding? = null
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentListLandmarkBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            bindViews()
        }

        private fun bindViews() {
            setupViewModel()
            setupRecyclerView()
            setupNavigationButton()
            setupFloatingActionButton()
        }

        private fun setupViewModel() {
            val db = RoomData.getDatabase(requireActivity().applicationContext)
            viewModel = ViewModelProvider(requireActivity(), LandmarkViewModelFactory(db.LandmarkDao()))[LandmarkViewModel::class.java]
            viewModel.cityId = navigationArgs.cityId
        }

        private fun setupRecyclerView() {
            val adapter = getAdapter()
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = adapter
            viewModel.getLandmarksByCityId(navigationArgs.cityId).observe(viewLifecycleOwner) { items ->
                items.let { adapter.submitList(it) }
            }
        }

        private fun getAdapter(): LandmarkListAdapter {
            return LandmarkListAdapter {
                val action = LandmarkListFragmentDirections.actionLandmarkListFragmentToLandmarkDetailFragment(it.landmarkId, viewModel.cityId ?: 0)
                findNavController().navigate(action)
            }
        }

        private fun setupNavigationButton() {
            binding.floatingActionButton2.setOnClickListener {
                val action = LandmarkListFragmentDirections.actionLandmarkListFragmentToCityListFragment()
                findNavController().navigate(action)
            }
        }

        private fun setupFloatingActionButton() {
            binding.floatingActionButton.setOnClickListener {
                // Inflate the layout for AlertDialog
                val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_add_landmark, null)
                val landmarkNameInput = dialogView.findViewById<EditText>(R.id.landmark_name)
                val landmarkDescriptionInput = dialogView.findViewById<EditText>(R.id.landmark_description)

                // Build the AlertDialog
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.add_landmark_title))
                    .setView(dialogView)
                    .setPositiveButton(getString(R.string.save_action)) { _, _ ->
                        val landmarkName = landmarkNameInput.text.toString()
                        val landmarkDescription = landmarkDescriptionInput.text.toString()
                        if (viewModel.isEntryValid(landmarkName, landmarkDescription)) {
                            viewModel.addNewLandmark(landmarkName, landmarkDescription, viewModel.cityId!!)
                        } else {
                            // Handle case where entries are invalid. This could be a Toast, SnackBar, etc.
                            Toast.makeText(requireContext(), "Please enter valid data", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
                    .create()
                    .show()
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
