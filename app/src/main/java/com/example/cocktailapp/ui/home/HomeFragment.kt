package com.example.cocktailapp.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cocktailapp.R
import com.example.cocktailapp.data.model.Cocktail
import com.example.cocktailapp.databinding.FragmentHomeBinding
import com.example.cocktailapp.ui.adapter.CocktailAdapter
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var cocktailAdapter: CocktailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupTabs()
        setupSearch()
        observeViewModel()
    }

    private fun setupAdapter() {
        cocktailAdapter = CocktailAdapter { cocktail ->
            navigateToDetail(cocktail)
        }
        binding.recyclerView.adapter = cocktailAdapter
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val category = when (tab.position) {
                    0 -> HomeViewModel.CocktailCategory.POPULAR
                    1 -> HomeViewModel.CocktailCategory.ALL
                    2 -> HomeViewModel.CocktailCategory.ALCOHOLIC
                    3 -> HomeViewModel.CocktailCategory.NON_ALCOHOLIC
                    else -> HomeViewModel.CocktailCategory.POPULAR
                }
                viewModel.loadCocktails(category)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchCocktails(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    // Restore current category when search is cleared
                    val currentTab = binding.tabLayout.selectedTabPosition
                    val category = when (currentTab) {
                        0 -> HomeViewModel.CocktailCategory.POPULAR
                        1 -> HomeViewModel.CocktailCategory.ALL
                        2 -> HomeViewModel.CocktailCategory.ALCOHOLIC
                        3 -> HomeViewModel.CocktailCategory.NON_ALCOHOLIC
                        else -> HomeViewModel.CocktailCategory.POPULAR
                    }
                    viewModel.loadCocktails(category)
                } else {
                    viewModel.searchCocktails(newText)
                }
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is HomeViewModel.UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.textError.visibility = View.GONE
                    }
                    is HomeViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.textError.visibility = View.GONE
                        cocktailAdapter.submitList(state.cocktails)
                    }
                    is HomeViewModel.UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.textError.visibility = View.VISIBLE
                        binding.textError.text = state.message
                    }
                }
            }
        }
    }

    private fun navigateToDetail(cocktail: Cocktail) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToDetailFragment(cocktail.id)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}