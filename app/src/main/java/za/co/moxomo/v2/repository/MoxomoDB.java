package za.co.moxomo.v2.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import za.co.moxomo.v2.dao.AlertDao;
import za.co.moxomo.v2.dao.NotificationDao;
import za.co.moxomo.v2.model.Alert;
import za.co.moxomo.v2.model.Notification;

@Database(entities = {Notification.class, Alert.class}, version =1)
public abstract class MoxomoDB extends RoomDatabase {
    public abstract NotificationDao notificationDao();
    public abstract AlertDao alertDao();

}
