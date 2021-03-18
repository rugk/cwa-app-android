package de.rki.coronawarnapp.eventregistration.checkins

import de.rki.coronawarnapp.eventregistration.storage.TraceLocationDatabase
import de.rki.coronawarnapp.eventregistration.storage.dao.CheckInDao
import de.rki.coronawarnapp.eventregistration.storage.entity.TraceLocationCheckInEntity
import de.rki.coronawarnapp.util.coroutine.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class CheckInRepository @Inject constructor(
    traceLocationDatabaseFactory: TraceLocationDatabase.Factory,
    @AppScope private val appScope: CoroutineScope
) {

    private val traceLocationDatabase: TraceLocationDatabase by lazy {
        traceLocationDatabaseFactory.create()
    }

    private val checkInDao: CheckInDao by lazy {
        traceLocationDatabase.eventCheckInDao()
    }

    val allCheckIns: Flow<List<CheckIn>> =
        checkInDao
            .allEntries()
            .map { list -> list.map { it.toCheckIn() } }

    fun addCheckIn(checkIn: CheckIn) {
        appScope.launch {
            checkInDao.insert(checkIn.toEntity())
        }
    }

    fun updateCheckIn(checkIn: CheckIn) {
        appScope.launch {
            checkInDao.update(checkIn.toEntity())
        }
    }

    fun clear() {
        appScope.launch {
            checkInDao.deleteAll()
        }
    }
}

private fun TraceLocationCheckInEntity.toCheckIn() = CheckIn(
    id = id,
    guid = guid,
    version = version,
    type = type,
    description = description,
    address = address,
    traceLocationStart = traceLocationStart,
    traceLocationEnd = traceLocationEnd,
    defaultCheckInLengthInMinutes = defaultCheckInLengthInMinutes,
    signature = signature,
    checkInStart = checkInStart,
    checkInEnd = checkInEnd,
    targetCheckInEnd = targetCheckInEnd,
    createJournalEntry = createJournalEntry
)

private fun CheckIn.toEntity() = TraceLocationCheckInEntity(
    id = id,
    guid = guid,
    version = version,
    type = type,
    description = description,
    address = address,
    traceLocationStart = traceLocationStart,
    traceLocationEnd = traceLocationEnd,
    defaultCheckInLengthInMinutes = defaultCheckInLengthInMinutes,
    signature = signature,
    checkInStart = checkInStart,
    checkInEnd = checkInEnd,
    targetCheckInEnd = targetCheckInEnd,
    createJournalEntry = createJournalEntry
)