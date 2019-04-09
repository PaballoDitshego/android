package za.co.moxomo.dao;


;
import android.database.Cursor;

import androidx.room.Dao;


@Dao
public interface DBObject {

  //  String DB_NAME = "search_suggest.db";

    //@Query("SELECT * FROM kk WHERE kk LIKE :searchString")
    public Cursor getDealsCursor(String searchString);
}
