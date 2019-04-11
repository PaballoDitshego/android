package za.co.moxomo.gcm;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import za.co.moxomo.activities.MainActivity;
import za.co.moxomo.contentproviders.NotificationsContentProvider;
import za.co.moxomo.helpers.ApplicationConstants;

;

/**
 * Created by Paballo Ditshego on 7/28/15.
 */
public class FCMListenerService extends FirebaseMessagingService {

    private static final String TAG = "FCMListenerService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

       // remoteMessage.


     //   sendNotification(remoteMessage);

    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    private void sendNotification(Map<String,String> msg) {

       /* Long row_id = saveToDatabase(msg);

        //store user.id locally
        if (msg.getString("id").equals(null)) {
            SharedPreferences prefs = getSharedPreferences("UserDetails",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("id", msg.getString("id"));
            editor.commit();

        }


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

*/

    }
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }



    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

      /*  String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notif)
                      //  .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());*/
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