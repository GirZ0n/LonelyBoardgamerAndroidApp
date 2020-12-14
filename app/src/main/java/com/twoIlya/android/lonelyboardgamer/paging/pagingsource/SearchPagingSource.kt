package com.twoIlya.android.lonelyboardgamer.paging.pagingsource

import androidx.paging.PagingSource
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.twoIlya.android.lonelyboardgamer.api.ServerAPI
import com.twoIlya.android.lonelyboardgamer.dataClasses.SearchProfile
import com.twoIlya.android.lonelyboardgamer.dataClasses.Token
import com.twoIlya.android.lonelyboardgamer.repository.ServerRepository
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(
    private val serverToken: Token,
    private val api: ServerAPI
) :
    PagingSource<Int, SearchProfile>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchProfile> {
        val position = params.key ?: ServerRepository.Constants.SERVER_STARTING_PAGE_INDEX
        return try {
            val response =
                api.search(
                    "Bearer ${serverToken.value}",
                    ServerRepository.Constants.NETWORK_PAGE_SIZE, position
                )
            val profileType = object : TypeToken<List<SearchProfile>>() {}.type
            val profiles =
                Gson().fromJson<List<SearchProfile>>(
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
