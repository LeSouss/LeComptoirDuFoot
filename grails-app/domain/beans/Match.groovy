package beans

class Match {

    Team away
    Team home
    Date date
    String status = "NOT STARTED"
    Boolean awayResult = false
    Boolean drawResult = false
    Boolean homeResult = false
    Float awayQuote = 1
    Float drawQuote = 1
    Float homeQuote = 1

    static hasMany = [forecasts: Forecast]

    static hasOne = [day: Day]

    static constraints = {
        status inList: ["NOT STARTED", "STARTED", "FINISHED", "CANCELLED"]
    }

    static mapping = {
        table "match_domain"
        id column: "match_id"
        home lazy: false
        away lazy: false
        forecasts lazy: false
        day lazy: false
    }

    String toString () {
        "${this.home} - ${this.away}"
    }
}
