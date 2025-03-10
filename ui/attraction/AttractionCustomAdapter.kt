package com.example.planitgo_finalproject.ui.attraction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.planitgo_finalproject.data.models.UserCustomAttraction
import com.bumptech.glide.Glide
import com.example.planitgo_finalproject.R
import com.example.planitgo_finalproject.databinding.AttractionCustomCardBinding

class AttractionCustomAdapter(private var attractionList: List<UserCustomAttraction>, private val callback: AttractionListener) : RecyclerView.Adapter<AttractionCustomAdapter.AttractionCustomViewHolder>() {
    interface AttractionListener {
        fun onAttractionLongClick(attraction: UserCustomAttraction)
        fun onEditAttractionBtn(attraction: UserCustomAttraction)
        fun onFavoriteAttractionBtn(attraction: UserCustomAttraction)
    }

    inner class AttractionCustomViewHolder(private val binding: AttractionCustomCardBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnLongClickListener {
        init {
            binding.root.setOnLongClickListener(this)
        }

        fun bind(attraction: UserCustomAttraction) {
            binding.nameAttraction.text = attraction.name
            binding.priceAttraction.text = attraction.price.toString() + "$"
            binding.locationAttraction.text = attraction.location
            Glide.with(binding.root).load(attraction.image).into(binding.imageAttraction)
            if(attraction.isFavorite)
                binding.favoriteAttractionBtn.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.favorite))
            else binding.favoriteAttractionBtn.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.white))

            binding.editAttractionBtn.setOnClickListener {
                callback.onEditAttractionBtn(attractionList[bindingAdapterPosition])
            }
            binding.favoriteAttractionBtn.setOnClickListener {
                callback.onFavoriteAttractionBtn(attractionList[bindingAdapterPosition])
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            callback.onAttractionLongClick(attractionList[bindingAdapterPosition])
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AttractionCustomViewHolder(AttractionCustomCardBinding.inflate(
        LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = attractionList.size

    override fun onBindViewHolder(holder: AttractionCustomViewHolder, position: Int) =
        holder.bind(attractionList[position])

    fun attractionAt(position: Int) = attractionList[position]
    fun updateList(newAttractions: List<UserCustomAttraction>) {
        attractionList = newAttractions
        notifyDataSetChanged()
    }
}