package beans

import security.User

class Forecast {

    Boolean awayBet = false
    Boolean drawBet = false
    Boolean homeBet = false
    Boolean isUpdated = false

    static hasOne = [match: Match, user: User]

    static constraints = {

    }

    static mapping = {
        table "forecast_domain"
        id column: "forecast_id"
        match lazy: false
        user lazy: false
    }

    String toString () {
        "${this.match.home} - ${this.match.away} (${this.user})"
    }

    String toStringMultiple () {

        Bonus b = Bonus.findByUserAndDayAndForecast(this.user, this.match.day, this)
        b?.type
    }

}
