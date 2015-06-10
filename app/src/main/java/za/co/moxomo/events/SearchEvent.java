package za.co.moxomo.events;

/**
 * Created by Paballo Ditshego on 6/5/15.
 */
public class SearchEvent {
     String query;

    public SearchEvent(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
