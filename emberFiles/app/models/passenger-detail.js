import Model,{attr,belongsTo,hasMany} from '@ember-data/model';

export default class PassengerDetailModel extends Model {
@attr user_id;
@belongsTo('flight-detail') flight_data;
@attr passenger_name;
@attr age;
@attr journey_date;
@attr gender;
@attr flight_id;
@attr ticket_type;
@attr offer_price;
@attr ticket_price;
@attr payment_amount;
}
