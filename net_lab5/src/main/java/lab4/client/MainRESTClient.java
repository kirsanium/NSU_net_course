package lab4.client;

public class MainRESTClient {
    public static void main(String[] args) {
        RESTClient restClient = new RESTClient(6789, true);
        restClient.start();
    }
}

