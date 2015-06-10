package za.co.moxomo.events;

/**
 * Created by Paballo Ditshego on 6/6/15.
 */
public class BrowserViewInitEvent {
    public String getUrl() {
        return url;
    }

    private String url;

    public BrowserViewInitEvent(String url) {
        this.url = url;
    }
}
