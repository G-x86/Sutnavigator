package Data;

public class Room extends DataAll {  
    
     
    private String floor;
    private String roomNumber;
    private String parentBuildingName;
    private int bId;
    @Override
    public String getDisplayText() {
        return name + " (" + parentBuildingName + ", Fl." + floor + ")";
    }

    public int getBId() {
        return bId;
    }

    public void setBId(int bId) {
        this.bId = bId;
    }
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getParentBuildingName() { return parentBuildingName; }
    public void setParentBuildingName(String parentBuildingName) { this.parentBuildingName = parentBuildingName; }
}