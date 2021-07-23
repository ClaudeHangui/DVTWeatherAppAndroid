package com.changui.dvtweatherappandroid.presentation

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import com.changui.dvtweatherappandroid.R
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.material.snackbar.Snackbar

internal fun Context.showRationaleDialog(
    permissions: List<PermissionStatus>,
    request: PermissionRequest
) {
    // val msg = getString(R.string.rationale_permissions, permissions.toMessage<PermissionStatus.Denied.ShouldShowRationale>())
    val msg = "To provide the weather conditions where you're found, we need to get access to your current location"

    AlertDialog.Builder(this)
        .setTitle(R.string.permissions_required)
        .setMessage(msg)
        .setPositiveButton(R.string.request_again) { _, _ ->
            // Send the request again.
            request.send()
        }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}

internal fun Context.showPermanentlyDeniedDialog(permissions: List<PermissionStatus>) {
    // val msg = getString(R.string.permanently_denied_permissions, permissions.toMessage<PermissionStatus.Denied.Permanently>())
    val msg = "To provide the weather conditions where you're found, we need to get access to your current location"

    AlertDialog.Builder(this)
        .setTitle(R.string.permissions_required)
        .setMessage(msg)
        .setPositiveButton(R.string.action_settings) { _, _ ->
            // Open the app's settings.
            val intent = createAppSettingsIntent()
            startActivity(intent)
        }
        .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _ ->
            dialog.dismiss()
            closeApp(this)
        }
        .show()
}

internal fun Context.showGrantedToast(permissions: List<PermissionStatus>) {
    val msg = getString(
        R.string.granted_permissions,
        permissions.toMessage<PermissionStatus.Granted>()
    )
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

private inline fun <reified T : PermissionStatus> List<PermissionStatus>.toMessage(): String = filterIsInstance<T>()
    .joinToString { it.permission }

private fun createAppSettingsIntent() = Intent().apply {
    action = Settings.ACTION_SETTINGS
}

internal fun Fragment.showSnackBar(message: String) {
    if (view != null) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(android.R.string.ok)) { }
            .show()
    }
}

internal fun closeApp(context: Context) {
    if (Build.VERSION.SDK_INT in 16..20) {
        finishAffinity(context as Activity)
    } else if (Build.VERSION.SDK_INT >= 21) {
        (context as Activity).finishAndRemoveTask()
    }
}