package beans

import security.User

class DayScore {

    Day day
    User user
    Float points = 0
    Integer nbMatchsOk = 0
    //Float bonusPoints = 0

    static constraints = {
    }

    static mapping = {
        table "day_score_domain"
        id column: "day_score_id"
        day lazy: false
        user lazy: false
    }

    /*
    String toString() {
        "${this.user.username} ${this.day} : ${this.points} + ${this.bonusPoints}"
    }
     */

    String toString() {
        "${this.user.username} ${this.day} : ${this.points}"
    }
}
