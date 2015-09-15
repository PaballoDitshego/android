package za.co.moxomo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.Calendar;

/**
 * Created by Paballo Ditshego on 7/28/15.
 */
public class GCMListenerService extends GcmListenerService {


    @Override
    public void onMessageReceived(String from, Bundle data) {

        sendNotification(data);

    }

    private void sendNotification(Bundle msg) {

        Long row_id = saveToDatabase(msg);


        Intent resultIntent = new Intent(this, NotificationActivity.class);
        resultIntent.putExtra("row_id", row_id);
        resultIntent.putExtra("notification", "");

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(msg.getString("title"))
                .setContentText(msg.getString("body"))
                .setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(resultPendingIntent);


        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        mNotifyBuilder.setAutoCancel(true);
        mNotificationManager.notify((int) Calendar.getInstance().getTimeInMillis(), mNotifyBuilder.build());


    }


    /*
      Save notification data

      returns Long row id/_id
     */
    private Long saveToDatabase(Bundle msg) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", msg.getString(ApplicationConstants.TITLE_KEY));
        contentValues.put("body", msg.getString(ApplicationConstants.BODY));
        contentValues.put("action_string", msg.getString(ApplicationConstants.ACTION_STRING));
        contentValues.put("type", msg.getString(ApplicationConstants.TYPE));
        contentValues.put("status", "unread");
        contentValues.put("image_url", msg.getString(ApplicationConstants.IMAGE_URL));
        Uri uri = getContentResolver().insert(
                NotificationsContentProvider.CONTENT_URI,   // the user dictionary content URI
                contentValues);


        return Long.parseLong(uri.getPathSegments().get(1));


    }
}