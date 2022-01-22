package com.claudiogalvaodev.moviemanager.ui.explore

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.claudiogalvaodev.moviemanager.R
import com.claudiogalvaodev.moviemanager.model.Filter
import com.claudiogalvaodev.moviemanager.model.Movie
import com.claudiogalvaodev.moviemanager.ui.usecases.GetMoviesByCriteriousUseCase
import com.claudiogalvaodev.moviemanager.utils.OrderByConstants
import com.claudiogalvaodev.moviemanager.utils.enum.FilterType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExploreMoviesViewModel(
    private val context: Context,
    private val getMoviesByCriteriousUseCase: GetMoviesByCriteriousUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    private val _filters = MutableStateFlow<List<Filter>>(emptyList())
    val filters = _filters.asStateFlow()

    private val _movies = MutableStateFlow<List<Movie>>(mutableListOf())
    val movies = _movies.asStateFlow()

    var isLoading: Boolean = false
    var isUpdate: Boolean = false

    fun initFilters() = viewModelScope.launch {
        withContext(dispatcher) {
            val filters = listOf(
                Filter(type = FilterType.SORT_BY,
                    name = context.resources.getString(R.string.filter_type_orderby),
                    currentValue = OrderByConstants.POPULARITY_DESC),
                Filter(type = FilterType.GENRES,
                    name = context.resources.getString(R.string.filter_type_genders),
                    currentValue = ""),
                Filter(type = FilterType.ACTORS,
                    name = context.resources.getString(R.string.filter_type_actors),
                    currentValue = ""),
                Filter(type = FilterType.DIRECTORS,
                    name = context.resources.getString(R.string.filter_type_directors),
                    currentValue = ""),
                Filter(type = FilterType.COMPANIES,
                    name = context.resources.getString(R.string.filter_type_companies),
                    currentValue = ""),
                Filter(type = FilterType.YEARS,
                    name = context.resources.getString(R.string.filter_type_years),
                    currentValue = "")
            )
            _filters.emit(filters)
        }
    }

    fun getMoviesByCriterious() = viewModelScope.launch {
        withContext(dispatcher) {
            isLoading = true
            val moviesResult = getMoviesByCriteriousUseCase.invoke(_filters.value, isUpdate)

            if(moviesResult.isSuccess) {
                moviesResult.getOrNull()?.let { movies ->
                    if(isUpdate) {
                        _movies.emit(movies)
                        isLoading = false
                        isUpdate = false
                        return@withContext
                    }
                    val moviesList = mutableListOf<Movie>()
                    moviesList.addAll(_movies.value)
                    moviesList.addAll(movies)
                    _movies.emit(moviesList)
                }
            }
            isLoading = false
            isUpdate = false
        }
    }

    fun updateFilter(newFilter: Filter) = viewModelScope.launch {
        withContext(dispatcher) {
            val newFilters = _filters.value.subList(0, _filters.value.size)
            newFilters.map { filter ->
                if (filter.type == newFilter.type) {
                    if(filter.currentValue == newFilter.currentValue) return@withContext
                    filter.currentValue = newFilter.currentValue
                }
            }
            isUpdate = true
            _filters.emit(newFilters)
            getMoviesByCriterious()
        }
    }

}