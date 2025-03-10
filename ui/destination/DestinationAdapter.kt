package com.example.planitgo_finalproject.ui.destination

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planitgo_finalproject.data.models.Destination
import com.example.planitgo_finalproject.databinding.DestinationDetailsCardBinding

class DestinationAdapter(private var destinationList: List<Destination>, private val listener: DestinationListener) :
RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>(){
    interface DestinationListener{
        fun onAttractionClicked(lon: Double, lat: Double)
    }
    class DestinationViewHolder(private val binding: DestinationDetailsCardBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(destination: Destination, listener: DestinationListener){
            binding.countryCardTitle.text = destination.country
            binding.cityCardTitle.text = destination.city
            Glide.with(binding.root).load(destination.flagImg).into(binding.countryCardImg)
            binding.viewAttractionBtn.setOnClickListener{
                listener.onAttractionClicked(destination.lon, destination.lat)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder =
        DestinationViewHolder(DestinationDetailsCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun getItemCount(): Int = destinationList.size
    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(destinationList[position], listener)
    }
    fun countryAt(position: Int) = destinationList[position]
}