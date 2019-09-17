package za.co.moxomo.v2.dao;


import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import za.co.moxomo.v2.model.Vacancy;

@Dao
public interface VacancyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVacancy(Vacancy... vacancies);

    @Query("SELECT * FROM Vacancy")
    DataSource.Factory<Integer, Vacancy> getAllVacancies();

    @Query("SELECT * FROM Vacancy")
    List<Vacancy> getAllDBVacancies();

    @Query("SELECT id FROM Vacancy")
    List<String> getVacancyIds();



    @Delete
    void delete(Vacancy vacancy);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Vacancy vacancy);

    @Query("Delete from Vacancy")
    void deleteAll();


}
