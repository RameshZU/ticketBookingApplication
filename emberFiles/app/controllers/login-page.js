import Controller from '@ember/controller';
import { action } from '@ember/object';
import { tracked } from '@glimmer/tracking';
import { inject as service } from '@ember/service';
export default class LoginPageController extends Controller {
    @tracked numberCheck;
    @tracked verifyUserState;
    @service store;

    //show the password when user click the eye icon
    @action
    changeInputType(ele) {
        let passwordInput = ele.target.parentElement.previousElementSibling;
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);
        ele.target.classList.toggle('fa-eye-slash');
        ele.target.classList.toggle('fa-eye');
      }

    //if user input and verify user is validation is success add the cookie and retreive the data from the DB  
    @action
    verifyUserForLogin(){
        if(this.userPhoneNumber!=undefined && !(this.checkNumber()) && this.pwd!=undefined && !(this.verifyUserState) && this.userPhoneNumber.length>9){
            let data=this.store.queryRecord('user-detail',{  "phone_number":this.userPhoneNumber,"password":this.pwd}).then((result)=>{
                if(result.get('id')!=null){
                  document.cookie="id=1; path=/; max-age=" + 30*24*60*60;
                  document.cookie="cookiestate=true; path=/; max-age=" + 30*24*60*60;
                  document.cookie="phone_number="+this.userPhoneNumber+"; path=/; max-age=" + 30*24*60*60;
                  this.transitionToRoute('index');
                  this.userPhoneNumber="";
                  this.pwd="";
                }
            })
            
        }
    }

    //verify the user in the DB
    @action
    async  verifyUser(){
        if(this.userPhoneNumber != undefined  && !(this.checkNumber()) && this.userPhoneNumber.length>9){
          this.numberCheck=false;
          const data=await fetch("http://localhost:8080/ticketApp/checkUser?phone_number="+this.userPhoneNumber).then(response => response.json())
          .then(result => { 
           if(result.checkuser.state==="true"){
           this.verifyUserState=false;
           }
           else{
            this.verifyUserState=true;
          }
         })
        }
       }

    //validate user login input
    @action
    validateInput(){
      if(this.userPhoneNumber==undefined || this.checkNumber()  || this.userPhoneNumber.length<9){
         this.numberCheck=true;
      }
      else{
          this.numberCheck=false;
      }
      this.verifyUserForLogin();
    }
    
    //check the input is number
    checkNumber(){
        return isNaN(this.userPhoneNumber)
    }
}
