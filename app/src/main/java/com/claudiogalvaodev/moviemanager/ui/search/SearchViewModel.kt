package com.claudiogalvaodev.moviemanager.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudiogalvaodev.moviemanager.data.model.Movie
import com.claudiogalvaodev.moviemanager.ui.usecases.SearchMoviesUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(mutableListOf())
    val movies = _movies.asStateFlow()

    private var lastQuery = ""

    var isLoading: Boolean = false
    var isUpdate: Boolean = true

    fun searchMovies(query: String) = viewModelScope.launch {
        withContext(dispatcher) {
            if(isUpdate) {
                if(query == lastQuery) return@withContext
                lastQuery = query
            }

            isLoading = true
            val moviesResult = searchMoviesUseCase.invoke(query, isUpdate)

            if(moviesResult.isSuccess) {
                moviesResult.getOrNull()?.let { movies ->
                    if(isUpdate) {
                        _movies.emit(movies)
                        isLoading = false
                        isUpdate = true
                        return@withContext
                    }
                    val moviesList = mutableListOf<Movie>()
                    moviesList.addAll(_movies.value)
                    moviesList.addAll(movies)
                    _movies.emit(moviesList)
                }
                isLoading = false
                isUpdate = true
            }
        }
    }

    fun loadMoreMovies() {
        isUpdate = false
        searchMovies(lastQuery)
    }

}