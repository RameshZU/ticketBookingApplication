import Route from '@ember/routing/route';

export default class SignupPageRoute extends Route {
    model(){
        let user=this.store.createRecord('user-detail');
        return user;
    }
}
