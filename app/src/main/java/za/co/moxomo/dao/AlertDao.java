package za.co.moxomo.dao;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Maybe;
import za.co.moxomo.model.Alert;


@Dao
public interface AlertDao {

    @Query("SELECT * FROM Alert  WHERE  _id >= :id  order by _id desc LIMIT :size")
    Maybe<List<Alert>> getAllAlerts(int id, int size);

    @Insert
    void insertAlert(Alert Alert);



}
