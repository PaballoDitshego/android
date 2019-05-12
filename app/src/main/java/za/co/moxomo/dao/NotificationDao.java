package za.co.moxomo.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import za.co.moxomo.model.Alert;
import za.co.moxomo.model.Notification;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM Notification")
    DataSource.Factory<Integer, Notification> getAllNotifications();

    @Insert
    void insertNotification(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("Delete from Notification")
    void deleteAll();


}
