package beans

import grails.transaction.Transactional

@Transactional(readOnly = false)
class LeagueService {

    def updateScores(League league) {

        league?.users?.each { it ->

            Float points = 0

            DayScore.findAllByUser(it)?.each {dayScore ->
                //points = points + dayScore.points + dayScore.bonusPoints
                points = points + dayScore.points
            }

            Score score = Score.findByUserAndLeague(it, league)

            score?.points = points

            score?.save(flush: true)
        }

    }

}
