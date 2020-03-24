package io.petros.movies.network.rest

import io.petros.movies.domain.interactor.movie.LoadMoviesUseCase
import io.petros.movies.domain.model.movie.MoviesResultPage
import io.petros.movies.network.WebService
import io.petros.movies.utils.releaseDateGte
import io.petros.movies.utils.releaseDateLte
import java.net.ConnectException
import java.net.UnknownHostException

class RestClient(
    private val restApi: RestApi
) : WebService {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun loadMovies(year: Int?, month: Int?, page: Int?): MoviesResultPage {
        try {
            return restApi.loadMovies(
                releaseDateGte(year, month),
                releaseDateLte(year, month),
                page
            ).toMoviesResultPage()
        } catch (e: Exception) {
            when (e) {
                is UnknownHostException -> throw LoadMoviesUseCase.Error(e)
                is ConnectException -> throw LoadMoviesUseCase.Error(e)
                else -> throw e
            }
        }
    }

}