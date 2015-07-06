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
 * 學生地理位置資訊
 * @author shawn
 *
 */
public class Main2 {

	public static void main(String[] args) throws SQLException, InterruptedException {
		
		Dao dao=new Dao();
		//里或全部？
		//List<Map>list=dao.sqlGet("SELECT student_no, perm_addr FROM stmd WHERE "
		//+ "(perm_addr IS NOT NULL && perm_addr!='') AND geocode IS NULL AND perm_addr NOT LIKE '%里%'");	
		
		//List<Map>list=dao.sqlGet("SELECT student_no, perm_addr FROM stmd WHERE "
		//+ "(perm_addr IS NOT NULL && perm_addr!='') AND geocode IS NULL");	
		
		List<Map>list=dao.sqlGet("SELECT * FROM stmd WHERE geocode NOT LIKE '%lat%'");
		
		String addr;
		JSONObject geoinfo;		
		String liner;		
		int success=0, fail=0;		
		
		for(int i=0; i<list.size(); i++){
			
			Thread.sleep(1000);			
			addr=list.get(i).get("perm_addr").toString();
			geoinfo=getArea(addr);
			System.out.print(list.get(i).get("perm_addr")+": ");
			System.out.println(geoinfo);
			if(geoinfo!=null){				
				try{
					liner=geoinfo.get("administrative_area_level_4").toString();
				}catch(Exception e){
					liner=null;
					continue;
				}
				
				try{				
					dao.exSql("UPDATE stmd SET liner='"+liner+"', geocode='"+geoinfo+"' WHERE student_no='"+list.get(i).get("student_no")+"'");
					success++;
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}else{
				System.out.println("無戶籍地址");
				fail++;
			}
		}
		
		System.out.println("成功: "+success+"筆, 失敗: "+fail+"筆");
		
	}
	
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
