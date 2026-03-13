package User;

public class SearchResult {
    public String text;
    public String imagePath;
    public String category;
    public int categoryId;
    public double lat;
    public double lon;
    public int buildingId; 

    public SearchResult(String text, String imagePath, String category, int categoryId, double lat, double lon, int buildingId) {
        this.text = text;
        this.imagePath = imagePath;
        this.category = category;
        this.categoryId = categoryId;
        this.lat = lat;
        this.lon = lon;
        this.buildingId = buildingId;  
    }
}