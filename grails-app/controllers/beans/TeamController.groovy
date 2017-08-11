package beans

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
@Transactional(readOnly = true)
class TeamController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Team.list(params), model:[teamCount: Team.count()]
    }

    def show(Team team) {
        respond team
    }

    def create() {
        respond new Team(params)
    }

    @Transactional
    def save(Team team) {
        if (team == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (team.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond team.errors, view:'create'
            return
        }

        team.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'team.label', default: 'Team'), team.id])
                redirect team
            }
            '*' { respond team, [status: CREATED] }
        }
    }

    def edit(Team team) {
        respond team
    }

    @Transactional
    def update(Team team) {
        if (team == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (team.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond team.errors, view:'edit'
            return
        }

        team.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'team.label', default: 'Team'), team.id])
                redirect team
            }
            '*'{ respond team, [status: OK] }
        }
    }

    @Transactional
    def delete(Team team) {

        if (team == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        team.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'team.label', default: 'Team'), team.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'team.label', default: 'Team'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
