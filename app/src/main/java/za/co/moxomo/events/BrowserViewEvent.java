package za.co.moxomo.events;

/**
 * Created by Paballo Ditshego on 5/31/15.
 */
public class BrowserViewEvent {

    public String url;
    public BrowserViewEvent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
