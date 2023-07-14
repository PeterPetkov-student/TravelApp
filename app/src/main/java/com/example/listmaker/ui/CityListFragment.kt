package com.example.listmaker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listmaker.viewModels.CityViewModel
import com.example.listmaker.viewModels.CityViewModelFactory
import com.example.listmaker.R
import com.example.listmaker.appDatabase.RoomData
import com.example.listmaker.databinding.FragmentCityListBinding
import com.example.listmaker.adapters.CityListAdapter
import com.example.listmaker.adapters.LandmarkListAdapter
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
        val db = RoomData.getDatabase(requireActivity().applicationContext)
        landmarkViewModel = ViewModelProvider(requireActivity(), LandmarkViewModelFactory(db.LandmarkDao()))[LandmarkViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity(), CityViewModelFactory(db.CityDao(), landmarkViewModel))[CityViewModel::class.java]

        val adapter = CityListAdapter(
            onItemClicked = { city ->
                val action = CityListFragmentDirections.actionCityListFragmentToLandmarkListFragment(city.cityId)
                findNavController().navigate(action)
            },
            onItemLongClick = { city ->
                val action = CityListFragmentDirections.actionCityListFragmentToCityDetailFragment(city.cityId)
                findNavController().navigate(action)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        viewModel.allCities.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val action = CityListFragmentDirections.actionCityListFragmentToCityAddFragment(
                getString(R.string.add_city_title),
                0
            )
            this.findNavController().navigate(action)
        }
    }
}
