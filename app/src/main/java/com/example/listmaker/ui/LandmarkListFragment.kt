package com.example.listmaker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listmaker.R
import com.example.listmaker.appDatabase.RoomData
import com.example.listmaker.databinding.FragmentListLandmarkBinding
import com.example.listmaker.interfaces.LandmarkListAdapter
import com.example.listmaker.viewModels.LandmarkViewModel
import com.example.listmaker.viewModels.LandmarkViewModelFactory


class LandmarkListFragment : Fragment() {

    private var viewModel: LandmarkViewModel

    init {
        val db = RoomData.getDatabase(requireActivity().applicationContext)
        viewModel = ViewModelProvider(requireActivity(), LandmarkViewModelFactory(db.LandmarkDao()))[LandmarkViewModel::class.java]
    }

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

        val adapter = LandmarkListAdapter {
            val action =
                LandmarkListFragmentDirections.actionLandmarkListFragmentToLandmarkDetailFragment(it.landmarkId)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        viewModel.allLandmarks.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val action = LandmarkListFragmentDirections.actionLandmarkListFragmentToLandmarkAddFragment(
                getString(R.string.add_landmark_title)
            )
            this.findNavController().navigate(action)
        }
    }
}
