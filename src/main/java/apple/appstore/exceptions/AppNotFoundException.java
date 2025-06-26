package apple.appstore.exceptions;


public class AppNotFoundException extends RuntimeException {
    public AppNotFoundException(int id) {
        super("App with ID " + id + " not found");
    }
}
