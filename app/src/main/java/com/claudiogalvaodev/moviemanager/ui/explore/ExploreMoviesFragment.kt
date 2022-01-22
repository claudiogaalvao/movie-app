package com.claudiogalvaodev.moviemanager.ui.explore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.claudiogalvaodev.moviemanager.databinding.FragmentExploreMoviesBinding
import com.claudiogalvaodev.moviemanager.model.Movie
import com.claudiogalvaodev.moviemanager.ui.adapter.FilterAdapter
import com.claudiogalvaodev.moviemanager.ui.adapter.SimplePosterAdapter
import com.claudiogalvaodev.moviemanager.ui.filter.FiltersActivity
import com.claudiogalvaodev.moviemanager.ui.moviedetails.MovieDetailsActivity
import kotlinx.coroutines.flow.collectLatest
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class ExploreMoviesFragment: Fragment() {

    private val viewModel: ExploreMoviesViewModel by viewModel()
    private val binding by lazy {
        FragmentExploreMoviesBinding.inflate(layoutInflater)
    }

    private val filterContract = registerForActivityResult(FiltersActivity.Contract()) { result ->
        result?.let { viewModel.updateFilter(result) }
    }

    private lateinit var filtersAdapter: FilterAdapter
    private lateinit var moviesAdapter: SimplePosterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFilters()
        getMovies()
        setupAdapter()
        setupRecyclerView()
        setObservables()
    }

    private fun initFilters() {
        viewModel.initFilters()
    }

    private fun getMovies() {
        viewModel.getMoviesByCriterious()
    }

    private fun setupAdapter() {
        filtersAdapter = FilterAdapter().apply {
            onItemClick = { filter ->
                filterContract.launch(filter)
            }
        }

        moviesAdapter = SimplePosterAdapter().apply {
            onItemClick = { movie ->
                goToMovieDetails(movie)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.fragmentExploreFiltersRecyclerview.adapter = filtersAdapter

        val layout = GridLayoutManager(context, calcNumberOfColumns())
        binding.fragmentExploreMoviesRecyclerview.apply {
            layoutManager = layout
            adapter = moviesAdapter
        }
        setOnLoadMoreListener()
    }

    private fun setOnLoadMoreListener() {
        binding.fragmentExploreMoviesRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy <= 0) return

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isNotAtBeginning = firstVisibleItemPosition >= 0
                val shouldPaginate = isNotAtBeginning && isAtLastItem && isNotAtBeginning
                if(shouldPaginate && !viewModel.isLoading) {
                    getMovies()
                }
            }
        })
    }

    private fun setObservables() {
        lifecycleScope.launchWhenStarted {
            viewModel.movies.collectLatest { movies ->
                submitMoviesList(movies)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.filters.collectLatest { filters ->
                filtersAdapter.submitList(filters)
            }
        }
    }

    private fun submitMoviesList(movies: List<Movie>) {
        moviesAdapter.submitList(movies)
    }

    private fun goToMovieDetails(movie: Movie) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("movieId", movie.id)
        startActivity(intent)
    }

    private fun calcNumberOfColumns(): Int {
        val displayMetrics = resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density

        val spaceBetween = 12
        val marginStart = 16
        val marginEnd = 16
        val widthEachImage = 120

        var countImages = dpWidth - marginStart - marginEnd
        countImages /= (widthEachImage+spaceBetween)
        return countImages.roundToInt()
    }

}