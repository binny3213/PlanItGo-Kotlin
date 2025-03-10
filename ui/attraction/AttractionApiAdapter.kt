package com.example.planitgo_finalproject.ui.attraction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.data.models.ApiAttraction
import com.example.planitgo_finalproject.databinding.AttractionApiCardBinding

class AttractionApiAdapter(private var apiAttractionList: List<ApiAttraction>, private val callback: ApiAttractionListener) : RecyclerView.Adapter<AttractionApiAdapter.AttractionViewHolder>() {
    interface ApiAttractionListener{
        fun onApiAttractionLongClick(attraction: ApiAttraction)
        fun onApiFavoriteAttractionBtn(attraction: ApiAttraction)
    }
    inner class AttractionViewHolder(private val binding: AttractionApiCardBinding) : RecyclerView.ViewHolder(binding.root) ,
    View.OnLongClickListener{
        init {
            binding.root.setOnLongClickListener(this)
        }

        override fun onLongClick(p0: View?): Boolean {
            callback.onApiAttractionLongClick(apiAttractionList[bindingAdapterPosition])
            return true
        }

        fun bind(apiAttraction: ApiAttraction){
            binding.nameTv.text = apiAttraction.name
            binding.locationTv.text = apiAttraction.address
            binding.rateTv.text = apiAttraction.rating
            Glide.with(binding.root).load(apiAttraction.icon).into(binding.imageApi)
            if(apiAttraction.isFavorite)
                binding.favoriteBtn.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.favorite))
            else binding.favoriteBtn.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.white))

            binding.favoriteBtn.setOnClickListener {
                callback.onApiFavoriteAttractionBtn(apiAttractionList[bindingAdapterPosition])
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        AttractionViewHolder(AttractionApiCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) = holder.bind(apiAttractionList[position])

    override fun getItemCount() = apiAttractionList.size
}