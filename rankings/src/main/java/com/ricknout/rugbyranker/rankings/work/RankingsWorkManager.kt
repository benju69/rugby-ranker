package com.ricknout.rugbyranker.rankings.work

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import com.ricknout.rugbyranker.core.vo.Sport
import java.util.concurrent.TimeUnit

class RankingsWorkManager {

    private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    private val mensRankingsWorkRequest = PeriodicWorkRequestBuilder<MensWorldRugbyRankingsWorker>(
            WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    private val womensRankingsWorkRequest = PeriodicWorkRequestBuilder<WomensWorldRugbyRankingsWorker>(
            WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    fun fetchAndStoreLatestWorldRugbyRankings(sport: Sport) {
        val uniqueWorkName = getRankingsUniqueWorkName(sport)
        val workRequest = when (sport) {
            Sport.MENS -> mensRankingsWorkRequest
            Sport.WOMENS -> womensRankingsWorkRequest
        }
        val workManager = WorkManager.getInstance()
        workManager.enqueueUniquePeriodicWork(uniqueWorkName, WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY, workRequest)
    }

    fun getLatestWorldRugbyRankingsWorkInfos(sport: Sport): LiveData<List<WorkInfo>> {
        val uniqueWorkName = getRankingsUniqueWorkName(sport)
        val workManager = WorkManager.getInstance()
        return workManager.getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
    }

    private fun getRankingsUniqueWorkName(sport: Sport) = when (sport) {
        Sport.MENS -> MensWorldRugbyRankingsWorker.UNIQUE_WORK_NAME
        Sport.WOMENS -> WomensWorldRugbyRankingsWorker.UNIQUE_WORK_NAME
    }

    companion object {
        private const val WORK_REQUEST_REPEAT_INTERVAL = 1L
        private val WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT = TimeUnit.DAYS
        private val WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY = ExistingPeriodicWorkPolicy.KEEP
    }
}
