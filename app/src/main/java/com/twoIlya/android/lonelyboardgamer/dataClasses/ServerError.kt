package com.twoIlya.android.lonelyboardgamer.dataClasses

import com.twoIlya.android.lonelyboardgamer.repository.ServerRepositoryResponse

data class ServerError(val code: Int, val message: String) : ServerRepositoryResponse()
