package pl.pg.maps;

import javax.xml.ws.Endpoint;

public class MapServerMain {

    public static void main(String[] args) {
        String url = "http://localhost:8080/MapService";
        System.out.println("Publikuję serwis SOAP pod adresem: " + url);

        MapService implementor = new MapService();
        Endpoint.publish(url, implementor);

        System.out.println("Serwis działa. WSDL: " + url + "?wsdl");
        System.out.println("Nie zamykaj tego procesu (Ctrl+C żeby zakończyć).");
    }
}
