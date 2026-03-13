package Data;

import java.awt.Color;

 
public abstract class DataAll {
    
  
    protected int id;
    protected String name;
    protected String description;
    protected String category;
    protected String imagePath;
    protected Double mapX;
    protected Double mapY;

 
    public abstract String getDisplayText();

     
    public Color getCategoryColor() {
        if (category == null) return Color.GRAY;
        switch (category.toLowerCase()) {
            case "academic": return new Color(66, 133, 244);
            case "dining": return new Color(245, 124, 0);
            case "housing": return new Color(52, 168, 83);
            case "library": return new Color(156, 39, 176);
            case "recreation": return new Color(234, 67, 53);
            case "classroom": return new Color(0, 150, 136);
            default: return Color.GRAY;
        }
    }

    public Color getCategoryBgColor() {
        if (category == null) return new Color(240, 240, 240);
        switch (category.toLowerCase()) {
            case "academic": return new Color(219, 234, 254);
            case "dining": return new Color(254, 243, 199);
            case "housing": return new Color(220, 252, 231);
            case "library": return new Color(237, 233, 254);
            case "recreation": return new Color(254, 226, 226);
            case "classroom": return new Color(224, 242, 241);
            default: return new Color(243, 244, 246);
        }
    }

 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Double getMapX() { return mapX; }
    public void setMapX( Double mapX) { this.mapX = mapX; }

    public Double getMapY() { return mapY; }
    public void setMapY(Double mapY) { this.mapY = mapY; }
}