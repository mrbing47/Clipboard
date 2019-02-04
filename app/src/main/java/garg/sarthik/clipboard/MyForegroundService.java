package garg.sarthik.clipboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class MyForegroundService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {

    public static final String CHANNEL_ID = "420YOLO";
    public static boolean isListening = false;
    public final String TAG = "Service";
    ClipboardManager clipboardManager;
    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: IN THE FOREGROUND SERVICE");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(this);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent stopForeground = new Intent(this, MyForegroundService.class);
        stopForeground.putExtra("KEY", true);
        PendingIntent pi = PendingIntent.getService(this,
                333,
                stopForeground,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (intent.hasExtra("KEY")) {
            stopService();
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_dashboard)
                .setContentTitle("Clipboard is Running")
                .setContentText("Clipboard is looking for all the copied data")
                .setAutoCancel(false)
                .addAction(R.mipmap.ic_launcher, "Stop", pi)
                .build();

        startForeground(420, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrimaryClipChanged() {

        Log.e(TAG, "onPrimaryClipChanged: CLIP LABEL = " + clipboardManager.getPrimaryClipDescription().getLabel());
        String text;
        if (!clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_INTENT) && clipboardManager.getPrimaryClip().getItemAt(0).getText() != null) {
            text = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString().trim();
            addClip(text);
        } else
            Toast.makeText(this, "Clipboard can only handle plain text", Toast.LENGTH_SHORT).show();

        Log.e(TAG, "onPrimaryClipChanged: MIME TYPE = " + clipboardManager.getPrimaryClipDescription().getMimeType(0));
    }

    public void addClip(String text) {
        Clip clip = new Clip(text, DateFormat.getDateTimeInstance().format(new Date()));
        try {
            ClipApplication.getClipDb().getClipDao().insertClip(clip);
            Log.e(TAG, "onPrimaryClipChanged: " + text);
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, "addClip: ", e);
            ClipApplication.getClipDb().getClipDao().updateClip(clip);
        }
    }

    public void stopService() {
        Log.e(TAG, "onStartCommand: Removing Intent");
        clipboardManager.removePrimaryClipChangedListener(this);
        isListening = false;
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        clipboardManager.removePrimaryClipChangedListener(this);
        isListening = false;
        super.onDestroy();
    }
}
