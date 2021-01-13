package testhelpers

import dagger.Module
import de.rki.coronawarnapp.ui.contactdiary.ContactDiaryDayFragmentTestModule
import de.rki.coronawarnapp.ui.contactdiary.ContactDiaryLocationListFragmentTestModule
import de.rki.coronawarnapp.ui.contactdiary.ContactDiaryOnboardingFragmentTestModule
import de.rki.coronawarnapp.ui.contactdiary.ContactDiaryOverviewFragmentTestModule
import de.rki.coronawarnapp.ui.contactdiary.ContactDiaryPersonListFragmentTestModule
import de.rki.coronawarnapp.ui.main.home.HomeFragmentTestModule
import de.rki.coronawarnapp.ui.onboarding.OnboardingDeltaInteroperabilityFragmentTestModule
import de.rki.coronawarnapp.ui.onboarding.OnboardingFragmentTestModule
import de.rki.coronawarnapp.ui.onboarding.OnboardingNotificationsTestModule
import de.rki.coronawarnapp.ui.onboarding.OnboardingPrivacyTestModule
import de.rki.coronawarnapp.ui.onboarding.OnboardingTestFragmentModule
import de.rki.coronawarnapp.ui.onboarding.OnboardingTracingFragmentTestModule
import de.rki.coronawarnapp.ui.submission.SubmissionContactTestModule
import de.rki.coronawarnapp.ui.submission.SubmissionDispatcherTestModule
import de.rki.coronawarnapp.ui.submission.SubmissionQRScanFragmentModule
import de.rki.coronawarnapp.ui.submission.SubmissionSymptomIntroFragmentTestModule
import de.rki.coronawarnapp.ui.submission.SubmissionTanTestModule
import de.rki.coronawarnapp.ui.submission.SubmissionTestResultConsentGivenTestModule
import de.rki.coronawarnapp.ui.submission.SubmissionTestResultTestModule
import de.rki.coronawarnapp.ui.tracing.TracingDetailsFragmentTestTestModule

@Module(
    includes = [
        HomeFragmentTestModule::class,
        // Onboarding
        OnboardingFragmentTestModule::class,
        OnboardingDeltaInteroperabilityFragmentTestModule::class,
        OnboardingNotificationsTestModule::class,
        OnboardingPrivacyTestModule::class,
        OnboardingTestFragmentModule::class,
        OnboardingTracingFragmentTestModule::class,
        // Submission
        SubmissionDispatcherTestModule::class,
        SubmissionTanTestModule::class,
        SubmissionTestResultTestModule::class,
        SubmissionTestResultConsentGivenTestModule::class,
        SubmissionSymptomIntroFragmentTestModule::class,
        SubmissionContactTestModule::class,
        SubmissionQRScanFragmentModule::class,
        SubmissionQRScanFragmentModule::class,
        // Tracing
        TracingDetailsFragmentTestTestModule::class,
        // Contact Diary
        ContactDiaryOnboardingFragmentTestModule::class,
        ContactDiaryOverviewFragmentTestModule::class,
        ContactDiaryDayFragmentTestModule::class,
        ContactDiaryPersonListFragmentTestModule::class,
        ContactDiaryLocationListFragmentTestModule::class
    ]
)
class FragmentTestModuleRegistrar
