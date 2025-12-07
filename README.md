# MapService – Usługa SOAP do wycinków mapy
Projekt zawiera prostą usługę sieciową SOAP (JAX-WS), która zwraca wycinek mapy miasta jako obraz PNG zakodowany w Base64.
## Struktura
- src/
  - mapa.png                → obraz mapy (max 1000×1000 px)
  - pl/pg/maps/
    - MapService.java       → implementacja usługi
    - MapServerMain.java    → uruchomienie serwera SOAP
## Uruchomienie
- Użyj JDK 8.
- W VS Code uruchom MapServerMain.
- Usługa będzie dostępna pod adresem:
  - http://localhost:8080/MapService
- WSDL dostępny pod:
  - http://localhost:8080/MapService?wsdl
## Dostępne metody
- getMapFragmentByPixels(x1, y1, x2, y2) – zwraca wycinek mapy na podstawie współrzędnych pikseli.
- getMapFragmentByGeo(lat1, lon1, lat2, lon2) – to samo, lecz z użyciem współrzędnych geograficznych.
Obie metody zwracają obraz jako Base64 PNG.