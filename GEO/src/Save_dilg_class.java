import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import dao.Dao;


public class Save_dilg_class {

	public static void main(String[] args) throws SQLException, ParseException {
		
		Date d=new Date(), d1;
		Calendar stb=Calendar.getInstance(), stb1=Calendar.getInstance();
		Dao df=new Dao();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		
		
		df.exSql("UPDATE Class SET stds=(SELECT COUNT(*)FROM stmd WHERE depart_class=Class.ClassNo)");
		df.exSql("DELETE FROM BATCH_DILG_CLASS");
		
		List<Map>cls=df.sqlGet("SELECT ClassNo FROM Class WHERE stds>0");
		
		for(int i=0; i<cls.size(); i++){
			stb.setTime(sf.parse("2015-02-25"));		
			stb1.setTime(sf.parse("2015-02-25"));
			stb1.add(Calendar.DAY_OF_YEAR, 7);
			//System.out.println(cls.get(i).get("ClassNo"));
			for(int j=1; j<=18; j++){	
				//if(j<11){
				//if(d.getTime()<=stb1.getTimeInMillis() && d.getTime()>=stb.getTimeInMillis()){
					//System.out.println("第"+j+"週自"+sf.format(stb.getTime())+"~"+sf.format(stb1.getTime()));
					df.exSql("DELETE FROM BATCH_DILG_CLASS WHERE week="+j+" AND ClassNo='"+cls.get(i).get("ClassNo")+"'");
					df.exSql("INSERT INTO BATCH_DILG_CLASS(ClassNo,week,stds,dilgs)SELECT ClassNo, "+j+", stds,"
					+ "(SELECT COUNT(*)FROM Dilg WHERE abs<'5'AND date>='"+sf.format(stb.getTime())+"'AND date<='"+sf.format(stb1.getTime())+"'AND "
					+ "student_no IN(SELECT student_no FROM stmd WHERE depart_class=Class.ClassNo))"
					+ "FROM Class WHERE ClassNo='"+cls.get(i).get("ClassNo")+"'");
				//}				
				stb.add(Calendar.DAY_OF_YEAR, 7);
				stb1.add(Calendar.DAY_OF_YEAR, 7);
			}
			
		}
		
		d1=new Date();
		System.out.println((d1.getTime()-d.getTime())/1000);

	}

}
