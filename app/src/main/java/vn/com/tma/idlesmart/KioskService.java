package vn.com.tma.idlesmart;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static android.app.PendingIntent.FLAG_ONE_SHOT;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.Window.FEATURE_NO_TITLE;

public class KioskService extends Service {
    private static final String AndroidPackageInstaller = "com.android.packageinstaller";
    private static final long CHECKINTERVAL;
    private static final long DELAYINTERVAL;
    private static long FOREGROUND_RATE = 0;
    private static final int MAXRESTARTS = 10;
    private static final long NOTKIOSKINTERVAL;
    private static final long RESTARTINTERVAL;
    private static final String TAG = "IdleSmart.KioskService";
    private static int foreground_logmsg_count;
    private static boolean foreground_logmsg_issued;
    private static long interval;
    public static int restartcount;
    private Context ctx;
    private boolean packageInstallerRunning;
    private Dialog restoreDialog;
    private boolean running;
    private Thread f0t;

    /* renamed from: com.idlesmarter.aoa.KioskService.1 */
    class C00001 implements Runnable {
        C00001() {
        }

        public void run() {
            do {
                KioskService.this.handleKioskMode();
                try {
                    Thread.sleep(KioskService.interval);
                } catch (InterruptedException e) {
                    Log.i(KioskService.TAG, "Thread interrupted: 'KioskService'");
                }
            } while (KioskService.this.running);
            KioskService.this.stopSelf();
        }
    }

    public KioskService() {
        this.f0t = null;
        this.ctx = null;
        this.running = false;
        this.packageInstallerRunning = false;
        this.restoreDialog = null;
    }

    static {
        CHECKINTERVAL = TimeUnit.MILLISECONDS.toMillis(250);
        NOTKIOSKINTERVAL = TimeUnit.SECONDS.toMillis(10);
        DELAYINTERVAL = TimeUnit.SECONDS.toMillis(6);
        RESTARTINTERVAL = TimeUnit.SECONDS.toMillis(12);
        interval = CHECKINTERVAL;
        FOREGROUND_RATE = TimeUnit.SECONDS.toMillis(60) / CHECKINTERVAL;
        restartcount = 0;
        foreground_logmsg_issued = false;
        foreground_logmsg_count = 0;
    }

    public void onDestroy() {
        Log.i(TAG, "Stopping service 'KioskService'");
        this.running = false;
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service 'KioskService'");
        this.running = true;
        this.ctx = this;
        this.f0t = new Thread(new C00001());
        this.f0t.start();
        return START_NOT_STICKY;
    }

    /**
     * Check if app is not in foreground and there is no any updating,
     * restore the app by bringing main activity to front
     */
    private void handleKioskMode() {
        boolean isKiosModeActive = PrefUtils.isKioskModeActive(this.ctx);
        if (isKiosModeActive) {
            if (PrefUtils.getApkUpdateState(this.ctx) == PrefUtils.PREF_APK_UPDATE_RUNNING) {
                if (MainActivity.DebugLog) {
                    Log.i("IdleSmart.KioskService", "APK Update is running");
                }
                interval = CHECKINTERVAL;
                return;
            }

            if (MainActivity.HasFocus) {
                interval = CHECKINTERVAL;
            }
            if (this.isInForeground()) {
                restartcount = 0;
                if (MainActivity.DebugLog) {
                    if (foreground_logmsg_issued) {
                        foreground_logmsg_count++;
                    }

                    if (foreground_logmsg_count > FOREGROUND_RATE) {
                        Log.i("IdleSmart.KioskService", "We are in Foreround");
                        foreground_logmsg_issued = true;
                        foreground_logmsg_count = 0;
                    }
                }
                interval = CHECKINTERVAL;
                return;
            } else {
                if (MainActivity.PackageUpdatePending) {
                    if (this.packageInstallerRunning) {
                        if (MainActivity.DebugLog) {
                            Log.i("IdleSmart.KioskService", "   Package installer is running [for us]");
                            foreground_logmsg_issued = false;
                        }
                        interval = CHECKINTERVAL;
                        return;
                    }
                }
                if (MainActivity.DebugLog) {
                    Log.i("IdleSmart.KioskService", "   We are in the Background. Restart our main activity..");
                    foreground_logmsg_issued = false;
                }

                if (this.restoreApp()) {
                    interval = RESTARTINTERVAL;
                } else {
                    interval = DELAYINTERVAL;
                }
                return;
            }
        } else {
            if (MainActivity.DebugLog) {
                Log.i("IdleSmart.KioskService", "We are NOT in Kiosk Mode");
                foreground_logmsg_issued = false;
                this.isInForeground();
            }
            interval = NOTKIOSKINTERVAL;
            return;
        }
    }

    private boolean isInForeground() {
        RunningAppProcessInfo currentInfo = null;
        Field field = null;
        try {
            field = RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception e) {
        }
        for (RunningAppProcessInfo app : ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses()) {
            if (app.importance == 100 && app.importanceReasonCode == 0) {
                Integer state = null;
                try {
                    state = Integer.valueOf(field.getInt(app));
                } catch (Exception e2) {
                }
                if (state != null && state.intValue() == 2) {
                    currentInfo = app;
                    break;
                }
            }
        }
        if (currentInfo == null) {
            return false;
        }
        this.packageInstallerRunning = currentInfo.processName.equals(AndroidPackageInstaller);
        return this.ctx.getApplicationContext().getPackageName().equals(currentInfo.processName);
    }

    /**
     * Bring the main activity to front
     * @return true if success
     *         false if fail
     */
    private boolean restoreApp() {
        Log.i(TAG, "restoreApp()..");
        if (restartcount >= MAXRESTARTS) {
            if (MainActivity.DebugLog) {
                Log.i(TAG, "we are at maxrestarts");
            }
            return false;
        }
        restartcount++;
        if (MainActivity.DebugLog) {
            Log.i(TAG, "   restartcount=" + restartcount);
        }
        Log.i(TAG, "   we will restart the App::Activity()..");
        bringActivityToFront();
        return true;
    }

    public void mystartActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long now = Calendar.getInstance().getTimeInMillis();
        if (VERSION.SDK_INT >= 19) {
            alarmManager.setExact(0, now, pendingIntent);
        } else {
            alarmManager.set(0, now, pendingIntent);
        }
    }

    public void bringActivityToFront() {
        Log.i(TAG, "bringActivityToFront");
        Intent intent = new Intent(this, BringToFront.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void openRestoreDialog() {
        if (this.restoreDialog != null && this.restoreDialog.isShowing()) {
            this.restoreDialog.dismiss();
        }
        this.restoreDialog = new Dialog(this);
        this.restoreDialog.requestWindowFeature(FEATURE_NO_TITLE);
        this.restoreDialog.setContentView(R.layout.restore_dialog);
        this.restoreDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.restoreDialog.show();
    }
}
