import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._;

class LogoutTheUser extends HttpServlet{
	override def doGet(req:HttpServletRequest,res:HttpServletResponse){
     res.setContentType("application/json")
     var out=res.getWriter();
     var dbHandler=new DBHandler();
     var state=req.getParameter("state");
     out.println(Json.toJson(Map("logout"->dbHandler.logoutUpdate(state))))
	}
}