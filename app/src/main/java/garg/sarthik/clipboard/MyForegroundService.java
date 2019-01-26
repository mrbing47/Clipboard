package garg.sarthik.clipboard;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
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
    List<Clip> clipList;

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

        clipList = ClipApplication.getClipDb().getClipDao().getAll();
        Intent stopForeground = new Intent(this, MyForegroundService.class);
        stopForeground.putExtra("KEY", true);
        PendingIntent pi = PendingIntent.getService(this,
                333,
                stopForeground,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (intent.hasExtra("KEY")) {
            Log.e(TAG, "onStartCommand: Removing Intent");
            stopForeground(true);
            stopSelf();
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
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

        String text;
        if (!clipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_INTENT) && clipboardManager.getPrimaryClip().getItemAt(0).getText() != null) {
            text = clipboardManager.getPrimaryClip().getItemAt(0).getText().toString().trim();
            addClip(text);
        } else
            Toast.makeText(this, "Clipboard can only handle plain text", Toast.LENGTH_SHORT).show();

        Log.e(TAG, "onPrimaryClipChanged: MIME TYPE " + clipboardManager.getPrimaryClipDescription().getMimeType(0));
    }

    public void addClip(String text) {
        Clip clip = new Clip(text, DateFormat.getDateTimeInstance().format(new Date()));
        if (!contains(clipList, text)) {
            clipList.add(clip);
            ClipApplication.getClipDb().getClipDao().insertClip(clip);
            Log.e(TAG, "onPrimaryClipChanged: " + text);
        } else {
            ClipApplication.getClipDb().getClipDao().updateClip(clip);
        }

    }

    public boolean contains(List<Clip> clipList, String text) {

        for (Clip clip : clipList)
            if (clip.getContent().equals(text))
                return true;

        return false;
    }

    @Override
    public void onDestroy() {
        clipboardManager.removePrimaryClipChangedListener(this);
        isListening = false;
        super.onDestroy();
    }
}
