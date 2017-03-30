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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleKioskMode() {
        /*
        r6 = this;
        r5 = 1;
        r4 = 0;
        r0 = r6.ctx;
        r0 = com.idlesmarter.aoa.PrefUtils.isKioskModeActive(r0);
        if (r0 == 0) goto L_0x0090;
    L_0x000a:
        r0 = r6.getApplicationContext();
        r0 = com.idlesmarter.aoa.PrefUtils.getApkUpdateState(r0);
        if (r0 != r5) goto L_0x0024;
    L_0x0014:
        r0 = com.idlesmarter.aoa.MainActivity.DebugLog;
        if (r0 == 0) goto L_0x001f;
    L_0x0018:
        r0 = "IdleSmart.KioskService";
        r1 = "APK Update is running.";
        android.util.Log.i(r0, r1);
    L_0x001f:
        r0 = CHECKINTERVAL;
        interval = r0;
    L_0x0023:
        return;
    L_0x0024:
        r0 = com.idlesmarter.aoa.MainActivity.HasFocus;
        if (r0 != 0) goto L_0x002c;
    L_0x0028:
        r0 = CHECKINTERVAL;
        interval = r0;
    L_0x002c:
        r0 = r6.isInForeground();
        if (r0 == 0) goto L_0x0059;
    L_0x0032:
        restartcount = r4;
        r0 = com.idlesmarter.aoa.MainActivity.DebugLog;
        if (r0 == 0) goto L_0x0054;
    L_0x0038:
        r0 = foreground_logmsg_issued;
        if (r0 == 0) goto L_0x0049;
    L_0x003c:
        r0 = foreground_logmsg_count;
        r1 = r0 + 1;
        foreground_logmsg_count = r1;
        r0 = (long) r0;
        r2 = FOREGROUND_RATE;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x0054;
    L_0x0049:
        r0 = "IdleSmart.KioskService";
        r1 = "we are in Foreground.";
        android.util.Log.i(r0, r1);
        foreground_logmsg_issued = r5;
        foreground_logmsg_count = r4;
    L_0x0054:
        r0 = CHECKINTERVAL;
        interval = r0;
        goto L_0x0023;
    L_0x0059:
        r0 = com.idlesmarter.aoa.MainActivity.PackageUpdatePending;
        if (r0 == 0) goto L_0x0073;
    L_0x005d:
        r0 = r6.packageInstallerRunning;
        if (r0 == 0) goto L_0x0073;
    L_0x0061:
        r0 = com.idlesmarter.aoa.MainActivity.DebugLog;
        if (r0 == 0) goto L_0x006e;
    L_0x0065:
        r0 = "IdleSmart.KioskService";
        r1 = "   package installer is running [for us]";
        android.util.Log.i(r0, r1);
        foreground_logmsg_issued = r4;
    L_0x006e:
        r0 = CHECKINTERVAL;
        interval = r0;
        goto L_0x0023;
    L_0x0073:
        r0 = com.idlesmarter.aoa.MainActivity.DebugLog;
        if (r0 == 0) goto L_0x0080;
    L_0x0077:
        r0 = "IdleSmart.KioskService";
        r1 = "   We are in the Background.  Re-start our main activity..";
        android.util.Log.i(r0, r1);
        foreground_logmsg_issued = r4;
    L_0x0080:
        r0 = r6.restoreApp();
        if (r0 == 0) goto L_0x008b;
    L_0x0086:
        r0 = RESTARTINTERVAL;
        interval = r0;
        goto L_0x0023;
    L_0x008b:
        r0 = DELAYINTERVAL;
        interval = r0;
        goto L_0x0023;
    L_0x0090:
        r0 = com.idlesmarter.aoa.MainActivity.DebugLog;
        if (r0 == 0) goto L_0x00a0;
    L_0x0094:
        r0 = "IdleSmart.KioskService";
        r1 = "We are NOT in Kiosk Mode.";
        android.util.Log.i(r0, r1);
        foreground_logmsg_issued = r4;
        r6.isInForeground();
    L_0x00a0:
        r0 = NOTKIOSKINTERVAL;
        interval = r0;
        goto L_0x0023;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.idlesmarter.aoa.KioskService.handleKioskMode():void");
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
