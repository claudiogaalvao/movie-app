package com.claudiogalvaodev.moviemanager.ui.usecases

import android.util.Log
import com.claudiogalvaodev.moviemanager.model.Company
import com.claudiogalvaodev.moviemanager.model.Movie
import com.claudiogalvaodev.moviemanager.repository.MoviesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GetMovieDetailsUseCase(
    private val repository: MoviesRepository
) {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie = _movie.asStateFlow()

    private val _companies = MutableStateFlow<List<Company>?>(emptyList())
    val companies = _companies.asStateFlow()

    suspend operator fun invoke(movieId: Int) {
        val moviesResult = repository.getDetails(movieId)
        if(moviesResult.isSuccess) {
            val movieDetails = moviesResult.getOrDefault(null)
            if(movieDetails != null) {
                Log.i("movie", movieDetails.id.toString())
                _movie.emit(movieDetails)
                _companies.emit(filterCompanies(movieDetails.production_companies))
            }
        }
    }

    private fun filterCompanies(companies: List<Company>): List<Company> {
        return companies.filter { company -> company.logo_path != null }
    }

}