package com.claudiogalvaodev.filmes.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.claudiogalvaodev.filmes.repository.MoviesRepository

class MovieDetailsViewModel(
    private val repository: MoviesRepository
): ViewModel() {

}