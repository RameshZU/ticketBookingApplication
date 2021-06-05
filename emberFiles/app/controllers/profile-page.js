import Controller from '@ember/controller';
import { action } from '@ember/object';
import { tracked } from '@glimmer/tracking';
export default class ProfilePageController extends Controller {
    @tracked passengerEmpty;
    get getUserData(){
        let userData=this.store.peekAll('user-detail');
        console.log(userData);
        return userData;
    }

   get addFlightDataInPassengerModel(){
       let flightDetails;
       if(this.model.length!=0){
        this.model.forEach((object)=>{
            let flightData=this.store.peekRecord('flight-detail',Number(object.flight_id));
            flightDetails=flightData;
       })
    return flightDetails;
       }
    }

    get getTotalAmount(){
        let totalAmount=0;
        if(this.model.length!=0){
            this.model.forEach((passengerData)=>{
                totalAmount+=Number(passengerData.payment_amount);
               })
        }
       return totalAmount; 
    }

    get isPassengerAvailable(){
        console.log(this.model.length);
        if(this.model.length==0){
            return true;
        }
        else{
            return false;
        }
    }
}
