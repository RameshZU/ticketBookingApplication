import Controller from '@ember/controller';
import { tracked } from '@glimmer/tracking';
export default class AboutFlightController extends Controller {
    //only show the book now button if user is login
    get avaiableForBooking(){
        let data = this.store.peekAll('user-detail').filter((ele) => {
            return ele.state == "true";
          });
          console.log(data.length);
          if (data.length == 0) {
            return false;
          } else {
            return true;
          } 
    }
}
