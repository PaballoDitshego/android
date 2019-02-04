package za.co.moxomo.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.database.Cursor;



@Dao
public interface DBObject {

  //  String DB_NAME = "search_suggest.db";

    //@Query("SELECT * FROM kk WHERE kk LIKE :searchString")
    public Cursor getDealsCursor(String searchString);
}
