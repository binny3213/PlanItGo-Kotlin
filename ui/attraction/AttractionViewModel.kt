package com.example.planitgo_finalproject.ui.attraction

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.UserCustomAttraction
import com.example.planitgo_finalproject.data.repositories.AttractionRepository
import com.example.planitgo_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionViewModel @Inject constructor(
    private val attractionRepository: AttractionRepository
) : ViewModel() {
    private val _coordinate = MutableLiveData<Pair<Double, Double>>()
    val coordinate: LiveData<Pair<Double, Double>> get() = _coordinate

    private val _imageUri = MutableLiveData<Uri?>().apply {
        value = Uri.parse("android.resource://com.example.planitgo_finalproject/drawable/airplain1")
    }
    val imageUri : LiveData<Uri?> get() = _imageUri

    var filteredAttractions: List<UserCustomAttraction> = emptyList()

    private val _chosenCustomAttraction = MutableLiveData<UserCustomAttraction?>()
    val chosenCustomAttraction : LiveData<UserCustomAttraction?> get() = _chosenCustomAttraction

    var attractionFullDetails : LiveData<Resource<ApiAttraction>> = MutableLiveData()

    var customAttractionList: LiveData<List<UserCustomAttraction>>? = _coordinate.switchMap {
        attractionRepository.getCustomAttractions(it.first,it.second) }

    val allApiAttractions = _coordinate.switchMap {
        attractionRepository.getAllApiAttractions(it.first, it.second)
    }

    fun searchAttractions(query: String) {
        filteredAttractions = if (query.isEmpty()) {
            customAttractionList?.value ?: emptyList()
        } else {
            customAttractionList?.value?.filter {
                it.name.contains(query, ignoreCase = true)
            } ?: emptyList()
        }
    }

    fun setLonLatCoordinate(longitude: Double, latitude: Double){
        _coordinate.value = Pair(longitude,latitude)
    }
    fun setImageUri(uri: Uri?){
        _imageUri.value = uri
    }
    fun addCustomAttraction(attraction: UserCustomAttraction){
        viewModelScope.launch {
            attractionRepository.addCustomAttraction(attraction)
        }
    }
    fun updateCustomAttraction(attraction: UserCustomAttraction){
        viewModelScope.launch {
            attractionRepository.updateCustomAttraction(attraction)
        }
    }
    private fun updateApiAttraction(attraction: ApiAttraction){
        viewModelScope.launch {
            attractionRepository.updateApiAttraction(attraction)
        }
    }
    fun deleteCustomAttraction(attraction: UserCustomAttraction){
        viewModelScope.launch {
            attractionRepository.deleteCustomAttraction(attraction)
        }
    }
    fun setCustomAttraction(attraction: UserCustomAttraction?){
        _chosenCustomAttraction.value = attraction
    }

    fun setApiAttraction(attraction: ApiAttraction?){
        attractionFullDetails = if (attraction != null) {
            attractionRepository.getAttractionDetails(attraction.placeId, attraction)
        }else{
            MutableLiveData()
        }
    }
    fun setFavoriteAttraction(attraction: Any) {
        if(attraction is UserCustomAttraction){
            attraction.isFavorite = !attraction.isFavorite
            updateCustomAttraction(attraction)
        }
        else if(attraction is ApiAttraction){
            attraction.isFavorite = !attraction.isFavorite
            updateApiAttraction(attraction)
        }
    }
}