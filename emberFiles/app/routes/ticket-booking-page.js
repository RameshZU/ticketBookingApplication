import Route from '@ember/routing/route';

export default class TicketBookingPageRoute extends Route {
    model(params){
        const{flight_id}=params;
        let flight_details=this.store.peekRecord('flight-detail',Number(flight_id));
        return flight_details;
    }
}
