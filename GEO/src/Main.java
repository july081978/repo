import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderAddressComponent;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.code.geocoder.model.LatLng;

import dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

/**
 * 學校位置資訊
 * @author shawn
 *
 */
public class Main {

	public static void main(String[] args) throws SQLException, InterruptedException {
		
		
		
		Dao dao=new Dao();
		
		//dao.executeUpdate("DELETE FROM Savedtime WHERE school_term='"+term+"' AND school_year='"+year+"'");
		
		//List<Map>list=dao.sqlGet("SELECT * FROM SchoolCode WHERE lat IS NULL");		
		List<Map>list=dao.sqlGet("SELECT * FROM Recruit_school WHERE lat IS NULL");
		JSONObject geoinfo;
		String addr;
		Float[] coords;		
		int cnt=0;
		for(int i=0; i<list.size(); i++){
			Thread.sleep(1000);
			if(list.get(i).get("address")!=null){
				addr=list.get(i).get("address").toString();
			}else{
				addr=list.get(i).get("name").toString();
			}
			
			geoinfo=getArea(addr);
			
			if(geoinfo==null){
				System.out.println(list.get(i).get("name"));
				addr=list.get(i).get("name").toString().substring(0, list.get(i).get("name").toString().indexOf("附"));
				//addr=list.get(i).get("name").toString();
				geoinfo=getArea(addr);
			}
			
			
			System.out.println(geoinfo);
			try{
				if(geoinfo.get("administrative_area_level_1")==null){
					addr=list.get(i).get("name").toString();
					geoinfo=getArea(addr);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			//addr.replaceAll("國立", "");
			//addr.replaceAll("市立", "");
			//addr.replaceAll("私立", "");
			//coords=performGeoCoding(addr);
			
			try{
				System.out.println(geoinfo);
				dao.exSql("UPDATE Recruit_school SET lat='"+geoinfo.get("lat")+"', lng='"+geoinfo.get("lng")+"', "
				+ "geocode='"+geoinfo+"'WHERE Oid="+list.get(i).get("Oid"));
			}catch(Exception e){
				cnt++;
				e.printStackTrace();
			}
			
		}
		
		System.out.println("失敗"+cnt);
	}

	/*public static Float[] performGeoCoding(String location) {
		if(location == null)return null;
		
		Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
		.setAddress(location) // location
		//.setLanguage("zh-tw") // language
		.getGeocoderRequest();
		GeocodeResponse geocoderResponse;
		try{
			geocoderResponse = geocoder.geocode(geocoderRequest);
			if (geocoderResponse.getStatus() == GeocoderStatus.OK & !geocoderResponse.getResults().isEmpty()) {
				
				GeocoderResult geocoderResult = geocoderResponse.getResults().iterator().next();
				LatLng latitudeLongitude = geocoderResult.getGeometry().getLocation();
				Float[] coords = new Float[2];
				coords[0] = latitudeLongitude.getLat().floatValue();
				coords[1] = latitudeLongitude.getLng().floatValue();
				return coords;
			}
		}catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}*/
	
	
	public static JSONObject getArea(String location) {
		if(location == null || location.trim().equals(""))return null;
		
		Geocoder geocoder = new Geocoder();
		GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(location) // location
		.setLanguage("zh-tw") // language
		.getGeocoderRequest();
		GeocodeResponse geocoderResponse;
		try{
			geocoderResponse = geocoder.geocode(geocoderRequest);
			if (geocoderResponse.getStatus() == GeocoderStatus.OK & !geocoderResponse.getResults().isEmpty()) {
				
				GeocoderResult geocoderResult = geocoderResponse.getResults().iterator().next();
				JSONObject obj = new JSONObject();				
				//座標
				try{
					LatLng latitudeLongitude = geocoderResult.getGeometry().getLocation();				
					obj.put("lat", latitudeLongitude.getLat());
					obj.put("lng", latitudeLongitude.getLng());
				}catch(Exception e){
					
				}
				
				//地址
				try{
					List<GeocoderAddressComponent>list=geocoderResult.getAddressComponents();				
					if(list.size()<1)return null;
					List types;				
					for(int i=0; i<list.size(); i++){					
						types=list.get(i).getTypes();					
						obj.put(types.get(0).toString(), list.get(i).getLongName());
					}
				}catch(Exception e){
					
				}				
				return obj;
			}
		}catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
