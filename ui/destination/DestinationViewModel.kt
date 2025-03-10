package com.example.planitgo_finalproject.ui.destination

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.planitgo_finalproject.data.models.CountryDetails
import com.example.planitgo_finalproject.data.models.Destination
import com.example.planitgo_finalproject.data.models.GeoapifyResponse
import com.example.planitgo_finalproject.data.repositories.DestinationRepository
import com.example.planitgo_finalproject.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationViewModel @Inject constructor(
    private val repository: DestinationRepository
) : ViewModel() {
    val countryList: LiveData<List<Destination>> = repository.getAllDestinations()

    private val _selectedCountryCoordinate = MutableLiveData<Pair<Double, Double>>()
    val selectedCountryCoordinate : LiveData<Pair<Double, Double>> get() = _selectedCountryCoordinate

    private val _searchQuery = MutableLiveData<String>()
    val suggestions: LiveData<Resource<GeoapifyResponse>> = _searchQuery.switchMap { query ->
        if (query.length >= 3) {
            repository.getDestinationDetailsApi(query)
        } else {
            MutableLiveData()
        }
    }

    fun getSuggestionsDestinationQuery(query: String) {
        _searchQuery.value = query
    }

    fun addDestination(countryDetail: CountryDetails){
        viewModelScope.launch {
            repository.fetchFlagAndSaveDestination(countryDetail)
        }
    }
    fun deleteDestination(destination:Destination){
        viewModelScope.launch {
            repository.deleteDestination(destination)
        }
    }
    fun deleteAttractionFromDestination(lon: Double, lat:Double){
        viewModelScope.launch {
            repository.deleteAttractionFromDestination(lon,lat)
        }
    }
    fun setSelectedCountryCoordinate(lon: Double, lat:Double){
        _selectedCountryCoordinate.value = Pair(lon,lat)
    }
}