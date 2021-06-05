import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, SQLException, Statement}
import scala.collection.mutable._;
import java.lang.Long;

class DBHandler {
  
  val url = "jdbc:mysql://localhost:3306/ticketBooking";
  val user = "root";
  val password = "";

  private var dbConnection:Connection=null;
  private var stmt: PreparedStatement=null;
  private var result:ResultSet = null;
  
  //db connection method
  def dbConnectionMethod(){
   try{
     Class.forName("com.mysql.cj.jdbc.Driver")
     dbConnection= DriverManager.getConnection(url,user, password)
   }
   catch{
    case e:SQLException =>{
      e.printStackTrace();
    }
   }
  }

 //close the connection
 def closeConnection(){
   try{
    if(stmt!=null){
      stmt.close();
    }
   }
   catch {
    case e:SQLException =>{
      e.printStackTrace();
    }
   }
   try{
     if(result!=null){
      result.close();
     }
   }
   catch {
     case e:SQLException =>{
      e.printStackTrace();
     }
   }
   try{
     if(dbConnection!=null){
      dbConnection.close();
     }
   }
   catch {
     case e:SQLException =>{
      e.printStackTrace();
     }
   }
 }

  /*after signup  add the user details in DB */
  def addUserData(name:String,phoneNum:Long,pwd:String,state:String):Boolean = {
     var addstate=false;
     dbConnectionMethod();
      try {
        stmt = dbConnection.prepareStatement("insert into user_data(user_name,phone_number,password,state) VALUES(?,?,?,?)");
        stmt.setString(1, name);
        stmt.setLong(2, phoneNum);
        stmt.setString(3, pwd);
        stmt.setString(4,state);
        stmt.execute();
        addstate=true;
      }
      catch {
        case e: SQLException => {
          e.printStackTrace()
        };
      }
      finally{
      closeConnection();
    }
      return addstate;
  }

  //after logout change the state false in DB
  def logoutUpdate(state:String):String={
   var logout="";
   dbConnectionMethod();
   try{
    stmt=dbConnection.prepareStatement("UPDATE user_data SET state=? WHERE state='true'");
    stmt.setString(1,state);
    stmt.execute();
    logout="success";
   }
   catch{
    case e:SQLException =>{
      e.printStackTrace();
    }
   }
   finally{
      closeConnection();
    }
   return logout;
  }

  //after login change the state true in DB
  def loginUpdate(state:String){
   dbConnectionMethod(); 
   try{
    stmt=dbConnection.prepareStatement("UPDATE user_data SET state=? WHERE state='false'");
    stmt.setString(1,state);
    stmt.execute();
   }
   catch{
    case e:SQLException =>{
      e.printStackTrace();
    }
   }
   finally{
      closeConnection();
    }
  }
  

  //add passenger details and book the ticket in the particular date
  def addPassengerDetails(userId:Int,passengerName:String,age:Int,flightId:Int,journeyDate:String,gender:String,ticketType:String,ticketPrice:Double,offerPrice:Double,paymentPrice:Double):ListBuffer[Map[String,String]]={
    dbConnectionMethod();
    try{
      stmt=dbConnection.prepareStatement("insert into passenger_details(user_id,passenger_name,age,flight_id,journey_date,gender,ticket_type,ticket_price,offer_price,payment_amount) VALUES (?,?,?,?,?,?,?,?,?,?)");
      stmt.setInt(1,userId);
      stmt.setString(2,passengerName);
      stmt.setInt(3,age);
      stmt.setInt(4,flightId);
      stmt.setString(5,journeyDate);
      stmt.setString(6,gender);
      stmt.setString(7,ticketType);
      stmt.setDouble(8,ticketPrice);
      stmt.setDouble(9,offerPrice);
      stmt.setDouble(10,paymentPrice);
      stmt.execute();
      if(!(isTicketIsAvailabe(flightId,journeyDate))){
        addFlightDateAndSeatCount(flightId,journeyDate,getSeatCountFromFlightDetails(flightId));
      }
      else{
        updateFlightSeatCount(flightId,journeyDate);
      }
    }
    catch{
      case e:SQLException => {
        e.printStackTrace();
      }
    }
    finally{
      closeConnection();
    }
    return getPassengerDetails(userId);
  }

