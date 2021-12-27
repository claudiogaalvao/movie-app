package com.claudiogalvaodev.moviemanager.model

import com.claudiogalvaodev.moviemanager.BuildConfig
import com.claudiogalvaodev.moviemanager.utils.enum.PosterSizes

data class Company(
    val id: Int,
    val logo_path: String?,
    val name: String,
    val origin_country: String,
) {
    fun getLogoImageUrl(imageSize: PosterSizes = PosterSizes.W_500) : String {
        return "${BuildConfig.MOVIEDB_IMAGE_BASE_URL}${getPosterSize(imageSize)}$logo_path"
    }

    private fun getPosterSize(size: PosterSizes): String {
        return when(size) {
            PosterSizes.W_780 -> "w780"
            PosterSizes.W_500 -> "w500"
            PosterSizes.W_342 -> "w342"
            PosterSizes.W_185 -> "w185"
            PosterSizes.W_154 -> "w154"
            PosterSizes.W_92 -> "w92"
            else -> "original"
        }
    }
}