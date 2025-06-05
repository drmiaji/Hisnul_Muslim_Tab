package com.drmiaji.hisnulmuslimtab.utils

import android.app.Activity
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

object UpdateManager {

    private const val REQUEST_CODE_UPDATE = 1234
    private lateinit var appUpdateManager: AppUpdateManager

    fun checkForAppUpdate(activity: Activity) {
        appUpdateManager = AppUpdateManagerFactory.create(activity)
        val updateInfoTask = appUpdateManager.appUpdateInfo

        updateInfoTask.addOnSuccessListener { updateInfo: AppUpdateInfo ->
            if (
                updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // New approach using startUpdateFlow without result callback
                appUpdateManager.startUpdateFlow(
                    updateInfo,
                    activity,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                ).addOnSuccessListener {
                    // Update started successfully
                    Log.d("UpdateManager", "Update flow started successfully")
                    // Register listener after starting update
                    registerUpdateListener(activity)
                }.addOnFailureListener { exception ->
                    Log.e("UpdateManager", "Failed to start update flow", exception)
                }
            }
        }.addOnFailureListener {
            Log.e("UpdateManager", "Failed to check for updates", it)
        }
    }

    // If you need to handle the update completion, register a listener
    fun registerUpdateListener(activity: Activity) {
        val listener = InstallStateUpdatedListener { installState ->
            when (installState.installStatus()) {
                InstallStatus.DOWNLOADED -> showUpdateCompleteSnackbar(activity)
                InstallStatus.INSTALLED -> Log.d("UpdateManager", "Update installed successfully")
                InstallStatus.FAILED -> Log.e("UpdateManager", "Update failed")
                else -> Log.d("UpdateManager", "Other status: ${installState.installStatus()}")
            }
        }

        appUpdateManager.registerListener(listener)
    }

    // Method to check and complete downloaded updates
    fun completeUpdateIfDownloaded(activity: Activity) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateCompleteSnackbar(activity)
            }
        }
    }

    private fun showUpdateCompleteSnackbar(activity: Activity) {
        val rootView = activity.findViewById<View>(android.R.id.content)
        Snackbar.make(
            rootView,
            "Update downloaded. Restart to apply.",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("RESTART") {
            appUpdateManager.completeUpdate()
        }.show()
    }
}