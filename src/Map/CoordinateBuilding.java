package Map;

import java.util.ArrayList;
import java.util.List;

import Data.Building;
import Data.DataAll;
import User.Datauser;

public class CoordinateBuilding {
	private List<DataAll> Data = new ArrayList<>();
	public double[][] DataCoordinateBuilding ;
	public CoordinateBuilding() {
		new Datauser().getAllData(Data);
		
	}
	public  double[][] get() {
		DataCoordinateBuilding = Data.stream()
	             
	            .filter(item -> item instanceof Building)
	            
	            
	            .map(item -> (Building) item)
	            
	            .map(b -> {
	                
	            	Double catId = (double) 0.0f;
	                try {
	                    catId = Double.parseDouble(b.getCategory());
	                } catch (NumberFormatException e) {
	                    catId = (double) 0.0f;  
	                }
	                return new double[] { 
	                    b.getId(),    
	                    b.getMapX(),   
	                    b.getMapY(),   
	                    catId 
	                    
	                };
	            })
	            .toArray(double[][]::new);

	        return DataCoordinateBuilding;
	}
}
