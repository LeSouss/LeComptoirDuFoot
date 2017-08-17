package beans

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
@Transactional(readOnly = true)
class DayController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE", start: "PUT"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Day.list(params), model:[dayCount: Day.count()]
    }

    @Secured('ROLE_USER')
    def show(Day day) {

        List<Match> matchsList = day?.matchs?.sort { a, b -> a.date <=> b.date ?: a.toString() <=> b.toString() }

        respond day, model:[matchsList: matchsList]
    }

    def create() {
        respond new Day(params)
    }

    @Transactional
    def save(Day day) {
        if (day == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (day.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond day.errors, view:'create'
            return
        }

        day.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'day.label', default: 'Day'), day.id])
                redirect day
            }
            '*' { respond day, [status: CREATED] }
        }
    }

    def edit(Day day) {
        respond day
    }

    @Transactional
    def update(Day day) {
        if (day == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (day.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond day.errors, view:'edit'
            return
        }

        day.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'day.label', default: 'Day'), day.id])
                redirect day
            }
            '*'{ respond day, [status: OK] }
        }
    }

    @Transactional
    def delete(Day day) {

        if (day == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        day.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'day.label', default: 'Day'), day.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'day.label', default: 'Day'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    @Secured('ROLE_USER')
    def bet (Day day) {
        redirect(controller: "forecast", action: "index", params: [dayId: day.id])
    }

    @Transactional
    def Start(Day day) {

        day?.matchs?.each { it ->

            if ((it.date < new Date()) && ("NOT STARTED" == it.status)) {

                it.status = "STARTED"
                it.save(flush: true)

            }

        }

        redirect day

    }

}
