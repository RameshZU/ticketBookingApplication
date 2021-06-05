import Model,{ attr } from '@ember-data/model';

export default class UserDetailModel extends Model {
    @attr user_name;
    @attr phone_number;
    @attr password;
    @attr state; 
}
