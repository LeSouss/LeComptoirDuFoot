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
}
