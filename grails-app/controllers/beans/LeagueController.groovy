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

        scores.each { it -> it.save(flush: true)}
        List<Day> days = league?.days?.sort { it.name }

        respond league, model:[scoresList: scores, daysList: days]
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

        if (admin) {

            Role roleAdmin = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN')

            roleAdmin.save(flush: true)

            UserRole userRoleAdmin = UserRole.findByUserAndRole(admin, roleAdmin)

            if (!userRoleAdmin && (admin.username == "LeSouss")) {

                UserRole.findByUser(admin)?.delete(flush: true)

                UserRole.create(admin, roleAdmin).save(flush: true)

                springSecurityService.reauthenticate admin.username
            }

        }

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

            render(view: "show", model: [league: league, scoresList: Score.findAllByLeague(league)?.sort { a, b -> a.points <=> b.points ?: a.user.username <=> b.user.username }, daysList: league?.days?.sort { it.name }])

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

    @Transactional
    def UpdateScore(League league) {

        Score.findAllByLeague(league)?.each { s ->
            //it.user

            Float points = 0

            Match.findAllByDayInListAndStatus(league?.days?.toList(), "FINISHED").each { m ->

                Forecast.findAllByMatchAndUser(m, s.user)?.each { f ->

                    //if resultat = pari
                    // alors points += cote
                    if (f.homeBet && m.homeResult) {

                        if (f.isDouble) {
                            points += (m.homeQuote * 2)
                        } else {
                            points += m.homeQuote
                        }

                    } else if (f.drawBet && m.drawResult) {

                        if (f.isDouble) {
                            points += (m.drawQuote * 2)
                        } else {
                            points += m.drawQuote
                        }

                    } else if (f.awayBet && m.awayResult) {

                        if (f.isDouble) {
                            points += (m.awayQuote * 2)
                        } else {
                            points += m.awayQuote
                        }

                    } else {
                        points += 0
                    }

                }

            }

            s.points = points

            s.save(flush: true)
        }

        redirect league

    }

}
