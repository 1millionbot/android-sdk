package com.onemillionbot.sdk.repository.mappers

import com.onemillionbot.sdk.entities.Attended
import com.onemillionbot.sdk.network.dtos.AttendedDTO
import com.onemillionbot.sdk.network.dtos.StatusDTO
import com.onemillionbot.sdk.network.dtos.StatusWrapperDTO
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi

class StatusMapper(
    private val moshi: Moshi
) {
    fun asDto(json: String, userName: String): StatusDTO {
        return try {
            val adapterWrapper = moshi.adapter(StatusWrapperDTO::class.java)
            val jsonStatus = adapterWrapper.fromJson(json)!!.status
            val adapter = moshi.adapter(StatusDTO::class.java)
            adapter.fromJson(jsonStatus)!!
        } catch (e: JsonDataException) {
            // This happen when the chat has been created and there are no messages.
            StatusDTO(
                online = true,
                typing = false,
                userName = userName,
                deleted = false,
                attended = null,
                origin = null
            )
        }
    }

    fun asDto(json: String): StatusDTO {
        val adapter = moshi.adapter(StatusDTO::class.java)
        val status = adapter.fromJson(json)!!
        return status
    }

    fun asEntity(dto: AttendedDTO) = with(dto) {
        Attended(url = image, name = name.orEmpty())
    }
}
