import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import dao.Dao;


public class SaveBatchDilgClass {

	public static void main(String[] args) throws SQLException, ParseException {
		
		Calendar stb=Calendar.getInstance();
		Calendar stb1=Calendar.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		
		Dao df=new Dao();
		df.exSql("UPDATE Class SET stds=(SELECT COUNT(*)FROM stmd WHERE depart_class=Class.ClassNo)");
		List<Map>cls=df.sqlGet("SELECT ClassNo FROM Class WHERE stds>0");
		
		for(int i=0; i<cls.size(); i++){
			stb.setTime(sdf.parse("2015-02-25"));
			stb1.setTime(sdf.parse("2015-02-25"));
			stb1.add(Calendar.DAY_OF_YEAR, 7);
			for(int j=1; j<=18; j++){
				
				stb.add(Calendar.DAY_OF_YEAR, 7);
				stb1.add(Calendar.DAY_OF_YEAR, 7);
				System.out.println("SELECT COUNT(*)FROM Dilg WHERE date>'"+sdf.format(stb.getTime())+"'AND date<'"+
				sdf.format(stb1.getTime())+"'AND student_no IN(SELECT student_no FROM stmd WHERE depart_class='"+
						cls.get(i).get("ClassNo")+"')");
				
				
				
				
				
				
			}
			
		}
		
		

	}

}
