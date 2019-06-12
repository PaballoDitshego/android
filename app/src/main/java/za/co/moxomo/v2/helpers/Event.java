package za.co.moxomo.v2.helpers;

public class Event<T> {

    private T content;

    boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    public T getContentIfNotHandled(){
        if(hasBeenHandled){
            return null;
        }else{
            hasBeenHandled=true;
            return content;
        }
    }


    public boolean isHasBeenHandled() {
        return hasBeenHandled;
    }

    public void setContent(T content) {
        this.content = content;
    }

    private  T peekContent(){
        return content;
    }

    public void setHasBeenHandled(boolean hasBeenHandled) {
        this.hasBeenHandled = hasBeenHandled;
    }
}
