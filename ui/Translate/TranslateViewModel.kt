package com.example.planitgo_finalproject.ui.Translate

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planitgo_finalproject.data.models.ModelLanguage
import com.example.planitgo_finalproject.data.repositories.TranslateRepository
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val translateRepository: TranslateRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _translatedText = MutableLiveData<String>()
    val translatedText : LiveData<String> get() = _translatedText

    private val _isDownloading = MutableLiveData<Boolean>()
    val isDownloading: LiveData<Boolean> get() = _isDownloading

    private val _sourceLanguage = MutableLiveData<String>()
    val sourceLanguage: LiveData<String> get() = _sourceLanguage

    private val _targetLanguage = MutableLiveData<String>()
    val targetLanguage: LiveData<String> get() = _targetLanguage

    private val _languages = MutableLiveData<List<ModelLanguage>>()
    val languages: LiveData<List<ModelLanguage>> get() = _languages

    private val _canChangeLanguage = MutableLiveData(true)
    val canChangeLanguage: LiveData<Boolean> get() = _canChangeLanguage

    private var translator: Translator? = null

    init {
        val languages = translateRepository.getSavedLanguages()
        loadAvailableLanguages()
        _sourceLanguage.value = languages.first
        _targetLanguage.value = languages.second
        initializeTranslator()
    }

    private fun loadAvailableLanguages(){
        val languagesList = TranslateLanguage.getAllLanguages().map { code ->
            ModelLanguage(code, Locale(code).displayLanguage)
        }
        _languages.value = languagesList
    }

    fun setSourceLanguage(language: String){
        _sourceLanguage.value = language
        translateRepository.saveLanguages(language, _targetLanguage.value ?: "en")
        initializeTranslator()
    }
    fun setTargetLanguage(language: String){
        _targetLanguage.value = language
        translateRepository.saveLanguages(_sourceLanguage.value ?: "he", language)
        initializeTranslator()
    }
    fun swapLanguages(){
        val temp = _sourceLanguage.value
        _sourceLanguage.value = _targetLanguage.value
        temp?.let {
            _targetLanguage.value = temp!!
            initializeTranslator()
        }
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
    private fun initializeTranslator() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(_sourceLanguage.value ?: TranslateLanguage.HEBREW)
            .setTargetLanguage(_targetLanguage.value ?: TranslateLanguage.ENGLISH)
            .build()
        translator = Translation.getClient(options)
        _isDownloading.value = true
        if(!isNetworkAvailable(context)){
            _isDownloading.value = false
            _translatedText.value = "No internet connection. Please check your network settings."
            return
        }
        else{
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()
            viewModelScope.launch {
                try{
                    translator?.downloadModelIfNeeded(conditions)?.addOnSuccessListener {
                        _isDownloading.value = false
                    }?.addOnFailureListener {
                        _isDownloading.value = false
                        _translatedText.value = "Language download failed."
                    }
                }catch (e: TimeoutCancellationException){
                    _isDownloading.value = false
                    _translatedText.value = "Language download failed."
                }
            }
        }
    }
    fun getTranslateText(text: String){
        translator?.translate(text)
            ?.addOnSuccessListener { translatedText ->
                _translatedText.value = translatedText
            }
            ?.addOnFailureListener { exception->
                _translatedText.value = "Translate Failed"
            }
    }
    fun setCanChangeLanguage(canChange: Boolean) {
        _canChangeLanguage.value = canChange
    }

    override fun onCleared() {
        super.onCleared()
        translator?.close()
    }
}