package com.claudiogalvaodev.moviemanager.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.claudiogalvaodev.moviemanager.databinding.FragmentSearchBinding
import com.claudiogalvaodev.moviemanager.ui.MainActivity
import com.claudiogalvaodev.moviemanager.ui.adapter.SimplePosterAdapter
import com.claudiogalvaodev.moviemanager.ui.model.MovieModel
import com.claudiogalvaodev.moviemanager.ui.moviedetails.MovieDetailsActivity
import com.claudiogalvaodev.moviemanager.utils.extensions.launchWhenResumed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private val binding by lazy {
        FragmentSearchBinding.inflate(layoutInflater)
    }

    private lateinit var moviesAdapter: SimplePosterAdapter

    private var isSearchInitialized = false

    private val listener = ViewTreeObserver.OnGlobalLayoutListener {
        val r = Rect()
        binding.root.getWindowVisibleDisplayFrame(r)
        val screenHeight: Int = binding.root.rootView.height
        val keypadHeight = screenHeight - r.bottom
        if (keypadHeight > screenHeight * 0.15) {
            (activity as MainActivity).hideBottomNavigation()
        } else {
            (activity as MainActivity).showBottomNavigation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchFragmentEdittext.requestFocus()

        setupAdapter()
        setupRecyclerView()
        setObservers()
        setupListeners()
    }

    override fun onPause() {
        super.onPause()
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    private fun setupAdapter() {
        moviesAdapter = SimplePosterAdapter().apply {
            onItemClick = { movieId ->
                goToMovieDetails(movieId)
            }
        }
    }

    private fun setupRecyclerView() {
        val layout = GridLayoutManager(context, calcNumberOfColumns())
        binding.searchFragmentMoviesRecyclerview.apply {
            layoutManager = layout
            adapter = moviesAdapter
        }
        setOnLoadMoreListener()
    }

    private fun setObservers() {
        launchWhenResumed {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collectLatest { movies ->
                    if(movies.isEmpty() && isSearchInitialized) {
                        binding.wathingIcon.visibility = View.VISIBLE
                        binding.searchMoviesDidntFindDescription.visibility = View.VISIBLE
                    } else if(binding.wathingIcon.isVisible) {
                        binding.wathingIcon.visibility = View.GONE
                        binding.searchMoviesDidntFindDescription.visibility = View.GONE
                    }
                    submitMoviesList(movies)
                    if(viewModel.getSecondPage) viewModel.loadMoreMovies()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.searchFragmentEdittext.addTextChangedListener { editable ->
            lifecycleScope.launch {
                delay(2000)
                searchMovie(editable.toString())
            }
        }

        binding.searchFragmentEdittext.setOnEditorActionListener { textView, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMovie(textView.text.toString())
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)
            }
            false
        }
    }

    private fun searchMovie(query: String) {
        isSearchInitialized = true
        viewModel.searchMovies(query)
    }

    private fun submitMoviesList(movieModels: List<MovieModel>) {
        moviesAdapter.submitList(movieModels)
    }

    private fun setOnLoadMoreListener() {
        binding.searchFragmentMoviesRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    viewModel.loadMoreMovies()
                }
            }
        })
    }

    private fun goToMovieDetails(movieId: Int) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        intent.putExtra("movieId", movieId)
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
        val numberOfColumns = countImages.roundToInt()
        if(numberOfColumns > 4) {
            viewModel.getSecondPage = true
        }
        return numberOfColumns
    }

}