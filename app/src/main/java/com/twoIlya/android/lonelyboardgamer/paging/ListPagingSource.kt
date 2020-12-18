package com.twoIlya.android.lonelyboardgamer.paging

import androidx.paging.PagingSource
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.twoIlya.android.lonelyboardgamer.api.ServerResponse
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import retrofit2.HttpException
import java.io.IOException

class ListPagingSource<T : Any>(
    private val classType: Class<T>,
    private val apiMethod: suspend (limit: Int, offset: Int) -> ServerResponse,
) :
    PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val position = params.key ?: ServerRepository.Constants.SERVER_STARTING_PAGE_INDEX
        return try {
            val response = apiMethod(ServerRepository.Constants.NETWORK_PAGE_SIZE, position)
            val profileType = TypeToken.getParameterized(List::class.java, classType).type
            val profiles =
                Gson().fromJson<List<T>>(
                    response.message.toString(),
                    profileType
                )
            LoadResult.Page(
                data = profiles,
                prevKey = if (position == ServerRepository.Constants.SERVER_STARTING_PAGE_INDEX) null else position - ServerRepository.Constants.NETWORK_PAGE_SIZE,
                nextKey = if (profiles.isEmpty()) null else position + ServerRepository.Constants.NETWORK_PAGE_SIZE,
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: JsonSyntaxException) {
            LoadResult.Error(exception)
        } catch (exception: NullPointerException) {
            LoadResult.Error(exception)
        }
    }
}
