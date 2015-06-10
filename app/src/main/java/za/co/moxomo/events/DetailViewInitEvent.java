package za.co.moxomo.events;

/**
 * Created by Paballo Ditshego on 6/6/15.
 */
public class DetailViewInitEvent {

    private Long id;

    public DetailViewInitEvent(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
