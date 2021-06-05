import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._;
import java.io.{BufferedReader, BufferedWriter, IOException, PrintWriter}

class UserDetailsQuery extends HttpServlet{

	def processRequest(req:HttpServletRequest,res:HttpServletResponse){

		res.setContentType("application/json")

		var out=res.getWriter();
		var dbHandler=new DBHandler()
		var reader:BufferedReader =req.getReader()
        var line = reader.readLine();
        var state=req.getParameter("state")
        var phoneNumber=req.getParameter("phone_number");
        var password=req.getParameter("password");
        var loginUserPhoneNumber=req.getParameter("phone_number");
        var loginState=req.getParameter("cookiestate");

       //add the new user
        if(line!=null){
          var userObject=Json.parse(line)("userDetail")
          var phoneNumber=userObject("phone_number").toString.toLong;
          var userName=userObject("user_name").toString;
          var password=userObject("password").toString;
          var state=userObject("state").toString;
          var success=dbHandler.addUserData(userName,phoneNumber,password,state)
		      out.println(Json.toJson(Map("sucess"->success)));
        }

        //if user login this method get the user detail from db 
        else if(phoneNumber!=null && password!=null){
          if(dbHandler.loginUser(phoneNumber.toLong,password,"true")){
            out.println(Json.toJson(Map("user-detail"->dbHandler.getAlreadyLoginUser(phoneNumber.toLong,"true"))));
          }
        }
        
        //get the user details until the user doesn't logout
        else if(loginUserPhoneNumber!=null && loginState!=null){
          out.println(Json.toJson(Map("user-detail"->dbHandler.getAlreadyLoginUser(loginUserPhoneNumber.toLong,loginState))));
        }
	}
   
    //override the dopost method
  	override def doPost(req:HttpServletRequest,res:HttpServletResponse){
		 processRequest(req,res)
	 }

   //override the doGet method
	 override def doGet(req:HttpServletRequest,res:HttpServletResponse){
		processRequest(req,res)
	 }
}