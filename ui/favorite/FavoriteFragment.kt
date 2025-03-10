package com.example.planitgo_finalproject.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.UserCustomAttraction
import com.example.planitgo_finalproject.databinding.FavoriteLayoutBinding
import com.example.planitgo_finalproject.ui.attraction.AttractionApiAdapter
import com.example.planitgo_finalproject.ui.attraction.AttractionCustomAdapter
import com.example.planitgo_finalproject.ui.attraction.AttractionViewModel
import com.example.planitgo_finalproject.ui.destination.DestinationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding : FavoriteLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ArrayAdapter<String>
    private val attractionViewModel : AttractionViewModel by activityViewModels()
    private val favoriteViewModel : FavoriteViewModel by viewModels()
    private val destinationViewModel : DestinationViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FavoriteLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line, mutableListOf())
        adapter.setDropDownViewResource((android.R.layout.simple_dropdown_item_1line))
        binding.destinationSpinner.adapter = adapter
        setupDestinationSpinner()
        destinationSelected()
        favoriteListCustomAttractionObserver()
        favoriteListApiAttraction()
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setupDestinationSpinner(){
        destinationViewModel.countryList.observe(viewLifecycleOwner){dest->
            dest?.let {
                adapter.clear()
                adapter.addAll(dest.map { "${it.city}, ${it.country}" })
                adapter.notifyDataSetChanged()
            }
        }
    }
    private fun destinationSelected() {
        binding.destinationSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem = parent?.getItemAtPosition(position).toString()
                    val parts = selectedItem.split(", ")
                    val city = parts[0]
                    val country = parts[1]
                    val destination = destinationViewModel.countryList.value?.firstOrNull { it.city == city && it.country == country }
                    destination?.let {
                        favoriteViewModel.setLonLatCoordinate(destination.lon, destination.lat)
                    }

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //do nothing
                }
            }
    }
    private fun favoriteListCustomAttractionObserver() {
        favoriteViewModel.favoriteCustomAttractions?.observe(viewLifecycleOwner){
            val attractionCustomAdapter = AttractionCustomAdapter(it, object : AttractionCustomAdapter.AttractionListener{
                override fun onAttractionLongClick(attraction: UserCustomAttraction) {
                    attractionViewModel.setCustomAttraction(attraction)
                    findNavController().navigate(R.id.action_favoriteFragment_to_detailsAttractionFragment)
                }
                override fun onEditAttractionBtn(attraction: UserCustomAttraction) {
                    attractionViewModel.setCustomAttraction(attraction)
                    if(attraction.longitude != null && attraction.latitude != null){
                        attractionViewModel.setLonLatCoordinate(attraction.longitude, attraction.latitude)
                    }
                    findNavController().navigate(R.id.action_favoriteFragment_to_addEditFragment)
                }

                override fun onFavoriteAttractionBtn(attraction: UserCustomAttraction) {
                    attractionViewModel.setFavoriteAttraction(attraction)
                    Toast.makeText(requireContext(),getString(R.string.attraction_remove_from_favorite), Toast.LENGTH_SHORT).show()
                }
            })
            binding.favoriteAttractionCustomRv.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
            binding.favoriteAttractionCustomRv.adapter = attractionCustomAdapter
            binding.explainTextFavoriteAttCustom.visibility = if(it.isEmpty()) View.VISIBLE else View.GONE
        }
    }
    private fun favoriteListApiAttraction() {
        favoriteViewModel.favoriteApiAttractions?.observe(viewLifecycleOwner){
            val attractionApiAdapter = AttractionApiAdapter(it, object : AttractionApiAdapter.ApiAttractionListener{
                override fun onApiAttractionLongClick(attraction: ApiAttraction) {
                    attractionViewModel.setApiAttraction(attraction)
                    findNavController().navigate(R.id.action_favoriteFragment_to_detailsAttractionFragment)
                }

                override fun onApiFavoriteAttractionBtn(attraction: ApiAttraction) {
                    attractionViewModel.setFavoriteAttraction(attraction)
                    Toast.makeText(requireContext(),getString(R.string.attraction_remove_from_favorite), Toast.LENGTH_SHORT).show()
                }

            })
            binding.favoriteAttractionApiRv.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
            binding.favoriteAttractionApiRv.adapter = attractionApiAdapter
            binding.explainTextFavoriteAttApi.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}