    //afet book the ticket add the date and seat capacity on the flight
    def addFlightDateAndSeatCount(flightId:Int,journeyDate:String,seatCount:Int){
     dbConnectionMethod();
     try{
      stmt=dbConnection.prepareStatement("INSERT INTO booking_flight_details VALUES(?,?,?)");
      stmt.setString(1,journeyDate);
      stmt.setInt(2,seatCount);
      stmt.setInt(3,flightId);
      stmt.execute();
     }
     catch{
      case e: SQLException => {
        e.printStackTrace();
      }
     }
     finally{
      closeConnection();
     }
    }

   //decrease the seat count in the particular date
   def updateFlightSeatCount(flightId:Int,journeyDate:String){
    dbConnectionMethod();
    var seatCount=getSeatCountFromBooking(flightId,journeyDate);
    println(seatCount);
    seatCount=seatCount-1;
    println(seatCount);
    try{
      stmt=dbConnection.prepareStatement("UPDATE booking_flight_details SET seat_count=? WHERE flight_id=? AND booking_date=?");
      stmt.setInt(1,seatCount);
      stmt.setInt(2,flightId);
      stmt.setString(3,journeyDate);
      stmt.execute();
    }
    catch {
      case e:SQLException =>{
        e.printStackTrace();
      }
    }
    finally{
      closeConnection();
    }
   }

   def getSeatCountFromBooking(flightId:Int,journeyDate:String):Int={
     dbConnectionMethod();
     var seat=0;
     try{
      stmt=dbConnection.prepareStatement("select * from booking_flight_details WHERE flight_id=? AND booking_date=?");
      stmt.setInt(1,flightId);
      stmt.setString(2,journeyDate);
      stmt.execute();
      result=stmt.getResultSet();
      while(result.next()){
        seat=result.getInt(2);
      }
    }
    catch {
      case e:SQLException =>{
        e.printStackTrace();
      }
    }
    return seat;
   }
  
