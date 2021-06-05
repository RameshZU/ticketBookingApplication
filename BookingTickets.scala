import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json._;
import java.io.{BufferedReader}

class BookingTickets extends HttpServlet{

   def processRequest(req:HttpServletRequest,res:HttpServletResponse){
   	  res.setContentType("application/json")
      var reader:BufferedReader =req.getReader();
      var out=res.getWriter();

      var line = reader.readLine();
      var dbHandler = new DBHandler();

      var user_id=req.getParameter("id");
      var passengerId=req.getParameter("passengerId");
      var requestUrl=req.getRequestURI();
      var deletePassengerId=requestUrl.substring(requestUrl.lastIndexOf("/")+1);

     //add the passenger details in the DB
     if(line!=null){
     	var passengerObject=Json.parse(line)("passengerDetail");
     	var userId=passengerObject("user_id").toString.toInt;
     	var passengerName=passengerObject("passenger_name").toString;
     	var age=passengerObject("age").toString.toInt;
     	var flightId=passengerObject("flight_id").toString.toInt;
     	var gender=passengerObject("gender").toString;
     	var journeyDate=passengerObject("journey_date").toString;
     	var ticketType=passengerObject("ticket_type").toString;
     	var ticketPrice=passengerObject("ticket_price").toString.toDouble;
      var offerPrice=passengerObject("offer_price").toString.toInt;
     	var paymentPrice=passengerObject("payment_amount").toString.toInt;
        
        out.println(Json.toJson(Map("passenger-detail"->dbHandler.addPassengerDetails(userId,passengerName,age,flightId,journeyDate,gender,ticketType,ticketPrice,offerPrice,paymentPrice))));

     }
     
     //get all the passenger details in the particular user
     else if(user_id!=null){
      var passengerList=dbHandler.getPassengerDetails(user_id).toInt;
         out.println(Json.toJson(Map("passenger-detail"->passengerList)));
     }

     //get the single passenger-details
     else if(passengerId!=null){
      var id=passengerId.toInt;
      out.println(Json.toJson(Map("passenger-detail"->dbHandler.getSinglePassengerData(id))));
     }
     
     //deletePassenger record in the passenger-details table and cancel the ticket in booking_flight_details
     else if(deletePassengerId!=null){
      var deletePassenger=deletePassengerId.toInt;
      out.println(Json.toJson(Map("passenger-detail"->dbHandler.deletePassengerRecord(deletePassenger))));
     }

   }

	override def doPost(req:HttpServletRequest,res:HttpServletResponse){
		processRequest(req,res)
	}


	override def doGet(req:HttpServletRequest,res:HttpServletResponse){
		processRequest(req,res)
	}

  override def doDelete(req:HttpServletRequest,res:HttpServletResponse){
    processRequest(req,res)
  }

}