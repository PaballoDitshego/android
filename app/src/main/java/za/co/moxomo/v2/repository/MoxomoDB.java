package za.co.moxomo.v2.repository;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import za.co.moxomo.v2.dao.AlertDao;
import za.co.moxomo.v2.dao.NotificationDao;
import za.co.moxomo.v2.dao.SavedVacancyDao;
import za.co.moxomo.v2.dao.VacancyDao;
import za.co.moxomo.v2.model.Alert;
import za.co.moxomo.v2.model.Notification;
import za.co.moxomo.v2.model.SavedVacancy;
import za.co.moxomo.v2.model.Vacancy;

@Database(entities = {Notification.class, Alert.class, Vacancy.class, SavedVacancy.class}, version =10)
public abstract class MoxomoDB extends RoomDatabase {
    public abstract NotificationDao notificationDao();
    public abstract AlertDao alertDao();
    public abstract SavedVacancyDao savedVacancyDao();
    public abstract VacancyDao vacancyDao();

}
