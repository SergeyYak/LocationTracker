package com.sergei.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.sergei.tracker.R;

import org.jetbrains.annotations.NotNull;


public class StartupActivity extends AppCompatActivity {
    private TextView messageView;
    private AppConfig appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appConfig = new AppConfig(getApplicationContext());

        ClickListeners btnListeners = new ClickListeners(getApplicationContext(), this);

        messageView = findViewById(R.id.MessageView);

        EditText serverURLEditText = findViewById(R.id.EditServerURLText);
        serverURLEditText.setText(appConfig.new Server().serverHost);

        Button requestLocationPermissionBtn = findViewById(R.id.RequestPermissionBtn);
        Button disableBatteryEnergySavingBtn = findViewById(R.id.DisableBatteryEnergySaving);

        Button disableNotificationBtn = findViewById(R.id.DisableNotification);
        Button registerDeviceBtn = findViewById(R.id.RegisterDeviceBtn);
        Button startTrackBtn = findViewById(R.id.StartTrackBtn);
        Button exitBtn = findViewById(R.id.ExitBtn);

        requestLocationPermissionBtn.setOnClickListener(btnListeners.requestLocationPermissionBtnListener);
        disableBatteryEnergySavingBtn.setOnClickListener(btnListeners.disableBatteryEnergyBtnListener);

        disableNotificationBtn.setOnClickListener(btnListeners.disableNotificationBtnListener);
        registerDeviceBtn.setOnClickListener(btnListeners.registerDeviceBtnListener);
        startTrackBtn.setOnClickListener(btnListeners.startTrackBtnListener);
        exitBtn.setOnClickListener(btnListeners.exitBtnListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode can be different 1 or 2 or 3 and so far.
        if (requestCode == PermissionsClass.LocationClass.locationRequestCode) {
            if (grantResults.length > 0) {
                int allowStatus = PackageManager.PERMISSION_GRANTED;

                boolean allAllowed = true;
                for(int grantResult:grantResults){
                    allAllowed = grantResult == allowStatus;
                }
                if (allAllowed) {
                    messageView.setText(R.string.permissionGranted);
                } else {
                    messageView.setText(R.string.permissionDenied);
                }
            }
        }
    }

    private class ClickListeners {
        private final ButtonActions buttonActions;

        ClickListeners(Context context, Activity activity) {
            buttonActions = new ButtonActions(context, activity);
        }

        final View.OnClickListener requestLocationPermissionBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonActions.RequestLocationPermission();
            }
        };

        final View.OnClickListener disableBatteryEnergyBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonActions.DisableBatteryEnergySaving();
            }
        };

        final View.OnClickListener disableNotificationBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonActions.DisableNotification();
            }
        };

        final View.OnClickListener registerDeviceBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonActions.RegisterDevice();
            }
        };

        final View.OnClickListener startTrackBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonActions.StartTrackingService();
            }
        };

        final View.OnClickListener exitBtnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonActions.Exit();
            }
        };
    }

    private class ButtonActions {
        private final Context context;
        private final Activity activity;
        private final EditText serverURLTextView;
        private final DeviceRegistration deviceRegistration;

        ButtonActions(Context context, Activity activity) {
            this.context = context;
            this.activity = activity;
            serverURLTextView = activity.findViewById(R.id.EditServerURLText);
            this.deviceRegistration = new DeviceRegistration(context);
        }

        private void RequestLocationPermission() {
            boolean locationAllowed = PermissionsClass.LocationClass.checkLocationPermissions(context);
            if (!locationAllowed) {
                PermissionsClass.LocationClass.requestPermissions(activity);
            } else messageView.setText(R.string.permissionAlreadyGranted);
        }

        private void DisableBatteryEnergySaving() {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                activity.startActivity(intent);
            } else messageView.setText(R.string.batterySavingAlreadyDisabled);
        }

        private void DisableNotification() {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                Intent appDetailsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                appDetailsIntent.setData(Uri.parse("package:" + context.getPackageName()));
                activity.startActivity(appDetailsIntent);
            } else messageView.setText(R.string.notificationsAlreadyDisabled);
        }

        private void RegisterDevice() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();

                    final String url = serverURLTextView.getText().toString();
                    if (appConfig.ValidateURL(url)) {
                        appConfig.Write(AppConfig.Values.ServerHostOptionName, url);
                        messageView.post(new Runnable() {
                            @Override
                            public void run() {
                                messageView.setText(R.string.registeringStatus);
                            }
                        });
                        String currentDeviceId = deviceRegistration.getDeviceId();
                        String newId = deviceRegistration.RegisterDevice();
                        if (newId.equals(currentDeviceId)) {
                            messageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    messageView.setText(R.string.notRegisteredStatus);
                                }
                            });

                        }
                        else {
                            messageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    messageView.setText(R.string.registeredStatus);
                                }
                            });
                        }

                    }
                    else messageView.post(new Runnable() {
                        @Override
                        public void run() {
                            messageView.setText(R.string.incorrectURL);
                        }
                    });

                    Looper.loop();
                }
            }).start();
        }

        private void StartTrackingService() {
            boolean[] appReadyStatus = appConfig.CheckIfAppReadyForLaunch();
            if(!appReadyStatus[0]) {messageView.setText(R.string.notRegisteredStatus); return;}
            if(!appReadyStatus[1]) {messageView.setText(R.string.incorrectURL); return;}
            if(!appReadyStatus[2]) {messageView.setText(R.string.locationIsNotAllowed); return;}

            EditText serverURLEditText = findViewById(R.id.EditServerURLText);
            serverURLEditText.setText(appConfig.new Server().serverHost);

            messageView.setText(R.string.trackingStarted);
            Intent ServiceIntent = new Intent(context, AppService.class);
            context.stopService(ServiceIntent);
            context.startForegroundService(ServiceIntent);
        }

        private void Exit() {
            finish();
        }
    }
}