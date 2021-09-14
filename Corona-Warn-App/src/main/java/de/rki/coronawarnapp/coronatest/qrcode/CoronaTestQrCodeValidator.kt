package de.rki.coronawarnapp.coronatest.qrcode

import dagger.Reusable
import de.rki.coronawarnapp.qrcode.scanner.QrCodeExtractor
import timber.log.Timber
import javax.inject.Inject

@Reusable
class CoronaTestQrCodeValidator @Inject constructor(
    raExtractor: RapidAntigenQrCodeExtractor,
    pcrExtractor: PcrQrCodeExtractor
) {
    private val extractors = setOf(raExtractor, pcrExtractor)

    suspend fun validate(rawString: String): CoronaTestQRCode {
        return findExtractor(rawString)
            ?.extract(rawString)
            ?.also { Timber.i("Extracted data from QR code is %s", it) }
            ?: throw InvalidQRCodeException()
    }

    private suspend fun findExtractor(rawString: String): QrCodeExtractor<CoronaTestQRCode>? {
        return extractors.find { it.canHandle(rawString) }
    }
}