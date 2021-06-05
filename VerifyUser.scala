import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._;

class VerifyUser extends HttpServlet{
	override def doGet(request:HttpServletRequest,response:HttpServletResponse){
     response.setContentType("application/json")
     var out=response.getWriter();
     var dbHandler=new DBHandler();
     val phonenumber=request.getParameter("phone_number")
     var checkState=dbHandler.verifyUser(phonenumber.toString);
     var map=Json.toJson(Map("id"->"1","state"->checkState));
     out.println(Json.toJson(Map("checkuser"->map)));
	}
}