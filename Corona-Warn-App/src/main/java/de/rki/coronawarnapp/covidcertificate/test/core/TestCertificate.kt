package de.rki.coronawarnapp.covidcertificate.test.core

import de.rki.coronawarnapp.R
import de.rki.coronawarnapp.covidcertificate.common.certificate.CwaCovidCertificate
import de.rki.coronawarnapp.covidcertificate.common.certificate.TestDccV1
import de.rki.coronawarnapp.covidcertificate.common.repository.TestCertificateContainerId
import org.joda.time.Instant

interface TestCertificate : CwaCovidCertificate {
    override val containerId: TestCertificateContainerId

    /**
     * Disease or agent targeted (required)
     */
    val testType: String
    val testResult: String

    /**
     * NAA Test Name (only for PCR tests, but not required)
     */
    val testName: String?

    /**
     * RAT Test name and manufacturer (only for RAT tests, but not required)
     */
    val testNameAndManufacturer: String?
    val sampleCollectedAt: Instant?
    val sampleCollectedAtFormatted: String
    val testCenter: String?
    val registeredAt: Instant
    val isUpdatingData: Boolean
    val isCertificateRetrievalPending: Boolean

    val isPCRTestCertificate: Boolean get() = rawCertificate.isPCRTestCertificate
    val isRapidAntigenTestCertificate: Boolean get() = rawCertificate.isRapidAntigenTestCertificate

    override val rawCertificate: TestDccV1

    companion object {
        const val icon = R.drawable.ic_test_certificate
    }
}
