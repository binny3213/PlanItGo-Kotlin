package com.example.planitgo_finalproject.ui.details_attraction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.databinding.DetailsLayoutBinding
import com.example.planitgo_finalproject.ui.attraction.AttractionViewModel
import com.example.planitgo_finalproject.utils.Failure
import com.example.planitgo_finalproject.utils.Loading
import com.example.planitgo_finalproject.utils.Success
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsAttractionFragment : Fragment() {
    private var _binding : DetailsLayoutBinding? = null
    private val binding get() = _binding!!
    private val attractionViewModel : AttractionViewModel by activityViewModels()
    @Inject lateinit var glide: RequestManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailsLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        chosenAttractionObserver()
        chosenAttractionApiObserver()
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (isRemoving && !requireActivity().isChangingConfigurations) {
            attractionViewModel.setCustomAttraction(null)
            attractionViewModel.setApiAttraction(null)
        }
    }
    private fun chosenAttractionObserver(){
        attractionViewModel.chosenCustomAttraction.observe(viewLifecycleOwner){ attraction ->
            attraction?.let {
                binding.titleAttraction.text = attraction.name
                binding.location.text = attraction.location
                binding.descText.text = attraction.description
                binding.hours.text = "${attraction.openingTime} - ${attraction.endTime}"
                binding.price.text = attraction.price.toString() + "$"
                glide.load(attraction.image).into(binding.backImg)
            }
        }
        val previousDestination = findNavController().previousBackStackEntry?.destination?.id
        binding.detailsBackBtn.setOnClickListener {
            when(previousDestination){
                R.id.favoriteFragment ->  findNavController().navigate(R.id.action_detailsAttractionFragment_to_favoriteFragment)
                else ->  findNavController().navigate(R.id.action_detailsAttractionFragment_to_attractionFragment)
            }
        }
    }
    private fun chosenAttractionApiObserver() {
        attractionViewModel.attractionFullDetails.observe(viewLifecycleOwner){ attDetails ->
            attDetails?.let {
                when(it.status){
                    is Loading ->{
                        showApiDetails(it.status.data, getString(R.string.loading),  getString(R.string.loading), it.status.data?.icon)
                    }
                    is Success -> {
                        showApiDetails(it.status.data, it.status.data?.description, it.status.data?.openingHours, it.status.data?.photoURL)
                    }
                    is Failure -> {
                        showApiDetails(it.status.data,
                            getString(R.string.no_description_found),
                            getString(R.string.no_hours_found), it.status.data?.icon)
                        Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun showApiDetails(attraction: ApiAttraction?, desc: String?, hours: String?, image: String?){
        binding.titleAttraction.text = attraction?.name
        binding.location.text = attraction?.address
        binding.descText.text = desc ?: getString(R.string.no_description_found)
        binding.hours.text = hours ?: getString(R.string.no_hours_found)
        binding.price.visibility = View.GONE
        binding.ratingBar.visibility = View.VISIBLE
        binding.ratingBar.rating = attraction?.rating?.toFloatOrNull() ?: 0f
        glide.load(image).into(binding.backImg)
    }

}