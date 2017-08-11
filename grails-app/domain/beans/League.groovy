package beans

import security.User

class League {

    String name
    String token

    static hasMany = [users: User, days: Day]

    static belongsTo = User

    static constraints = {
        name blank: false, unique: true
        token blank: false, unique: true
    }

    static mapping = {
        users lazy: false
        days lazy: false
    }

    String toString() {
        "${this.name}"
    }
}
