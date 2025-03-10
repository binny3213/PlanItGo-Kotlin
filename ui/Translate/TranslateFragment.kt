package com.example.planitgo_finalproject.ui.Translate
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.databinding.TranslateLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TranslateFragment : Fragment() {
    private var _binding: TranslateLayoutBinding? = null
    private val binding get() = _binding!!
    private val translateViewModel: TranslateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TranslateLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        translateViewModel.sourceLanguage.observe(viewLifecycleOwner){language->
            binding.sourceLanguageBtn.text = language
        }
        translateViewModel.targetLanguage.observe(viewLifecycleOwner){language->
            binding.targetLanguageBtn.text = language
        }
        translateViewModel.translatedText.observe(viewLifecycleOwner){translateText->
            binding.translatedTextView.text = translateText
        }
        isLanguageDownloadingObserver()
        canChangeLanguageObserver()
        setSourceLanguageBtnListener()
        setTargetLanguageListener()
        setTranslateDirectionBtnListener()
        setTranslateBtnListener()
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun openLanguageMenu(languageBtn: Button){
        val popupMenu = PopupMenu(requireContext(), languageBtn)
        translateViewModel.languages.observe(viewLifecycleOwner) { languages ->
            if (languages.isNotEmpty()) {
                for (i in languages.indices) {
                    popupMenu.menu.add(Menu.NONE, i, i, languages[i].languageTitle)
                }
                popupMenu.show()
            }
            popupMenu.setOnMenuItemClickListener { menuItem->
                val position = menuItem.itemId
                when(languageBtn){
                    binding.sourceLanguageBtn -> translateViewModel.setSourceLanguage(languages[position].languageCode)
                    else -> translateViewModel.setTargetLanguage(languages[position].languageCode)
                }
                false
            }
        }
    }
    private fun isLanguageDownloadingObserver(){
        translateViewModel.isDownloading.observe(viewLifecycleOwner) {isDownloading ->
            if(isDownloading){
                binding.translatePb.visibility = View.VISIBLE
                binding.loadingLanguageTv.visibility = View.VISIBLE
            }
            else
            {
                binding.translatePb.visibility = View.GONE
                binding.loadingLanguageTv.visibility = View.GONE
            }
        }
    }
    private fun canChangeLanguageObserver(){
        translateViewModel.canChangeLanguage.observe(viewLifecycleOwner){canChange->
            binding.sourceLanguageBtn.isEnabled = canChange
            binding.targetLanguageBtn.isEnabled = canChange
        }
    }
    private fun setSourceLanguageBtnListener(){
        binding.sourceLanguageBtn.setOnClickListener {
            openLanguageMenu(binding.sourceLanguageBtn)
        }
    }
    private fun setTargetLanguageListener(){
        binding.targetLanguageBtn.setOnClickListener {
            openLanguageMenu(binding.targetLanguageBtn)
        }

    }
    private fun setTranslateDirectionBtnListener(){
        binding.translateDirectionBtn.setOnClickListener {
            val newRotation = if(binding.translateDirectionBtn.rotation == 0F){
                translateViewModel.setCanChangeLanguage(false)
                180F
            } else{
                translateViewModel.setCanChangeLanguage(true)
                0F
            }
            binding.translateDirectionBtn.rotation = newRotation
            translateViewModel.swapLanguages()
            binding.editTextTranslate.text.clear()
            binding.translatedTextView.text = ""
        }
    }
    private fun setTranslateBtnListener(){
        binding.translateBtn.setOnClickListener {
            if(binding.editTextTranslate.text.isEmpty()){
                Toast.makeText(requireContext(),
                    getString(R.string.enter_text_to_translate), Toast.LENGTH_SHORT).show()
            }
            else{
                translateViewModel.getTranslateText(binding.editTextTranslate.text.toString())
            }
        }
    }
}