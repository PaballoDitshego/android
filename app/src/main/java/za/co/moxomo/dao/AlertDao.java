package za.co.moxomo.dao;


import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import za.co.moxomo.model.Alert;

@Dao
public interface AlertDao {

    @Insert
    void insertAlert(Alert... alerts);

    @Query("SELECT * FROM Alert")
    DataSource.Factory<Integer, Alert> getAllAlerts();

    @Delete
    void delete(Alert alert);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Alert alert);

    @Query("Delete from Alert")
    void deleteAll();




}
