package de.rki.coronawarnapp.test.submission.ui

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import com.google.gson.Gson
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import de.rki.coronawarnapp.submission.data.tekhistory.TEKHistoryStorage
import de.rki.coronawarnapp.submission.data.tekhistory.TEKHistoryUpdater
import de.rki.coronawarnapp.util.coroutine.DispatcherProvider
import de.rki.coronawarnapp.util.serialization.BaseGson
import de.rki.coronawarnapp.util.ui.SingleLiveEvent
import de.rki.coronawarnapp.util.viewmodel.CWAViewModel
import de.rki.coronawarnapp.util.viewmodel.SimpleCWAViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber

class SubmissionTestFragmentViewModel @AssistedInject constructor(
    dispatcherProvider: DispatcherProvider,
    private val tekHistoryStorage: TEKHistoryStorage,
    tekHistoryUpdaterFactory: TEKHistoryUpdater.Factory,
    @BaseGson baseGson: Gson
) : CWAViewModel(dispatcherProvider = dispatcherProvider) {

    private val exportJson = baseGson.newBuilder().apply {
        setPrettyPrinting()
    }.create()

    private val tekHistoryUpdater = tekHistoryUpdaterFactory.create(
        object : TEKHistoryUpdater.Callback {
            override fun onTEKAvailable(teks: List<TemporaryExposureKey>) {
                Timber.d("TEKs are available: %s", teks)
            }

            override fun onTEKPermissionDeclined() {
                Timber.d("Permission were declined.")
            }

            override fun onTracingConsentRequired(onConsentResult: (given: Boolean) -> Unit) {
                showTracingConsentDialog.postValue(onConsentResult)
            }

            override fun onPermissionRequired(permissionRequest: (Activity) -> Unit) {
                permissionRequestEvent.postValue(permissionRequest)
            }

            override fun onError(error: Throwable) {
                errorEvents.postValue(error)
            }
        }
    )

    val errorEvents = SingleLiveEvent<Throwable>()

    val shareTEKsEvent = SingleLiveEvent<TEKExport>()

    val permissionRequestEvent = SingleLiveEvent<(Activity) -> Unit>()
    val showTracingConsentDialog = SingleLiveEvent<(Boolean) -> Unit>()

    val tekHistory: LiveData<List<TEKHistoryItem>> = tekHistoryStorage.tekData
        .map { items ->
            items.flatMap { batch ->
                batch.keys
                    .map { key ->
                        TEKHistoryItem(
                            obtainedAt = batch.obtainedAt,
                            batchId = batch.batchId,
                            key = key
                        )
                    }
            }
        }
        .map { historyItems -> historyItems.sortedBy { it.obtainedAt } }
        .asLiveData(context = dispatcherProvider.Default)

    fun updateStorage() {
        tekHistoryUpdater.updateTEKHistoryOrRequestPermission()
    }

    fun clearStorage() {
        launch {
            tekHistoryStorage.reset()
        }
    }

    fun emailTEKs() {
        launch {
            val exportedKeys = tekHistoryStorage.tekData.first().toExportedKeys()

            val tekExport = TEKExport(
                exportText = exportJson.toJson(exportedKeys)
            )
            shareTEKsEvent.postValue(tekExport)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return tekHistoryUpdater.handleActivityResult(requestCode, resultCode, data).also {
            Timber.d("tekHistoryUpdater.handleActivityResult(): %s", it)
        }
    }

    @AssistedFactory
    interface Factory : SimpleCWAViewModelFactory<SubmissionTestFragmentViewModel>
}
