package lecomptoirdufoot

import beans.Day
import beans.League
import beans.Match
import beans.Team
import security.Role
import security.User
import security.UserRole

class BootStrap {

    def destroy = {
    }

    def init = { servletContext ->
        def adminRole = new Role(authority: 'ROLE_ADMIN').save()
        def testUser = new User(username: 'LeSouss', password: 'pwd').save()
        UserRole.create testUser, adminRole
        UserRole.withSession {
            it.flush()
            it.clear()
        }
        assert User.count() == 1
        assert Role.count() == 1
        assert UserRole.count() == 1

        //def league = new League(name: 'Ligue 1', token: 'pwd').save()
        //league.addToUsers(testUser).save()
        //testUser.addToLeagues(league).save()

        //def day1 = new Day(name: 'Journée 1', league: league).save()
        //def day2 = new Day(name: 'Journée 2', league: league).save()

        //league.addToDays(day1).save()
        //league.addToDays(day2).save()

        //def marseille = new Team(name: "Marseille").save()
        //def paris = new Team(name: "Paris").save()
        //def lyon = new Team(name: "Lyon").save()
        //def monaco = new Team(name: "Monaco").save()
        //def nice = new Team(name: "Nice").save()

        //def match1 = new Match(home: marseille, away: paris, date: new Date(), day: day1).save()
        //def match2 = new Match(home: monaco, away: lyon, lyon: new Date(), day: day1).save()
        //def match3 = new Match(home: nice, away: marseille, date: new Date(), day: day2).save()

        //day1.addToMatchs(match1).save()
        //day1.addToMatchs(match2).save()
        //day2.addToMatchs(match3).save()


    }
}
