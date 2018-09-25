package beans

class Day {

    String name = "Journée"
    Integer dayNumber

    static hasOne = [league: League]
    static hasMany = [matchs: Match]

    static constraints = {
        name blank: false
        dayNumber nullable: false
    }

    static mapping = {
        table "day_domain"
        id column: "day_id"
        league lazy: false
        matchs lazy: false
    }

    String toString() {
        "${this.name} ${this.dayNumber}"
    }
}
