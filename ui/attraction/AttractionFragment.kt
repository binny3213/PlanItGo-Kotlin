package com.example.planitgo_finalproject.ui.attraction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.UserCustomAttraction
import com.example.planitgo_finalproject.databinding.AttractionLayoutBinding
import com.example.planitgo_finalproject.ui.destination.DestinationViewModel
import com.example.planitgo_finalproject.utils.Failure
import com.example.planitgo_finalproject.utils.Loading
import com.example.planitgo_finalproject.utils.Success
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttractionFragment : Fragment() {
    private var _binding : AttractionLayoutBinding? = null
    private val binding get() = _binding!!
    private val destinationViewModel : DestinationViewModel by activityViewModels()
    private val attractionViewModel : AttractionViewModel by activityViewModels()
    private lateinit var adapter: AttractionApiAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AttractionLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        selectedCountryCoordinateObserver()
        allAttractionApiObserver()
        allCustomAttractionObserver()
        setupItemTouchHelper()
        searchAttraction()
        binding.AddAttractionBtn.setOnClickListener {
            findNavController().navigate(R.id.action_attractionFragment_to_addEditFragment)
        }
        binding.AttractionsBackBtn.setOnClickListener {
            findNavController().navigate(R.id.action_attractionFragment_to_destinationFragment)
        }
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun selectedCountryCoordinateObserver(){
        destinationViewModel.selectedCountryCoordinate.observe(viewLifecycleOwner){ coor ->
            attractionViewModel.setLonLatCoordinate(coor.first,coor.second)
            val country = destinationViewModel.countryList.value?.firstOrNull { it.lon == coor.first && it.lat == coor.second }
            binding.mainTitle.text = getString(R.string.explore)+ " " + "${country?.city}"
        }
    }
    private fun allAttractionApiObserver(){
        attractionViewModel.allApiAttractions.observe(viewLifecycleOwner){ attraction ->
            attraction?.let {
                when(it.status){
                    is Loading -> {
                        binding.progressBarAttraction.visibility = View.VISIBLE
                    }
                    is Success -> {
                        if(!it.status.data.isNullOrEmpty()){
                            binding.progressBarAttraction.visibility = View.GONE
                            binding.explainTextAttApi.visibility = View.GONE
                            adapter = AttractionApiAdapter(it.status.data, object : AttractionApiAdapter.ApiAttractionListener{
                                override fun onApiAttractionLongClick(attraction: ApiAttraction) {
                                    attractionViewModel.setApiAttraction(attraction)
                                    findNavController().navigate(R.id.action_attractionFragment_to_detailsAttractionFragment)
                                }

                                override fun onApiFavoriteAttractionBtn(attraction: ApiAttraction) {
                                    attractionViewModel.setFavoriteAttraction(attraction)
                                    if(attraction.isFavorite){
                                        Toast.makeText(requireContext(),getString(R.string.attraction_added_to_favorite), Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(requireContext(),
                                            getString(R.string.attraction_remove_from_favorite), Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                            binding.attractionApiRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                            binding.attractionApiRv.adapter = adapter
                        } else{
                            binding.progressBarAttraction.visibility = View.GONE
                            binding.explainTextAttApi.visibility = View.VISIBLE
                        }
                    }
                    is Failure -> {
                        binding.progressBarAttraction.visibility = View.GONE
                        Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun allCustomAttractionObserver(){
        attractionViewModel.customAttractionList?.observe(viewLifecycleOwner) { attraction ->
            attraction?.let {
                val attractionCustomAdapter = AttractionCustomAdapter(it, object : AttractionCustomAdapter.AttractionListener{
                    override fun onAttractionLongClick(attraction: UserCustomAttraction) {
                        attractionViewModel.setCustomAttraction(attraction)
                        findNavController().navigate(R.id.action_attractionFragment_to_detailsAttractionFragment)
                    }
                    override fun onEditAttractionBtn(attraction: UserCustomAttraction) {
                        attractionViewModel.setCustomAttraction(attraction)
                        findNavController().navigate(R.id.action_attractionFragment_to_addEditFragment)
                    }

                    override fun onFavoriteAttractionBtn(attraction: UserCustomAttraction) {
                        attractionViewModel.setFavoriteAttraction(attraction)
                        if(attraction.isFavorite){
                            Toast.makeText(requireContext(),getString(R.string.attraction_added_to_favorite), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(),
                                getString(R.string.attraction_remove_from_favorite), Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                binding.attractionCustomRv.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                binding.attractionCustomRv.adapter = attractionCustomAdapter
                binding.explainTextAttCustom.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
    private fun setupItemTouchHelper(){
        ItemTouchHelper(object: ItemTouchHelper.Callback(){
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) = makeFlag(ACTION_STATE_SWIPE, UP or DOWN)

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val attraction = (binding.attractionCustomRv.adapter as AttractionCustomAdapter).attractionAt(viewHolder.bindingAdapterPosition)
                attractionViewModel.deleteCustomAttraction(attraction)
            }
        }).attachToRecyclerView(binding.attractionCustomRv)
    }
    private fun searchAttraction(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                attractionViewModel.searchAttractions(newText.orEmpty())
                (binding.attractionCustomRv.adapter as? AttractionCustomAdapter)?.updateList(attractionViewModel.filteredAttractions)
                return true
            }
        })
    }

}