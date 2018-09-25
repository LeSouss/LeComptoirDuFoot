package beans

import security.User

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
@Transactional(readOnly = false)
class ForecastController {

    def springSecurityService
    def forecastService
    def bonusService
    def dayScoreService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured('ROLE_USER')
    def synchronized index(Integer max) {

        params.max = Math.min(max ?: 10, 100)

        User user = springSecurityService?.currentUser

        List<Bonus> bonusesList = new ArrayList<>()

        if (params.dayId){

            Day day = Day.findById(params.dayId)

            if (day) {

                List<Match> matchsList = Match.findAllByDay(day).toList()

                for (Match m : matchsList){

                    Forecast forecast = Forecast.findByUserAndMatch(user, m)

                    if (!forecast) {
                        create(m, user)
                    }

                }

                dayScoreService.generateDayScoreBonus(user, day)

                bonusService.generateBonus(user, day)

                bonusesList = Bonus.findAllByUserAndDay(user, day)?.sort { it.type }

            }
        }

        List<Forecast> forecastsList = params.dayId ? Forecast?.findAllByUserAndMatchInList(user, Match.findAllByDay(Day.findById(params.dayId)))?.sort { a, b -> a?.match?.date <=> b?.match?.date ?: a?.match?.toString() <=> b?.match?.toString() } : Forecast?.findAllByUser(user)?.sort { a, b -> a?.match?.date <=> b?.match?.date ?: a?.match?.toString() <=> b?.match?.toString() }

        respond forecastsList, model:[forecastCount: forecastsList?.size(), dayId: params?.dayId, bonusesList: bonusesList]
    }

    def show(Forecast forecast) {
        respond forecast
    }

    def create(Match match, User user) {
        save(new Forecast(params), match, user)
    }

    @Transactional
    def save(Forecast forecast, Match match, User user) {
        if (forecast == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (forecast.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond forecast.errors, view:'create'
            return
        }

        if (user) {
            forecast.user = user
        }

        if (match) {
            forecast.match = match
        }
        forecast.save flush:true

        /*request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'forecast.label', default: 'Forecast'), forecast.id])
                redirect forecast
            }
            '*' { respond forecast, [status: CREATED] }
        }*/
    }

    def edit(Forecast forecast) {
        respond forecast
    }

    @Secured('ROLE_USER')
    @Transactional
    def update(Forecast forecast) {

        if (forecast == null) {
            notFound()
            return
        }

        if (forecast.hasErrors()) {
            respond forecast.errors, view:'edit'
            return
        }

        if (forecastService?.verifyForecast(forecast)) {

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'forecast.label', default: "${forecast.match}"), "-"])
                    redirect(action: "index", method:"GET", params: [dayId: forecast?.match?.day?.id])
                }
                '*'{ respond forecast, [status: OK] }
            }

        } else {
            transactionStatus.setRollbackOnly()
            request.withFormat {
                form multipartForm {
                    flash.message = "Impossible de mettre à jour le pronostic pour le macth ${forecast.match}"
                    redirect(action: "index", method:"GET", params: [dayId: forecast?.match?.day?.id])
                }
                '*'{ respond forecast, [status: OK] }
            }
        }

    }

    @Transactional
    def delete(Forecast forecast) {

        if (forecast == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        forecast.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'forecast.label', default: 'Forecast'), forecast.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'forecast.label', default: 'Forecast'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

}
