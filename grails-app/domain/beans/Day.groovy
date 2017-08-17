package beans

class Day {

    String name

    static hasOne = [league: League]
    static hasMany = [matchs: Match]

    static constraints = {
        name blank: false, unique: true
    }

    static mapping = {
        table "day_domain"
        id column: "day_id"
        league lazy: false
        matchs lazy: false
    }

    String toString() {
        "${this.name}"
    }
}
