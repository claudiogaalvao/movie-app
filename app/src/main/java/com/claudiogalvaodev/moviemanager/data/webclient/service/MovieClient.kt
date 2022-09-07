package com.claudiogalvaodev.moviemanager.data.webclient.service

import com.claudiogalvaodev.moviemanager.data.webclient.dto.movie.CollectionDto
import com.claudiogalvaodev.moviemanager.data.webclient.dto.movie.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieClient {

    @GET("trending/movie/week")
    suspend fun getTrendingWeek(): Response<MoviesResponseDto>

    @GET("movie/upcoming")
    suspend fun getUpComing(): Response<MoviesResponseDto>

    @GET("movie/now_playing")
    suspend fun getPlayingNow(@Query("page") page: Int = 1): Response<MoviesResponseDto>

    @GET("movie/{id}")
    suspend fun getDetails(
        @Path("id") id: Int,
        @Query("append_to_response") append: String = "videos"
    ): Response<MovieDto>

    @GET("movie/{id}/videos")
    suspend fun getVideos(
        @Path("id") movieId: Int
    ): Response<VideosResponseDto>

    @GET("movie/{id}/credits")
    suspend fun getCredits(@Path("id") id: Int): Response<CreditsDto>

    @GET("movie/{id}/watch/providers")
    suspend fun getProviders(@Path("id") id: Int): Response<ProvidersResponseDto>

    @GET("collection/{id}")
    suspend fun getCollection(@Path("id") id: Int): Response<CollectionDto>

    @GET("genre/movie/list")
    suspend fun getAllGenre(): Response<GenresResponseDto>

    @GET("person/popular")
    suspend fun getAllPopularPeople(@Query("page") page: Int): Response<PersonResponseDto>

    @GET("discover/movie")
    suspend fun getMoviesByCriterious(
        @Query("page") page: Int,
        @Query("release_date.lte") currentDate: String,
        @Query("sort_by") sortBy: String,
        @Query("with_genres") withGenres: String,
        @Query("vote_count.gte") voteCount: Int,
        @Query("with_people") withPeople: String,
        @Query("year") year: String
    ): Response<MoviesResponseDto>

    @GET("person/{id}")
    suspend fun getPersonDetails(@Path("id") id: String): Response<PersonDto>

    @GET("search/movie")
    suspend fun searchMovie(
        @Query("page") page: Int,
        @Query("query") query: String,
    ): Response<MoviesResponseDto>

    @GET("search/person")
    suspend fun searchPerson(
        @Query("page") page: Int,
        @Query("query") query: String,
    ): Response<PeopleResponseDto>
}