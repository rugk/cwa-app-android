package de.rki.coronawarnapp.ui.interoperability

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import de.rki.coronawarnapp.R
import de.rki.coronawarnapp.databinding.FragmentInteroperabilityConfigurationBinding
import de.rki.coronawarnapp.util.di.AutoInject
import de.rki.coronawarnapp.util.ui.observe2
import de.rki.coronawarnapp.util.ui.popBackStack
import de.rki.coronawarnapp.util.ui.viewBinding
import de.rki.coronawarnapp.util.viewmodel.CWAViewModelFactoryProvider
import de.rki.coronawarnapp.util.viewmodel.cwaViewModels
import javax.inject.Inject

class InteroperabilityConfigurationFragment :
    Fragment(R.layout.fragment_interoperability_configuration), AutoInject {

    @Inject lateinit var viewModelFactory: CWAViewModelFactoryProvider.Factory
    private val vm: InteroperabilityConfigurationFragmentViewModel by cwaViewModels { viewModelFactory }

    private val binding: FragmentInteroperabilityConfigurationBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.countryList.observe2(this) {
            binding.countryData = it
        }

        vm.saveInteroperabilityUsed()
        vm.navigateBack.observe2(this) {
            if (it) {
                popBackStack()
            }
        }

        binding.toolbar.setNavigationOnClickListener { popBackStack() }
        binding.noCountriesRiskdetailsInfoview.riskDetailsOpenSettingsButton.setOnClickListener {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            } else {
                Intent(Settings.ACTION_SETTINGS)
            }
            startActivity(intent)
        }
    }
}
