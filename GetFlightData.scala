import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._;

class GetFlightData extends HttpServlet{
 override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
 	response.setContentType("application/json")
 	val out=response.getWriter();
 	var dbHandler=new DBHandler();
 	var map=dbHandler.getAllFlightDetails();
 	out.println(Json.toJson(Map("flight-detail"->map)))
 }
}