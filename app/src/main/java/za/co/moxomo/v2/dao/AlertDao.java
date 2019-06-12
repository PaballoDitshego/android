package za.co.moxomo.v2.dao;


import java.util.List;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import za.co.moxomo.v2.model.Alert;

@Dao
public interface AlertDao {

    @Insert
    void insertAlert(Alert... alerts);

    @Query("SELECT * FROM Alert")
    DataSource.Factory<Integer, Alert> getAllAlerts();

    @Query("SELECT alertId FROM Alert")
    List<String> getAlertIds();

    @Delete
    void delete(Alert alert);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Alert alert);

    @Query("Delete from Alert")
    void deleteAll();




}
