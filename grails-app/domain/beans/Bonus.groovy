package beans

import security.User

class Bonus {

    User user
    Forecast forecast
    Day day
    String type

    static mapping = {
        table "bonus_domain"
        id column: "bonus_id"
        day lazy: false
        user lazy: false
        forecast lazy: false
    }

    static constraints = {
        type inList: ["Double", "Triple"]
        forecast nullable: true
    }
}
