package beans

import security.User

class Forecast {

    Boolean awayBet = false
    Boolean drawBet = false
    Boolean homeBet = false
    Boolean isDouble = false

    static hasOne = [match: Match, user: User]

    static constraints = {

    }

    static mapping = {
        table "forecast_domain"
        id column: "forecast_id"
        match lazy: false
        user lazy: false
    }
}
