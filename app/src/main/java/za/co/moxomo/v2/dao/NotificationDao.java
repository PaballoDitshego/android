package za.co.moxomo.v2.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import za.co.moxomo.v2.model.Notification;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM Notification order by timestamp desc")
    DataSource.Factory<Integer, Notification> getAllNotifications();

    @Insert
    void insertNotification(Notification notification);

    @Delete
    void delete(Notification notification);

    @Query("Delete from Notification")
    void deleteAll();


}
