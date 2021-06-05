import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._;
import java.lang.{Double,Integer}

class checkSeatAvailability extends HttpServlet{
 override def doGet(req:HttpServletRequest,res:HttpServletResponse){
 	res.setContentType("application/json");
 	var out=res.getWriter();
    var dbHandler = new DBHandler();
    var flight_id=req.getParameter("flight_id").toString.toInt;
    var date=req.getParameter("date");
    out.println(Json.toJson(Map("checkSeatAvailability"->dbHandler.checkSeatAvailable(flight_id,date))));
 }
}