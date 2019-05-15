package za.co.moxomo.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import za.co.moxomo.dao.AlertDao;
import za.co.moxomo.dao.NotificationDao;
import za.co.moxomo.model.Alert;
import za.co.moxomo.model.Notification;

@Database(entities = {Notification.class, Alert.class}, version =9 )
public abstract class MoxomoDB extends RoomDatabase {
    public abstract NotificationDao notificationDao();
    public abstract AlertDao alertDao();

}
