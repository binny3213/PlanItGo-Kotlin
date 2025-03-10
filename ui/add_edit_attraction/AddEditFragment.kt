package com.example.planitgo_finalproject.ui.add_edit_attraction

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.data.models.UserCustomAttraction
import com.example.planitgo_finalproject.databinding.AddEditAttLayoutBinding
import com.example.planitgo_finalproject.ui.attraction.AttractionViewModel
import com.example.planitgo_finalproject.ui.favorite.FavoriteViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddEditFragment : Fragment() {
    private var _binding : AddEditAttLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickItemLauncher: ActivityResultLauncher<Array<String>>
    private val attractionViewModel : AttractionViewModel by activityViewModels()
    private val favoriteViewModel: FavoriteViewModel by activityViewModels()
    private lateinit var coordinate: Pair<Double, Double>
    @Inject lateinit var glide: RequestManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddEditAttLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pickItemLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()){ uri ->
            uri?.let {
                binding.imageContainer.setImageURI(it)
                requireActivity().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                attractionViewModel.setImageUri(it)
            }
        }
        attractionViewModel.coordinate.observe(viewLifecycleOwner){ coor ->
            coor?.let{
                coordinate = coor
            }
        }
        getAttractionDetails()
        super.onViewCreated(view, savedInstanceState)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (isRemoving && !requireActivity().isChangingConfigurations) {
            attractionViewModel.setCustomAttraction(null)
            attractionViewModel.setImageUri(Uri.parse("android.resource://com.example.planitgo_finalproject/drawable/airplain1"))
        }
    }
    private fun getAttractionDetails(){
        setupChosenAttractionObserver()
        binding.openTimeBtn.setOnClickListener {
            openClockDialog(binding.openTimeBtn)
        }
        binding.closeTimeBtn.setOnClickListener {
            openClockDialog(binding.closeTimeBtn)
        }
        uploadImage()
        addTextWatcher()
        binding.saveBtn.setOnClickListener {
            val attractionName = binding.nameEditText.editText?.text.toString()
            val attractionAddress = binding.locationEditText.editText?.text.toString()
            val attractionDescription = binding.descEditText.editText?.text.toString()
            val price = binding.priceEditText.editText?.text.toString()
            val openTime = binding.openTimeBtn.text.toString()
            val closeTime = binding.closeTimeBtn.text.toString()
            val favorite = attractionViewModel.chosenCustomAttraction.value?.isFavorite == true
            if(checkTextDetails(attractionName,attractionAddress,attractionDescription,price) &&
                checkTimeDetails(openTime, closeTime)){
                val attraction = UserCustomAttraction(attractionName, attractionAddress,coordinate.second,coordinate.first, attractionDescription,
                    openTime, closeTime,price.toDouble(), favorite ,attractionViewModel.imageUri.value.toString()).apply {
                    id = attractionViewModel.chosenCustomAttraction.value?.id ?: 0
                }
                if(attractionViewModel.chosenCustomAttraction.value == null){
                    attractionViewModel.addCustomAttraction(attraction)
                }
                else{
                    attractionViewModel.updateCustomAttraction(attraction)
                }
                navigateToPreviousFragment()
            }
        }
        binding.exitAttBtn.setOnClickListener {
            navigateToPreviousFragment()
        }

    }
    private fun navigateToPreviousFragment(){
        val previousDestination = findNavController().previousBackStackEntry?.destination?.id
        when(previousDestination){
            R.id.favoriteFragment ->  findNavController().navigate(R.id.action_addEditFragment_to_favoriteFragment)
            else ->  findNavController().navigate(R.id.action_addEditFragment_to_attractionFragment)
        }
    }
    private fun setupChosenAttractionObserver(){
        attractionViewModel.chosenCustomAttraction.observe(viewLifecycleOwner) { attraction->
            attraction?.let {
                binding.nameEditText.editText?.setText(it.name)
                binding.locationEditText.editText?.setText(it.location)
                binding.descEditText.editText?.setText(it.description)
                binding.priceEditText.editText?.setText(it.price.toString())
                binding.openTimeBtn.text = it.openingTime
                binding.closeTimeBtn.text = it.endTime
                glide.load(Uri.parse(it.image)).into(binding.imageContainer)
                attractionViewModel.setImageUri(Uri.parse(it.image))
            }
        }
    }

    private fun openClockDialog(time: TextView){
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(8)
                .setMinute(0)
                .setTitleText(getString(R.string.select_time))
                .setInputMode(INPUT_MODE_CLOCK)
                .build()
        picker.show(parentFragmentManager, "clock")
        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val timeString = String.format("%02d:%02d", hour, minute)
            time.setText(timeString)
        }
    }
    private fun uploadImage(){
        attractionViewModel.imageUri.observe(viewLifecycleOwner){
            binding.imageContainer.setImageURI(it)
        }
        binding.imageBtn.setOnClickListener {
            pickItemLauncher.launch(arrayOf("image/*"))
        }
    }
    private fun checkTextDetails(attractionName: String,attractionAddress: String,attractionDescription: String, price: String): Boolean{
        when{
            attractionName.isEmpty() || attractionName.any { it.isDigit() }-> {
                binding.nameEditText.error =
                    "please enter attraction name with no digit"
                return false
            }
            attractionAddress.isEmpty() ->{
                binding.locationEditText.error = "please enter address"
                return false
            }
            attractionDescription.isEmpty() ->{
                binding.descEditText.error = "please make a description"
                return false
            }
            price.isEmpty() ->{
                binding.priceEditText.error = "invalid price"
                return false
            }
        }
        if(attractionViewModel.chosenCustomAttraction.value == null){
            if(attractionViewModel.customAttractionList?.value?.any { it.name == attractionName} == true)  {
                Toast.makeText(requireContext(),
                   "the attraction already exists", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }
    private fun checkTimeDetails(openingTime: String, closingTime: String): Boolean{
        when {
            openingTime == getString(R.string.opening_time) -> {
                Toast.makeText(requireContext(),
                    getString(R.string.please_enter_opening_time), Toast.LENGTH_SHORT)
                    .show()
                return false
            }

            closingTime == getString(R.string.closing_time) -> {
                Toast.makeText(requireContext(),
                    getString(R.string.please_enter_closing_time), Toast.LENGTH_SHORT)
                    .show()
                return false
            }
        }
        return true
    }
    private fun addTextWatcher(){
        binding.nameEditText.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {  }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                binding.nameEditText.error = null
            }
        })
        binding.locationEditText.editText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {  }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                binding.locationEditText.error = null
            }
        })
        binding.descEditText.editText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {  }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                binding.descEditText.error = null
            }
        })
        binding.priceEditText.editText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {  }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun afterTextChanged(p0: Editable?) {
                binding.priceEditText.error = null
            }
        })
    }


}