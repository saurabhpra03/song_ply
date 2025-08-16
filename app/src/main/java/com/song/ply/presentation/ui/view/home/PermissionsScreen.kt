package com.song.ply.presentation.ui.view.home

import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.song.ply.R
import com.song.ply.presentation.ui.theme.Dimen
import com.song.ply.presentation.ui.viewModel.PlayerManagerViewModel
import com.song.ply.presentation.ui.viewModel.SongsViewModel
import com.song.ply.presentation.utils.Const.goToAppSettings
import com.song.ply.presentation.utils.Const.hasPermission
import com.song.ply.presentation.utils.NeededPermission

@Composable
fun PermissionsScreen(
    activity: Activity,
    navController: NavController,
    showMiniPlayer: MutableState<Boolean>,
    selectedSongPath: MutableState<String>,
    viewModel: SongsViewModel,
    playerManagerViewModel: PlayerManagerViewModel
) {
    val context = LocalContext.current

    // Set of permissions needed, based on Android version
    val listOfPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                NeededPermission.READ_MEDIA_AUDIO,
                NeededPermission.POST_NOTIFICATIONS
            )
        } else {
            listOf(NeededPermission.READ_EXTERNAL_STORAGE)
        }
    }

    var isPermissionGranted by remember { mutableStateOf(false) }
    val deniedPermissions = remember { mutableStateListOf<NeededPermission>() }

    // Multiple permissions launcher
    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            val denied = listOfPermissions.filterNot {
                result[it.permission] == true
            }

            if (denied.isEmpty()) {
                isPermissionGranted = true
            } else {
                deniedPermissions.clear()
                deniedPermissions.addAll(denied)
            }
        }
    )

    // Request permissions when composable launches
    LaunchedEffect(Unit) {
        val notGranted = listOfPermissions.filterNot {
            context.hasPermission(it.permission)
        }

        if (notGranted.isEmpty()) {
            isPermissionGranted = true
        } else {
            multiplePermissionsLauncher.launch(notGranted.map { it.permission }.toTypedArray())
        }
    }

    // Recheck permissions when returning from App Settings
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val missingPermissions = listOfPermissions.filterNot {
                    context.hasPermission(it.permission)
                }

                if (missingPermissions.isEmpty()) {
                    isPermissionGranted = true
                    viewModel.fetchSongs()
                } else {
                    deniedPermissions.clear()
                    deniedPermissions.addAll(missingPermissions)
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    when {
        isPermissionGranted -> HomeScreen(context, navController, showMiniPlayer, selectedSongPath, viewModel, playerManagerViewModel)

        deniedPermissions.isNotEmpty() -> {
            val currentPermission = deniedPermissions.first()

            PermissionHandler(activity, currentPermission) { permission ->
                multiplePermissionsLauncher.launch(arrayOf(permission.permission))
            }
        }
    }
}

@Composable
private fun PermissionHandler(
    activity: Activity,
    currentPermission: NeededPermission,
    onLauncherPermission: (NeededPermission) -> Unit
){
    PermissionUI(currentPermission, activity) { isPermissionDeclined, permission ->
        if (isPermissionDeclined) activity.goToAppSettings() else onLauncherPermission(permission)
    }
}

@Composable
private fun PermissionUI(
    permissionDialog: NeededPermission?,
    activity: Activity,
    onClick: (Boolean, NeededPermission) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        permissionDialog?.let { permission ->
            val isPermissionDeclined =
                !activity.shouldShowRequestPermissionRationale(permission.permission)


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(Dimen.dimen20),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .size(Dimen.dimen100),
                    imageVector = Icons.Outlined.AudioFile,
                    contentDescription = "Audio file",
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    modifier = Modifier
                        .padding(top = Dimen.dimen50),
                    text = permission.title,
                    style = MaterialTheme.typography.headlineMedium
                        .copy(color = MaterialTheme.colorScheme.primary),
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier
                        .padding(top = Dimen.dimen20),
                    text = permission.permissionTextProvider(isPermissionDeclined),
                    style = MaterialTheme.typography.bodyLarge
                        .copy(color = MaterialTheme.colorScheme.onBackground),
                    textAlign = TextAlign.Center
                )

                Button(
                    modifier = Modifier
                        .padding(top = Dimen.dimen50)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    shape = RoundedCornerShape(Dimen.cornerShape),
                    onClick = { onClick(isPermissionDeclined, permission) }
                ) {
                    Text(
                        text = stringResource(R.string.grant_permission),
                        style = MaterialTheme.typography.titleLarge
                            .copy(color = MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}