package com.example.planitgo_finalproject.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.data.models.UserCustomAttraction
import com.example.planitgo_finalproject.data.repositories.AttractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: AttractionRepository
) : ViewModel() {
    private val _favoriteCoordinate = MutableLiveData<Pair<Double, Double>>()
    val favoriteCoordinate: LiveData<Pair<Double, Double>> get() = _favoriteCoordinate

    fun setLonLatCoordinate(longitude: Double, latitude: Double){
        _favoriteCoordinate.value = Pair(longitude,latitude)
    }
   var favoriteCustomAttractions: LiveData<List<UserCustomAttraction>>? = _favoriteCoordinate.switchMap {
       repository.getFavoriteCustomAttractions(it.first, it.second)
   }
    var favoriteApiAttractions: LiveData<List<ApiAttraction>>? = _favoriteCoordinate.switchMap {
        repository.getFavoriteApiAttractions(it.first, it.second)
    }
}