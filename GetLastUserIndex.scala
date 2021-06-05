import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._;

class GetLastUserIndex extends HttpServlet{
	override def doGet(req:HttpServletRequest,res:HttpServletResponse){
		res.setContentType("application/json")
		var out=res.getWriter()
		var dbHandler=new DBHandler()
		out.println(Json.toJson(Map("lastid"->dbHandler.getLastUserId())))
	}
}