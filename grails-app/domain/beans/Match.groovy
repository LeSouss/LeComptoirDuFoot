package beans

class Match {

    Team away
    Team home
    Date date
    String status = "NOT STARTED"
    Boolean awayResult = false
    Boolean drawResult = false
    Boolean homeResult = false
    Float awayQuote
    Float drawQuote
    Float homeQuote

    static hasMany = [forecasts: Forecast]

    static hasOne = [day: Day]

    static constraints = {
        status inList: ["NOT STARTED", "STARTED", "FINISHED", "CANCELLED"]
    }

    static mapping = {
        home lazy: false
        away lazy: false
        forecasts lazy: false
        day lazy: false
    }

    String toString () {
        "${this.home} - ${this.away}"
    }
}