  //retrieve all the passenger details in the particular user
  def getPassengerDetails(userId:Int):ListBuffer[Map[String,String]]={
    var passengerList=ListBuffer[Map[String,String]]();
    dbConnectionMethod();
    try{
      stmt=dbConnection.prepareStatement("select * from passenger_details WHERE user_id=?");
      stmt.setInt(1,userId);
      stmt.execute();
      result=stmt.getResultSet();
      while(result.next()){
        passengerList+=Map("id"->result.getInt(2).toString,"user_id"->result.getInt(1).toString,"passenger_name"->result.getString(3),"age"->result.getInt(4).toString,"flight_id"->result.getInt(5).toString,"journey_date"->result.getString(6),"gender"->result.getString(7),"ticket_type"->result.getString(8),"ticket_price"->result.getDouble(9).toString,"offer_price"->result.getDouble(10).toString,"payment_amount"->result.getDouble(11).toString;
      }
    }
      catch {
        case e:SQLException => {
          e.printStackTrace();
        }
      }
      finally{
      closeConnection();
    }
      return passengerList;
  }

  //retrieve the login user details
  def getAlreadyLoginUser(phoneNumber:Long,state:String):Map[String,String]={
     var userList=Map[String,String]()
    dbConnectionMethod();
    try{
      stmt=dbConnection.prepareStatement("select * from user_data WHERE state=? AND phone_number=?");
      stmt.setString(1,state)
      stmt.setLong(2,phoneNumber);
      stmt.execute();
      result=stmt.getResultSet();
      while(result.next()){
        userList+=("id"->result.getInt(1).toString(),"user_name"->result.getString(2),"phone_number"->result.getLong(3).toString(),"password"->result.getString(4),"state"->result.getString(5));
      }
    }
    catch {
      case e:SQLException => {
        e.printStackTrace()
      };
    }
    finally{
      closeConnection();
    }
    return userList;
  }

 //verify the user is already signup
  def verifyUser(phoneNumber:Long):String = { 
    var state=false;
    dbConnectionMethod();
    try {
      stmt=dbConnection.prepareStatement("select * from user_data where phone_number=?");
      stmt.setLong(1,phoneNumber);
      stmt.execute();
      result=stmt.getResultSet();
      while(result.next()) {
          state=true;
       }
     }
      catch{
        case e: SQLException =>{
          e.printStackTrace()
        }
      }
      finally{
      closeConnection();
    }
      return state.toString();
  }
  
  //login the user 
  def loginUser(phoneNumber:Long,password:String,state:String):Boolean={
   dbConnectionMethod();
   try{
    stmt=dbConnection.prepareStatement("select * from user_data WHERE phone_number=? AND password=?");
    stmt.setLong(1,phoneNumber);
    stmt.setString(2,password);
    stmt.execute();
    result=stmt.getResultSet();
    while(result.next()){
       loginUpdate(state); 
    }
   }
   catch{
    case e:SQLException => {
       e.printStackTrace();
    }
   }
   finally{
      closeConnection();
   }
   return true;
  } 

  //get the last userid in user table
  def getLastUserId():String = {
    var id=0;
    dbConnectionMethod();
    try{
      stmt=dbConnection.prepareStatement("select user_id from user_data ORDER BY user_id desc limit 1");
      stmt.execute()
      result=stmt.getResultSet();
      while(result.next()){
        id=result.getInt(1);
      }
    }
    catch{
      case e:SQLException => {
       e.printStackTrace();
      } 
    }
    finally{
      closeConnection();
    }
    id.toString();
  }
 

   //check the seats or availble in the date 
   def checkSeatAvailable(flight_id:Int,date:String):Map[String,String]={
    dbConnectionMethod();
    var seatCount=0;
    var flight_available="no";
    try{
      stmt=dbConnection.prepareStatement("select * from booking_flight_details WHERE flight_id=? AND booking_date=?")
      stmt.setInt(1,flight_id);
      stmt.setString(2,date);
      stmt.execute();
      result=stmt.getResultSet();
      while(result.next()){
       seatCount=result.getInt(3);
       flight_available="yes";
      }
    }
    catch {
      case e:SQLException => {
        e.printStackTrace();
      }
    }
    finally{
      closeConnection();
    }
    return Map("seatCount"->seatCount.toString(),"flight_available"->flight_available);
   }

   
   //isTicketIsAvailabe method used for book the tickets and update or add the seats count
   def isTicketIsAvailabe(flight_id:Int,date:String):Boolean={
    var state=false;
     try{
      stmt=dbConnection.prepareStatement("select * from booking_flight_details WHERE flight_id=? AND booking_date=?")
      stmt.setInt(1,flight_id);
      stmt.setString(2,date);
      stmt.execute();
      result=stmt.getResultSet();
      while(result.next()){
       state=true;
      }
    }
    catch {
      case e:SQLException => {
        e.printStackTrace();
      }
    }
    return state;
   }
   
   //get the seat count from the flight details table
   def getSeatCountFromFlightDetails(flight_id:Int):Int ={
    dbConnectionMethod();
    var seatCount=0;
    try{
      stmt=dbConnection.prepareStatement("select seat_availability from flight_details WHERE flight_id=?");
      stmt.setInt(1,flight_id);
      stmt.execute();
      result=stmt.getResultSet();
      while(result.next()){
       seatCount=result.getInt(1);
      }
    }
    catch {
      case e:SQLException => {
        e.printStackTrace();
      }
    }
    finally{
      closeConnection();
    }
    return seatCount;
   }

   
   //get the all flight details
   def getAllFlightDetails():ListBuffer[Map[String,String]]={
    dbConnectionMethod();
    var flight_list=ListBuffer[Map[String,String]]()
    try {
      stmt=dbConnection.prepareStatement("select * from flight_details")
      stmt.execute();
      result=stmt.getResultSet
      while (result.next()){
        flight_list+=Map("id"->result.getInt(1).toString(),"flight_name"->result.getString(2),"from_city"->result.getString(3),"to_city"->result.getString(4),"day_schedule"->result.getString(5),"time"->result.getString(6),"seat_availability"->result.getInt(7).toString(),"image"->result.getString(8));
      }
    }
    catch {
      case e:SQLException =>{
        e.printStackTrace()
      }
    }
    finally{
      closeConnection();
    }
    return flight_list;
   }

   
   //get the particular passenger data
   def getSinglePassengerData(passengerId:Int):Map[String,String]={
    var passengerList=Map[String,String]();
    dbConnectionMethod();
    try {
      stmt=dbConnection.prepareStatement("select * from passenger_details WHERE passenger_id=?");
      stmt.setInt(1,passengerId);
      stmt.execute();
      result=stmt.getResultSet();
      while(result.next()){
        passengerList+=("id"->result.getInt(2).toString,"user_id"->result.getInt(1).toString,"passenger_name"->result.getString(3),"age"->result.getInt(4).toString,"flight_id"->result.getInt(5).toString,"journey_date"->result.getString(6),"gender"->result.getString(7),"ticket_type"->result.getString(8),"ticket_price"->result.getDouble(9).toString,"offer_price"->result.getDouble(10).toString,"payment_amount"->result.getDouble(11).toString);
      }
    } catch {
      case e:SQLException => e.printStackTrace();
    }
    finally{
      closeConnection();
    }
    return passengerList;
   }
   
   //delet the passennger record and cancel the ticket
   def deletePassengerRecord(passengerId:Int):ListBuffer[Map[String,String]]={
    var neededData=getFlightId(passengerId);
    dbConnectionMethod();
    try{
      stmt=dbConnection.prepareStatement("DELETE FROM passenger_details WHERE passenger_id=?");
      stmt.setInt(1,passengerId);
      stmt.execute();
      increaseSeatCount((neededData("flightId").toInt),neededData("journeyDate"));
    }
    catch{
      case e:SQLException => {
        e.printStackTrace();
      }
    }
    finally{
      closeConnection();
    }
    return getPassengerDetails(neededData("user_id").toInt);
   }

   //after cancel the ticket increase the seat count in the  journeyDate
   def increaseSeatCount(flightId:Int,journeyDate:String){
    dbConnectionMethod();
    var seatCount=getSeatCountFromBooking(flightId,journeyDate);
    println(seatCount);
    seatCount=seatCount+1;
    println(seatCount);
    try{
      stmt=dbConnection.prepareStatement("UPDATE booking_flight_details SET seat_count=? WHERE flight_id=? AND booking_date=?");
      stmt.setInt(1,seatCount);
      stmt.setInt(2,flightId);
      stmt.setString(3,journeyDate);
      stmt.execute();
    }
    catch {
      case e:SQLException =>{
        e.printStackTrace();
      }
    }
    finally{
      closeConnection();
    } 
    
   }

   //get the flight id in passenger table
   def getFlightId(passengerId:Int):Map[String,String]={
    var queryDetails=Map[String,String]();
    dbConnectionMethod();
    try {
      stmt=dbConnection.prepareStatement("select flight_id,journey_date,user_id from passenger_details WHERE passenger_id=?");
      stmt.setInt(1,passengerId);
      stmt.execute();
      result=stmt.getResultSet();
      while(result.next()){
       queryDetails+=("flightId"->result.getInt(1).toString,"journeyDate"->result.getString(2),"user_id"->result.getInt(3).toString);
      }
    } catch {
      case e:SQLException => e.printStackTrace;
    }
    finally{
      closeConnection();
    }
    return queryDetails;
   }


}
