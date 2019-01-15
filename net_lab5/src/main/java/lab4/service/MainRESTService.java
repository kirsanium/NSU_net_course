package lab4.service;

public class MainRESTService {
    public static void main(String[] args) {
        RESTService service = new RESTService(6789);
        service.start();
    }
}
