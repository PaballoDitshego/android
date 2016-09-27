package za.co.moxomo.helpers;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.parceler.ParcelConverter;

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
}
