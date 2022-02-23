package de.rki.coronawarnapp.dccreissuance.core.server

import com.google.gson.Gson
import dagger.Lazy
import de.rki.coronawarnapp.dccreissuance.core.common.DccReissuanceException
import de.rki.coronawarnapp.dccreissuance.core.common.DccReissuanceException.ErrorCode
import de.rki.coronawarnapp.dccreissuance.core.server.data.DccReissuanceRequestBody
import de.rki.coronawarnapp.dccreissuance.core.server.data.DccReissuanceResponse
import de.rki.coronawarnapp.dccreissuance.core.server.validation.DccReissuanceServerCertificateValidator
import de.rki.coronawarnapp.exception.http.NetworkReadTimeoutException
import de.rki.coronawarnapp.tag
import de.rki.coronawarnapp.util.coroutine.DispatcherProvider
import de.rki.coronawarnapp.util.http.serverCertificateChain
import de.rki.coronawarnapp.util.serialization.BaseGson
import de.rki.coronawarnapp.util.serialization.fromJson
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class DccReissuanceServer @Inject constructor(
    private val dccReissuanceApiLazy: Lazy<DccReissuanceApi>,
    private val dispatcherProvider: DispatcherProvider,
    private val dccReissuanceServerCertificateValidator: DccReissuanceServerCertificateValidator,
    @BaseGson private val gson: Gson
) {

    private val dccReissuanceApi
        get() = dccReissuanceApiLazy.get()

    suspend fun requestDccReissuance(): List<DccReissuanceResponse> = withContext(dispatcherProvider.IO) {
        try {
            val dccReissuanceRequestBody = DccReissuanceRequestBody("combine", listOf("cert"))
            dccReissuanceApi.requestReissuance(dccReissuanceRequestBody).parseAndValidate()
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Failed to request Dcc Reissuance")
            throw when (e) {
                is DccReissuanceException -> throw e
                is UnknownHostException,
                is SocketTimeoutException,
                is NetworkReadTimeoutException -> ErrorCode.DCC_RI_NO_NETWORK
                else -> ErrorCode.DCC_RI_SERVER_ERR
            }.let { DccReissuanceException(errorCode = it, cause = e) }
        }
    }

    private fun Response<ResponseBody>.throwIfFailed() {
        Timber.tag(TAG).d("Checking if response=%s failed", this)

        if (isSuccessful) {
            Timber.d("Response is successful")
            return
        }

        when (code()) {
            400 -> ErrorCode.DCC_RI_400
            401 -> ErrorCode.DCC_RI_401
            403 -> ErrorCode.DCC_RI_403
            406 -> ErrorCode.DCC_RI_406
            500 -> ErrorCode.DCC_RI_500
            in 400..499 -> ErrorCode.DCC_RI_CLIENT_ERR
            else -> ErrorCode.DCC_RI_SERVER_ERR
        }.also { throw DccReissuanceException(errorCode = it) }
    }

    private fun Response<*>.validate() = dccReissuanceServerCertificateValidator.validate(raw().serverCertificateChain)

    private fun Response<ResponseBody>.parseAndValidate(): List<DccReissuanceResponse> {
        throwIfFailed()
        validate()

        return try {
            val body = checkNotNull(body()) { "Response body was null" }
            body.charStream().use { gson.fromJson(it) }
        } catch (e: Exception) {
            throw DccReissuanceException(errorCode = ErrorCode.DCC_RI_PARSE_ERR, cause = e)
        }
    }

    companion object {
        private val TAG = tag<DccReissuanceServer>()
    }
}
