package beans

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
@Transactional(readOnly = true)
class ScoreController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Score.list(params), model:[scoreCount: Score.count()]
    }

    def show(Score score) {
        respond score
    }

    def create() {
        respond new Score(params)
    }

    @Transactional
    def save(Score score) {
        if (score == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (score.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond score.errors, view:'create'
            return
        }

        score.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'score.label', default: 'Score'), score.id])
                redirect score
            }
            '*' { respond score, [status: CREATED] }
        }
    }

    def edit(Score score) {
        respond score
    }

    @Transactional
    def update(Score score) {
        if (score == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (score.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond score.errors, view:'edit'
            return
        }

        score.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'score.label', default: 'Score'), score.id])
                redirect score
            }
            '*'{ respond score, [status: OK] }
        }
    }

    @Transactional
    def delete(Score score) {

        if (score == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        score.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'score.label', default: 'Score'), score.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'score.label', default: 'Score'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
