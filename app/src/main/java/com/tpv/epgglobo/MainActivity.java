package com.tpv.epgglobo;

import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private boolean hasNotificationPermissionGranted = false;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                hasNotificationPermissionGranted = isGranted;
                if (!isGranted) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                            showNotificationPermissionRationale();
                        } else {
                            showSettingDialog();
                        }
                    }
                } else {
                    Log.i("Permissions", "Notification permission added.");
                }
            });

    private void showSettingDialog() {
        new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
                .setTitle("Notification Permission")
                .setMessage("Notification permission is required, Please allow notification permission from setting")
                .setPositiveButton("Ok", (dialog, which) -> {
                    Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showNotificationPermissionRationale() {
        new MaterialAlertDialogBuilder(this, com.google.android.material.R.style.MaterialAlertDialog_Material3)
                .setTitle("Alert")
                .setMessage("Notification permission is required, to show notification")
                .setPositiveButton("Ok", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= 33) {
                        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            MainFragment fragment = new MainFragment();
            transaction.replace(R.id.contentFragment, fragment);
            transaction.commit();
        }

        if (Build.VERSION.SDK_INT >= 33) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        } else {
            hasNotificationPermissionGranted = true;
        }

        if (hasNotificationPermissionGranted) {
            createNotificationChannel();
        }
    }

    public void createNotificationChannel() {
        String channelId = "EPGGlobo";
        String name = "Program Reminders";
        String description = "Reminds user when their selected tv programs starts.";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            manager.createNotificationChannel(channel);
        }

        Log.i("notifications", "Created notification channel");
    }
}