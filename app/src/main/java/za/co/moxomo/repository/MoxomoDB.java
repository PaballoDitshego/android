package za.co.moxomo.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import za.co.moxomo.dao.AlertDao;
import za.co.moxomo.dao.NotificationDao;
import za.co.moxomo.model.Notification;
import za.co.moxomo.model.Vacancy;

@Database(entities = {Notification.class}, version =1)
public abstract class MoxomoDB extends RoomDatabase {
   public abstract NotificationDao notificationDao();
  //  public abstract AlertDao alertDao();

}
