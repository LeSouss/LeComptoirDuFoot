package beans

class Team {

    String name

    static constraints = {
    }

    static mapping = {
        table "team_domain"
        id column: "team_id"
    }

    String toString () {
        "${this.name}"
    }
}
