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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listmaker.viewModels.CityViewModel
import com.example.listmaker.viewModels.CityViewModelFactory
import com.example.listmaker.R
import com.example.listmaker.appDatabase.RoomData
import com.example.listmaker.databinding.FragmentCityListBinding
import com.example.listmaker.adapters.CityListAdapter
import com.example.listmaker.viewModels.LandmarkViewModel
import com.example.listmaker.viewModels.LandmarkViewModelFactory


class CityListFragment : Fragment() {
    private lateinit var viewModel: CityViewModel
    private lateinit var landmarkViewModel: LandmarkViewModel

    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews()
    }

    private fun bindViews() {
        val db = RoomData.getDatabase(requireActivity().applicationContext)
        landmarkViewModel = ViewModelProvider(requireActivity(), LandmarkViewModelFactory(db.LandmarkDao()))[LandmarkViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity(), CityViewModelFactory(db.CityDao(), landmarkViewModel))[CityViewModel::class.java]

        setupRecyclerView()
        setupFloatingActionButton()
    }

    private fun setupRecyclerView() {
        val adapter = getAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        viewModel.allCities.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }
    }

    private fun getAdapter(): CityListAdapter {
        return CityListAdapter(
            onItemClicked = { city ->
                val action = CityListFragmentDirections.actionCityListFragmentToLandmarkListFragment(city.cityId)
                findNavController().navigate(action)
            },
            onItemLongClick = { city ->
                val action = CityListFragmentDirections.actionCityListFragmentToCityDetailFragment(city.cityId)
                findNavController().navigate(action)
            }
        )
    }

    private fun setupFloatingActionButton() {
        binding.floatingActionButton.setOnClickListener {
            // Inflate the layout for AlertDialog
            val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_add_city, null)
            val cityNameInput = dialogView.findViewById<EditText>(R.id.city_name)
            val cityDescriptionInput = dialogView.findViewById<EditText>(R.id.city_description)

            // Build the AlertDialog
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.add_city_title))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.save_action)) { _, _ ->
                    val cityName = cityNameInput.text.toString()
                    val cityDescription = cityDescriptionInput.text.toString()
                    if (viewModel.isEntryValid(cityName, cityDescription)) {
                        viewModel.addNewCity(cityName, cityDescription)
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

}
