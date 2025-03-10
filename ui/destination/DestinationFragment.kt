package com.example.planitgo_finalproject.ui.destination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.data.models.Destination
import com.example.planitgo_finalproject.data.models.GeoapifyResponse
import com.example.planitgo_finalproject.databinding.DestinationSelectionLayoutBinding
import com.example.planitgo_finalproject.utils.Failure
import com.example.planitgo_finalproject.utils.Loading
import com.example.planitgo_finalproject.utils.Success
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DestinationFragment : Fragment() {
    private var _binding: DestinationSelectionLayoutBinding? = null
    private val binding get() = _binding!!
    private val destViewModel: DestinationViewModel by activityViewModels()
    private var latestResponse: GeoapifyResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DestinationSelectionLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getTextInputAutoComplete()
        countryListObserver()
        setupItemTouchHelper()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getTextInputAutoComplete() {
        binding.searchCityActv.addTextChangedListener { text->
            if(!binding.searchCityActv.isPerformingCompletion){
                destViewModel.getSuggestionsDestinationQuery(text.toString())
            }
        }
        destViewModel.suggestions.observe(viewLifecycleOwner) { resource ->
            when(resource.status){
                is Loading -> {
                    setAutoCompleteSuggestions(binding.searchCityActv, listOf(getString(R.string.loading)))
                }
                is Success -> {
                    latestResponse = resource.status.data
                    val suggestions = resource.status.data?.features
                        ?.filter { !it.properties.city.isNullOrEmpty() && !it.properties.country.isNullOrEmpty() }
                        ?.map { "${it.properties.city}, ${it.properties.country}" }
                        ?.toMutableList() ?: mutableListOf()

                    setAutoCompleteSuggestions(binding.searchCityActv, suggestions)
                    destinationSelectClicked(latestResponse)
                }
                is Failure -> {
                    Toast.makeText(requireContext(), resource.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setAutoCompleteSuggestions(
        searchCityActv: AutoCompleteTextView,
        suggestions: List<String>
    ) {
        if (!isAdded || context == null || activity?.isFinishing == true || activity?.isDestroyed == true) {
            return
        }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
        searchCityActv.setAdapter(adapter)
        if (searchCityActv.text.isNotEmpty()) {
            searchCityActv.post {
                if (searchCityActv.isAttachedToWindow) {
                    searchCityActv.showDropDown()
                }
            }
        }
    }

    private fun destinationSelectClicked(response: GeoapifyResponse?) {
        binding.searchCityActv.setOnItemClickListener { adapterView, _, position, _ ->
            binding.searchCityActv.setText("")
            val selectedDestination = adapterView.getItemAtPosition(position) as String
            val parts = selectedDestination.split(", ")
            val city = parts[0]
            val country = parts[1]
            val selectedDetails = response?.features?.find { it.properties.city == city && it.properties.country == country }
            selectedDetails?.let {
                if(destViewModel.countryList.value?.any{it.lon == selectedDetails.properties.lon && it.lat== selectedDetails.properties.lat} == false){
                    destViewModel.addDestination(it.properties)
                } else{
                    Toast.makeText(requireContext(),
                        getString(R.string.the_destination_already_exists_in_the_list), Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun countryListObserver() {
        destViewModel.countryList.observe(viewLifecycleOwner) {
            binding.countryRvSelectD.adapter =
                DestinationAdapter(it, object : DestinationAdapter.DestinationListener {
                    override fun onAttractionClicked(lon: Double, lat: Double) {
                        destViewModel.setSelectedCountryCoordinate(lon, lat)
                        findNavController().navigate(R.id.action_destinationFragment_to_attractionFragment)
                    }
                })
            binding.countryRvSelectD.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupItemTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(ACTION_STATE_SWIPE, LEFT or RIGHT)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val country = (binding.countryRvSelectD.adapter as DestinationAdapter).countryAt(viewHolder.bindingAdapterPosition)
                deleteDialog(country, viewHolder.bindingAdapterPosition)
            }
        }).attachToRecyclerView(binding.countryRvSelectD)
    }
    private fun deleteDialog(destination: Destination, index: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle(getString(R.string.are_you_sure_you_want_to_delete_the_country))
            setMessage(getString(R.string.the_deletion_will_include_both_the_country_and_the_attractions_if_they_exist_are_you_sure_you_want_to_delete))
            setCancelable(false)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                destViewModel.deleteAttractionFromDestination(destination.lon,destination.lat)
                destViewModel.deleteDestination(destination)
            }
            setNegativeButton(getString(R.string.no)) { _, _ ->
                (binding.countryRvSelectD.adapter as DestinationAdapter).notifyItemChanged(index)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
}