package za.co.moxomo.v2.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Map;

import javax.inject.Inject;

import androidx.core.app.NotificationCompat;
import za.co.moxomo.v2.MoxomoApplication;
import za.co.moxomo.v2.R;
import za.co.moxomo.v2.activities.MainActivity;
import za.co.moxomo.v2.helpers.Utility;
import za.co.moxomo.v2.repository.Repository;


/**
 * Created by Paballo Ditshego on 7/28/15.
 */
public class FCMListenerService extends FirebaseMessagingService {

    private static final String TAG = "FCMListenerService";

    @Inject
    Repository repository;

    @Override
    public void onCreate() {
        MoxomoApplication.moxomoApplication().injectionComponent().inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "remote messge " + remoteMessage.getData().toString());
        sendNotification(remoteMessage.getData());

    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        Utility.storeFcmTokenInSharedPref(getApplicationContext(), token);
    }

    private void sendNotification(Map<String, String> msg) {
        za.co.moxomo.v2.model.Notification notification = za.co.moxomo.v2.model.Notification.builder()
                .id(msg.get("id"))
                .description(Utility.trimDescription(msg.get("body")))
                .url(msg.get("url"))
                .title(msg.get("title"))
                .location(msg.get("location"))
                .imageUrl(msg.get("imageUrl"))
                .timestamp(DateTime.now())
                .type(msg.get("alert_type"))
                .build();
        repository.insertNotification(notification);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("notification", "");

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this, "ALERTS")
                .setContentTitle(msg.get("title"))
                .setContentText(msg.get("body"))
                .setChannelId("ALERTS")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(resultPendingIntent);
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mNotifyBuilder.setDefaults(defaults);
        mNotifyBuilder.setAutoCancel(true);
        Log.d(TAG, "notifying " + mNotifyBuilder.toString());
        mNotificationManager.notify((int) Calendar.getInstance().getTimeInMillis(), mNotifyBuilder.build());


    }


}