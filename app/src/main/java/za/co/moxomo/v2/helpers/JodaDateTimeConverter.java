package za.co.moxomo.v2.helpers;

import org.joda.time.DateTime;
import org.parceler.ParcelConverter;

import androidx.room.TypeConverter;

/**
 * Created by Paballo Ditshego on 5/22/15.
 */
public class JodaDateTimeConverter implements ParcelConverter<DateTime> {

    @Override
    public void toParcel(DateTime time, android.os.Parcel parcel) {
        if (time == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(1);
            parcel.writeLong(time.getMillis());
        }
    }

    @Override
    public DateTime fromParcel(android.os.Parcel parcel) {
        int nullCheck = parcel .readInt();
        DateTime result;
        if (nullCheck < 0) {
            result = null;
        } else {
            result = new DateTime(parcel.readLong());
        }
        return result;
    }

    @TypeConverter
    public static DateTime toDate(Long timestamp) {
        return timestamp == null ? null : new DateTime(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(DateTime date) {
        return date == null ? null : date.getMillis();
    }
}
