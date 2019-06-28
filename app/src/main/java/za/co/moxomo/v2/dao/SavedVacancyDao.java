package za.co.moxomo.v2.dao;


import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import za.co.moxomo.v2.model.SavedVacancy;
import za.co.moxomo.v2.model.Vacancy;

@Dao
public interface SavedVacancyDao {

    @Insert
    void insertVacancy(SavedVacancy... vacancies);

    @Query("SELECT * FROM SavedVacancy")
    DataSource.Factory<Integer, Vacancy> getAllVacancies();

    @Query("SELECT id FROM SavedVacancy")
    List<String> getVacancyIds();

    @Delete
    void delete(SavedVacancy vacancy);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(SavedVacancy vacancy);

    @Query("Delete from SavedVacancy")
    void deleteAll();


}
