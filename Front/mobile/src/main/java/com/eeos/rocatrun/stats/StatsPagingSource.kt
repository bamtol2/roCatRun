package com.eeos.rocatrun.stats

import androidx.paging.PagingSource
import androidx.paging.PagingState

class StatsPagingSource(
    private val games: List<Game>
) : PagingSource<Int, Game>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Game> {
        return try {
            val currentPage = params.key ?: 0  // 첫 페이지는 0
            val pageSize = 4  // 한 번에 로드할 개수
            val startIndex = currentPage * pageSize
            val endIndex = minOf(startIndex + pageSize, games.size)

            val pagedGames = games.subList(startIndex, endIndex)

            LoadResult.Page(
                data = pagedGames,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (endIndex < games.size) currentPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Game>): Int? {
        return state.anchorPosition
    }
}