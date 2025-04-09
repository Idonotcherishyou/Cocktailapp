package com.example.cocktailapp.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.cocktailapp.R
import com.example.cocktailapp.databinding.FragmentDetailBinding
import com.example.cocktailapp.ui.adapter.IngredientAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var ingredientAdapter: IngredientAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupAdapter()
        observeViewModel()

        // Load cocktail details
        viewModel.loadCocktailDetails(args.cocktailId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupAdapter() {
        ingredientAdapter = IngredientAdapter()
        binding.recyclerViewIngredients.adapter = ingredientAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is DetailViewModel.UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.appBarLayout.visibility = View.GONE
                        binding.textError.visibility = View.GONE
                    }
                    is DetailViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.appBarLayout.visibility = View.VISIBLE
                        binding.textError.visibility = View.GONE

                        val cocktail = state.cocktail

                        // Set cocktail details
                        binding.collapsingToolbar.title = cocktail.name
                        binding.imageCocktail.load(cocktail.imageUrl) {
                            crossfade(true)
                            placeholder(R.drawable.placeholder_cocktail)
                            error(R.drawable.error_cocktail)
                        }

                        // Set category and glass
                        cocktail.alcoholic?.let {
                            binding.textCocktailCategory.text = it
                            binding.textCocktailCategory.visibility = View.VISIBLE
                        } ?: run {
                            binding.textCocktailCategory.visibility = View.GONE
                        }

                        cocktail.glass?.let {
                            binding.textCocktailGlass.text = it
                            binding.textCocktailGlass.visibility = View.VISIBLE
                        } ?: run {
                            binding.textCocktailGlass.visibility = View.GONE
                        }

                        // Set instructions
                        cocktail.instructions?.let {
                            binding.textInstructions.text = it
                            binding.textInstructions.visibility = View.VISIBLE
                            binding.textInstructionsLabel.visibility = View.VISIBLE
                        } ?: run {
                            binding.textInstructions.visibility = View.GONE
                            binding.textInstructionsLabel.visibility = View.GONE
                        }

                        // Set ingredients
                        val ingredients = cocktail.getIngredientsWithMeasures()
                        if (ingredients.isNotEmpty()) {
                            ingredientAdapter.submitList(ingredients)
                            binding.textIngredientsLabel.visibility = View.VISIBLE
                            binding.recyclerViewIngredients.visibility = View.VISIBLE
                        } else {
                            binding.textIngredientsLabel.visibility = View.GONE
                            binding.recyclerViewIngredients.visibility = View.GONE
                        }
                    }
                    is DetailViewModel.UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.appBarLayout.visibility = View.GONE
                        binding.textError.visibility = View.VISIBLE
                        binding.textError.text = state.message
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}