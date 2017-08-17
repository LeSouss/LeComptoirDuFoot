package beans

import security.User

class Score {

    League league
    User user
    Float points = 0

    static Score create(User user, League league) {
        def instance = new Score(user: user, league: league, points: 0)
        instance.save()
        instance
    }

    static constraints = {
    }

    static mapping = {
        table "score_domain"
        id column: "score_id"
        league lazy: false
        user lazy: false
    }

    String toString() {
        "${this.user.username} : ${this.points}"
    }
}
