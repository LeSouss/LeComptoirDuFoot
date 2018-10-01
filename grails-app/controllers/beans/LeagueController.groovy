package beans

import security.Role
import security.User
import security.UserRole

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
@Transactional(readOnly = true)
class LeagueController {

    def springSecurityService
    def leagueService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", UpdateScore:"PUT"]

    @Secured('ROLE_USER')
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        User user = springSecurityService?.currentUser

        List<League> leagueList = user?.leagues?.sort { it.name }

        respond leagueList, model:[leagueCount: leagueList?.size() ?: 0]
    }

    @Secured('ROLE_USER')
    def show(League league) {

        List<Score> scores = Score.findAllByLeague(league)?.sort { a, b ->

            //Tri par points décroissant puis par nom d'utilisateur croissant
            if (a.points < b.points) {
                1
            } else if (a.points > b.points) {
                -1
            } else {
                if (a.user.username < b.user.username) {
                    -1
                } else if (a.user.username > b.user.username) {
                    1
                } else {
                    0
                }
            }

        }

        List<Day> days = league?.days?.sort { a, b ->
            if (a.dayNumber < b.dayNumber){
                1
            } else if (a.dayNumber > b.dayNumber) {
                -1
            } else {
                0
            }
        }

        User user = springSecurityService?.currentUser
        Map<Integer, DayScore> dayScoresMap = new HashMap<Integer, DayScore>()
        days?.each { it ->
            dayScoresMap.put(it.dayNumber, DayScore.findByUserAndDay(user, it))
        }

        respond league, model:[scoresList: scores, daysList: days, dayScoresMap: dayScoresMap]
    }

    @Secured('ROLE_USER')
    def create() {
        respond new League(params)
    }

    @Secured('ROLE_USER')
    def join() {

    }

    @Secured('ROLE_USER')
    @Transactional
    def save(League league) {
        if (league == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (league.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond league.errors, view:'create'
            return
        }

        User admin = springSecurityService?.currentUser

        admin.addToLeagues(league).save(flush: true)
        league.addToUsers(admin).save(flush: true)

        Score score = Score.findByUserAndLeague(admin, league) ?: Score.create(admin, league)
        score.save(flush: true)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'league.label', default: 'League'), league.id])
                redirect league
            }
            '*' { respond league, [status: CREATED] }
        }
    }

    def edit(League league) {
        respond league
    }

    @Transactional
    def update(League league) {
        if (league == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (league.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond league.errors, view:'edit'
            return
        }

        league.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'league.label', default: 'League'), league.id])
                redirect league
            }
            '*'{ respond league, [status: OK] }
        }
    }

    @Transactional
    def delete(League league) {

        if (league == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        league.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'league.label', default: 'League'), league.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'league.label', default: 'League'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    @Secured('ROLE_USER')
    def joinLeague () {

        League league = League.findByToken(params.tokenToJoin)

        User user = springSecurityService?.currentUser

        if (league && user) {

            if (!user.leagues.contains(league)) {
                user.addToLeagues(league).save(flush: true)
            }

            if (!league.users.contains(user)) {
                league.addToUsers(user).save(flush: true)
            }

            Score score = Score.findByUserAndLeague(user, league) ?: Score.create(user, league)
            score.save(flush: true)

            List<Score> scoresList = Score.findAllByLeague(league)?.sort { a, b -> a.points <=> b.points ?: a.user.username <=> b.user.username }
            List<Day> daysList = league?.days?.sort { a, b ->
                if (a.dayNumber < b.dayNumber){
                    1
                } else if (a.dayNumber > b.dayNumber) {
                    -1
                } else {
                    0
                }
            }
            Map<Integer, DayScore> dayScoresMap = new HashMap<Integer, DayScore>()
            daysList?.each { it ->
                dayScoresMap.put(it.dayNumber, DayScore.findByUserAndDay(user, it))
            }

            render(view: "show", model: [league: league, scoresList: scoresList, daysList: daysList, dayScoresMap: dayScoresMap])

        } else {

            //redirect action: "join", method: "GET"

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'On t\'a filé un token en bois')
                    redirect action: "join", method: "GET"
                }
                //'*'{ respond league, [status: OK] }
            }

        }

    }

    def UpdateScore(League league) {

        leagueService.updateScores(league)

        redirect league

    }

}
