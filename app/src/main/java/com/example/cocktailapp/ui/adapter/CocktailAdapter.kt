package com.example.cocktailapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cocktailapp.R
import com.example.cocktailapp.data.model.Cocktail
import com.example.cocktailapp.databinding.ItemCocktailBinding

class CocktailAdapter(
    private val onCocktailClicked: (Cocktail) -> Unit
) : ListAdapter<Cocktail, CocktailAdapter.CocktailViewHolder>(CocktailDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        val binding = ItemCocktailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CocktailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CocktailViewHolder(
        private val binding: ItemCocktailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCocktailClicked(getItem(position))
                }
            }
        }

        fun bind(cocktail: Cocktail) {
            binding.apply {
                textCocktailName.text = cocktail.name

                // Load image with Coil
                imageCocktail.load(cocktail.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_cocktail)
                    error(R.drawable.error_cocktail)
                }

                // Show if alcoholic or not if available
                cocktail.alcoholic?.let {
                    textCocktailCategory.text = it
                    textCocktailCategory.visibility = android.view.View.VISIBLE
                } ?: run {
                    textCocktailCategory.visibility = android.view.View.GONE
                }
            }
        }
    }

    object CocktailDiffCallback : DiffUtil.ItemCallback<Cocktail>() {
        override fun areItemsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
            return oldItem == newItem
        }
    }
}