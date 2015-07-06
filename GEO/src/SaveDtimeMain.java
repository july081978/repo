import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.Dao;

/**
 * 課程轉歷年
 * 包含教學評量
 * @author john
 */
public class SaveDtimeMain {

	private static String year="100";
	private static String term="1";
	private static Map map;	
	
	public static Map getMap() {
		return map;
	}

	public static void setMap(Map map) {
		SaveDtimeMain.map = map;
	}

	public SaveDtimeMain() {}

	/**
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Dao dao=new Dao();
		
		dao.exSql("DELETE FROM Savedtime WHERE school_term='"+term+"' AND school_year='"+year+"'");
		List list=dao.sqlGet(
		"SELECT Oid, depart_class, cscode, techid, opt, elearning, " +
		"credit, thour, Introduction, Syllabi, Syllabi_sub " +
		"FROM Dtime WHERE Sterm='"+term+"'");
		
		//計算教學評量
		for(int i=0; i<list.size(); i++){
			countAvg((Map)list.get(i));			
			//更新本學期
			dao.exSql("UPDATE Dtime SET coansw="+map.get("total")+", " +
			"simples="+map.get("sumAns")+" WHERE Oid='"+((Map)list.get(i)).get("Oid")+"'");			
		}
		//寫入歷史
		dao.exSql("INSERT INTO Savedtime(depart_class, cscode, techid, " +
		"opt, credit, thour, Introduction, Syllabi, Syllabi_sub, avg, samples)" +
		"SELECT depart_class, cscode, techid, opt, credit, thour, Introduction, Syllabi, " +
		"Syllabi_sub, coansw, simples FROM Dtime WHERE Sterm='1'");
		dao.exSql("UPDATE Savedtime SET school_year="+year+", school_term="+term+" WHERE school_year=''");
	}
	
	/**
	 * 計算教學評量
	 * 重用CIS專案元件
	 * @see tw.edu.chit.service.impl.CourseManagerImpl
	 * @param d
	 * @throws SQLException
	 */
	public static void countAvg(Map d) throws SQLException{		
		SaveDtimeMain.setMap(new HashMap());		
		NumberFormat nf = NumberFormat.getInstance();
	    nf.setMaximumFractionDigits(1);
	    List coquests;
	    Dao dao=new Dao();
	    if(!d.get("elearning").equals("0")){//非一般課程
	    	coquests=dao.sqlGet("SELECT * FROM CoQuestion WHERE type='M' AND (textValue='1'||textValue='2') ORDER BY sequence");
	    }else{	    	
	    	coquests=dao.sqlGet("SELECT * FROM CoQuestion WHERE type='M' AND textValue='0' ORDER BY sequence");
	    }		
		
		float queSize[]=new float[coquests.size()];		
		List myCoansw=dao.sqlGet(
		"SELECT * FROM Coansw WHERE (answer NOT LIKE '%111%') AND Dtime_oid="+d.get("Oid"));

		for(int j=0; j<myCoansw.size(); j++){
			String answer=((Map)myCoansw.get(j)).get("answer").toString();
			// 散裝並加總
			for(int k=0; k<queSize.length; k++){
				//五分制和百分制的關鍵點在此
				queSize[k]=queSize[k]+Integer.parseInt( answer.substring(k, k+1))*20;	
			}
		}
		
		Map map=new HashMap();
		map.put("Oid", d.get("Oid"));		
		Map dtime=dao.sqlGetMap("SELECT d.techid, d.opt, d.credit, d.elearning, d.depart_class, d.cscode, e.cname " +
		"FROM Dtime d LEFT OUTER JOIN empl e ON d.techid=e.idno " +
		"WHERE d.Oid="+d.get("Oid"));		
		map.put("sumAns", myCoansw.size());		
		float total=0f; // 某科總分
		List score=new ArrayList();// 某科的細節
		Map sMap;
		BigDecimal big;		
		for(int j=0; j<coquests.size(); j++){
			sMap=new HashMap();
			sMap.put("options", ((Map)coquests.get(j)).get("options"));
			total=total+getCoanswTotle(queSize[j], myCoansw.size());
			score.add(sMap);
		}
		total=total/coquests.size();
		map.put("total", roundOff(total, 1));		
		
		if(myCoansw.size()>0){
			SaveDtimeMain.setMap(map);
		}else{
			map.put("total", 0);
			map.put("sumAns", 0);			
		}		
	}
	
	/**
	 * 算分數
	 */
	private static Float getCoanswTotle(float queSize, int myCoansw){
		return queSize/myCoansw;
	}
	
	/**
	 * 將float四捨五入至小數第n位
	 */
	public static float roundOff(float f, int n){
		try{
			BigDecimal b=new BigDecimal(f);	
			return b.setScale(n,BigDecimal.ROUND_HALF_UP).floatValue();
		}catch(NumberFormatException e){
			return 0;
		}		
	}

}
