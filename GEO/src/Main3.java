import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dao.Dao;

/**
 * 戶籍地址加註里資訊
 * @author shawn
 *
 */
public class Main3 {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Dao d=new Dao();
		
		List<Map<String,String>>list=d.sqlGet("SELECT s.student_no, s.perm_addr, liner FROM stmd s WHERE s.perm_addr NOT LIKE '%里%' AND s.liner IS NOT NULL");
		int a=0, b=0;
		System.out.println("要做: "+list.size());
		String place;
		for(int i=0; i<list.size(); i++){
			if(list.get(i).get("perm_addr").indexOf("村")>0){
				a++;
				continue;
			}		
			
			place=replace(list.get(i).get("perm_addr"), list.get(i).get("liner"));
			System.out.println(i+". "+place);
			
			d.exSql("UPDATE stmd SET perm_addr='"+place+"' WHERE student_no='"+list.get(i).get("student_no")+"'");
			
			b++;
		}
		
		System.out.println("沒做:"+a+" 有做: "+b);
	}
	
	private static String replace(String addr, String liner){
		
		StringBuilder sb=new StringBuilder();
		int place;
		//有區搞定結束
		if(addr.indexOf("區")>0){
			place=addr.indexOf("區")+1;
			sb.append(addr.substring(0, place));
			sb.append(liner);
			sb.append(addr.substring(place, addr.length()));
			
			return sb.toString();
		}else{
			place=addr.length();
			if(addr.indexOf("鄉")>0)place=addr.indexOf("鄉")+1;
			if(addr.indexOf("鎮")>0)place=addr.indexOf("鎮")+1;
			//if(addr.indexOf("縣")>0)place=addr.indexOf("縣")+1;
			if(addr.indexOf("市")>0)place=addr.indexOf("市")+1;
			//if(addr.indexOf("村")>0)place=addr.indexOf("村")+1;
			
			sb.append(addr.substring(0, place));
			sb.append(liner);
			sb.append(addr.substring(place, addr.length()));
			
			return sb.toString();
		}
		
		
		
		
		
	}

}
