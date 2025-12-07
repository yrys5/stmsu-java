package pl.pg.maps;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

@WebService(
        serviceName = "MapService",
        portName = "MapServicePort",
        targetNamespace = "http://maps.pg.pl/"
)
public class MapService {
    private static final double LAT_MIN = 54.35;   // dół mapy
    private static final double LAT_MAX = 54.45;   // góra mapy
    private static final double LON_MIN = 18.55;   // lewa strona
    private static final double LON_MAX = 18.70;   // prawa strona

    private static BufferedImage mapImage;
    private static int mapWidth;
    private static int mapHeight;

    static {
        try {
            InputStream is = MapService.class.getResourceAsStream("/mapa.png");
            if (is == null) {
                throw new RuntimeException("Nie znaleziono pliku mapa.png na classpath!");
            }
            mapImage = ImageIO.read(is);
            mapWidth = mapImage.getWidth();
            mapHeight = mapImage.getHeight();
            System.out.println("Mapa załadowana: " + mapWidth + "x" + mapHeight);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Błąd podczas wczytywania mapy: " + e.getMessage(), e);
        }
    }

    @WebMethod
    public String getMapFragmentByPixels(int x1, int y1, int x2, int y2) throws Exception {
        int xMin = Math.min(x1, x2);
        int yMin = Math.min(y1, y2);
        int xMax = Math.max(x1, x2);
        int yMax = Math.max(y1, y2);

        xMin = clamp(xMin, 0, mapWidth - 1);
        xMax = clamp(xMax, 0, mapWidth - 1);
        yMin = clamp(yMin, 0, mapHeight - 1);
        yMax = clamp(yMax, 0, mapHeight - 1);

        int width = xMax - xMin + 1;
        int height = yMax - yMin + 1;

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Niepoprawny prostokąt: width=" + width + ", height=" + height);
        }

        BufferedImage cropped = mapImage.getSubimage(xMin, yMin, width, height);
        return encodeToBase64Png(cropped);
    }

    @WebMethod
    public String getMapFragmentByGeo(double lat1, double lon1, double lat2, double lon2) throws Exception {
        int x1 = lonToPixelX(lon1);
        int y1 = latToPixelY(lat1);
        int x2 = lonToPixelX(lon2);
        int y2 = latToPixelY(lat2);

        return getMapFragmentByPixels(x1, y1, x2, y2);
    }


    private int lonToPixelX(double lon) {
        double ratio = (lon - LON_MIN) / (LON_MAX - LON_MIN);
        int x = (int) Math.round(ratio * (mapWidth - 1));
        return clamp(x, 0, mapWidth - 1);
    }

    private int latToPixelY(double lat) {
        double ratio = (LAT_MAX - lat) / (LAT_MAX - LAT_MIN); // odwrócona oś Y
        int y = (int) Math.round(ratio * (mapHeight - 1));
        return clamp(y, 0, mapHeight - 1);
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    // ----- POMOCNICZE: obraz → Base64 PNG -----

    private String encodeToBase64Png(BufferedImage img) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        byte[] bytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
