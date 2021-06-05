import Controller from '@ember/controller';

export default class ApplicationController extends Controller {
    //get the cookie from the browser
    getCookie(cookieKeyName){
            var cookieName = cookieKeyName + "=";
            var decodedCookie = decodeURIComponent(document.cookie);
            var cookieArray = decodedCookie.split('; ');
            console.log(cookieArray);
            for(var i = 0; i < cookieArray.length; i++) {
              var singleCookie = cookieArray[i];
              while (singleCookie.charAt(0) == ' ') {
                singleCookie = singleCookie.substring(1);
              }
              if (singleCookie.indexOf(cookieName) == 0) {
                return singleCookie.substring(cookieName.length, singleCookie.length);
              }
            }
            return "";
          }
    //get the cookie and check it's stored or not
    get checkCookie() {
        var user=this.getCookie("phone_number");
        var state=this.getCookie("cookiestate");
        if (user != "" && state!="" ) {
         let data= this.store.queryRecord('user-detail',{ "phone_number":user,"cookiestate":state});
        } else {
          this.transitionToRoute('index');
        }
      }      
    
}
