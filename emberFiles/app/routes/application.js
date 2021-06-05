import Route from '@ember/routing/route';

export default class ApplicationRoute extends Route {
  model(){
    let obj=this.store.findAll('flight-detail');
    console.log("saved");
    return obj;
  }
}
