package za.co.moxomo.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Maybe;
import za.co.moxomo.model.Notification;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM Notification  WHERE  _id >= :id  order by timestamp desc LIMIT :size")
    public Maybe<List<Notification>> getAllNotifications(int id, int size);

    @Insert
    public void insertNotification(Notification notification);
}
