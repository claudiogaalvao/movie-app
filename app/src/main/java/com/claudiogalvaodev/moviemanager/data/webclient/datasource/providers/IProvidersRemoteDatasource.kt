package com.claudiogalvaodev.moviemanager.data.webclient.datasource.providers

import com.claudiogalvaodev.moviemanager.ui.model.ProviderModel


interface IProvidersRemoteDatasource {

    suspend fun getPopularProviders(): Result<List<ProviderModel>>

}