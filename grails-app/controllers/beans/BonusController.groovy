package beans

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Secured('ROLE_ADMIN')
@Transactional(readOnly = false)
class BonusController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Bonus.list(params), model:[bonusCount: Bonus.count()]
    }

    def show(Bonus bonus) {
        respond bonus
    }

    def create() {
        respond new Bonus(params)
    }

    @Transactional
    def save(Bonus bonus) {
        if (bonus == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (bonus.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond bonus.errors, view:'create'
            return
        }

        bonus.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'bonus.label', default: 'Bonus'), bonus.id])
                redirect bonus
            }
            '*' { respond bonus, [status: CREATED] }
        }
    }

    def edit(Bonus bonus) {
        respond bonus
    }

    @Transactional
    def update(Bonus bonus) {
        if (bonus == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (bonus.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond bonus.errors, view:'edit'
            return
        }

        bonus.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'bonus.label', default: 'Bonus'), bonus.id])
                redirect bonus
            }
            '*'{ respond bonus, [status: OK] }
        }
    }

    @Transactional
    def delete(Bonus bonus) {

        if (bonus == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        bonus.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'bonus.label', default: 'Bonus'), bonus.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'bonus.label', default: 'Bonus'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
