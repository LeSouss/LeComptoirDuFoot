package beans

import security.User

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
@Transactional(readOnly = true)
class MatchController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def springSecurityService

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Match.list(params)?.sort { it.date }, model:[matchCount: Match.count()]
    }

    @Secured('ROLE_USER')
    def show(Match match) {

        User user = springSecurityService?.currentUser

        List<Forecast> forecasts = new ArrayList<Forecast>()

        if ((match.date <= new Date()) && ((match.status == "STARTED") || (match.status == "FINISHED"))) {
            forecasts = Forecast.findAllByMatch(match)?.sort { it -> it.user.username }
        } else {
            Forecast forecast = Forecast.findByUserAndMatch(user, match)
            if (forecast) [
                    forecasts.add(forecast)
            ]
        }

        respond match, model:[forecastsList: forecasts]
    }

    def create() {
        respond new Match(params)
    }

    @Transactional
    def save(Match match) {

        if (match == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (match.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond match.errors, view:'create'
            return
        }

        match.save flush:true

        redirect(action: "index", method:"GET")

        /*request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'match.label', default: 'Match'), match.id])
                redirect match
            }
            '*' { respond match, [status: CREATED] }
        }*/
    }

    def edit(Match match) {
        respond match
    }

    @Transactional
    def update(Match match) {
        if (match == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (match.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond match.errors, view:'edit'
            return
        }

        match.save flush:true

        redirect(action: "index", method:"GET")

        /*request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'match.label', default: 'Match'), match.id])
                redirect match
            }
            '*'{ respond match, [status: OK] }
        }*/
    }

    @Transactional
    def delete(Match match) {

        if (match == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        Forecast.findAllByMatch(match)?.each { it.delete(flush: true) }
        match.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'match.label', default: 'Match'), match.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'match.label', default: 'Match'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
