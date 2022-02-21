package de.rki.coronawarnapp.covidcertificate.person.ui.admission

import de.rki.coronawarnapp.ccl.configuration.update.CclSettings
import de.rki.coronawarnapp.ccl.dccadmission.storage.DccAdmissionCheckScenariosRepository
import de.rki.coronawarnapp.ccl.dccwalletinfo.calculation.CclJsonFunctions
import de.rki.coronawarnapp.ccl.dccwalletinfo.calculation.DccWalletInfoCalculationManager
import de.rki.coronawarnapp.ccl.ui.text.CclTextFormatter
import de.rki.coronawarnapp.covidcertificate.person.ui.dccAdmissionCheckScenarios
import de.rki.coronawarnapp.util.serialization.SerializationModule
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import testhelpers.BaseTest
import testhelpers.extensions.InstantExecutorExtension
import testhelpers.extensions.getOrAwaitValue

@ExtendWith(InstantExecutorExtension::class)
internal class AdmissionScenariosViewModelTest : BaseTest() {

    @MockK lateinit var cclJsonFunctions: CclJsonFunctions
    @MockK lateinit var admissionCheckScenariosRepository: DccAdmissionCheckScenariosRepository
    @MockK lateinit var admissionScenariosSharedViewModel: AdmissionScenariosSharedViewModel
    @MockK lateinit var cclSetting: CclSettings
    @MockK lateinit var dccWalletInfoCalculationManager: DccWalletInfoCalculationManager
    private val mapper = SerializationModule.jacksonBaseMapper

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { admissionCheckScenariosRepository.admissionCheckScenarios } returns flowOf(dccAdmissionCheckScenarios)
        every { admissionScenariosSharedViewModel.admissionScenarios } returns flowOf(dccAdmissionCheckScenarios)

        coEvery { admissionCheckScenariosRepository.save(any()) } just Runs
        coEvery { cclSetting.setAdmissionScenarioId(any()) } returns Job()
        coEvery { dccWalletInfoCalculationManager.triggerCalculationAfterCertificateChange(any()) } returns
            DccWalletInfoCalculationManager.Result.Success
    }

    @Test
    fun getState() {
        instance().state.getOrAwaitValue().apply {
            title shouldBe "Ihr Bundesland"
            scenarios.size shouldBe 3
            scenarios[0].apply {
                identifier shouldBe "DE"
                title shouldBe "Bundesweit"
                subtitle shouldBe ""
                enabled shouldBe true
            }

            scenarios[1].apply {
                identifier shouldBe "BW"
                title shouldBe "Baden-Württemberg"
                subtitle shouldBe "Schön hier"
                enabled shouldBe true
            }

            scenarios[2].apply {
                identifier shouldBe "HE"
                title shouldBe "Hesse"
                subtitle shouldBe "Für dieses Bundesland liegen momentan keine Regeln vor"
                enabled shouldBe false
            }
        }
    }

    @Test
    fun selectScenario() {
        instance().selectScenario("admissionScenarioId")
        coVerifySequence {
            admissionCheckScenariosRepository.save(any())
            cclSetting.setAdmissionScenarioId(any())
            dccWalletInfoCalculationManager.triggerCalculationAfterCertificateChange(any())
        }
    }

    private fun instance() = AdmissionScenariosViewModel(
        format = CclTextFormatter(cclJsonFunctions, mapper),
        admissionCheckScenariosRepository = admissionCheckScenariosRepository,
        admissionScenariosSharedViewModel = admissionScenariosSharedViewModel,
        cclSettings = cclSetting,
        dccWalletInfoCalculationManager = dccWalletInfoCalculationManager
    )
}
