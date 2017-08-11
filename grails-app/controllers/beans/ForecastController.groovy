package beans

import security.User

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_ADMIN')
@Transactional(readOnly = true)
class ForecastController {

    def springSecurityService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured('ROLE_USER')
    def index(Integer max) {

        params.max = Math.min(max ?: 10, 100)

        User user = springSecurityService?.currentUser

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

            }
        }

        List<Forecast> forecastsList = Forecast?.findAllByUser(user)?.sort { it?.match?.date }

        respond forecastsList, model:[forecastCount: forecastsList?.size(), dayId: params?.dayId]
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
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (forecast.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond forecast.errors, view:'edit'
            return
        }

        if (verifyForecast(forecast)) {

            forecast.save flush:true

            Float quote = getQuote(forecast)

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'forecast.label', default: "${forecast.match}"), "(${quote})"])
                    redirect(action: "index", method:"GET", params: [dayId: forecast?.match?.day?.id])
                }
                '*'{ respond forecast, [status: OK] }
            }

        } else {
            transactionStatus.setRollbackOnly()
            request.withFormat {
                form multipartForm {
                    flash.message = "Impossible de mettre à jour le pronostic pour le macth ${forecast.match}"
                    redirect(action: "index", method:"GET")
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

    Float getQuote (Forecast forecast) {

        Float quote

        if (forecast.homeBet) {
            quote = forecast.match.homeQuote
        } else if (forecast.drawBet) {
            quote = forecast.match.drawQuote
        } else if (forecast.awayBet) {
            quote = forecast.match.awayQuote
        } else {
            quote = 0
        }

        return quote

    }

    Boolean verifyForecast (Forecast forecast) {

        return checkQuote(forecast) && checkDouble(forecast) && checkDate(forecast)

    }

    Boolean checkQuote (Forecast forecast) {

        return (forecast.homeBet && !forecast.drawBet && !forecast.awayBet) ||
                (!forecast.homeBet && forecast.drawBet && !forecast.awayBet) ||
                (!forecast.homeBet && !forecast.drawBet && forecast.awayBet) ||
                (!forecast.homeBet && !forecast.drawBet && !forecast.awayBet)

    }

    Boolean checkDouble (Forecast forecast) {

        Integer count = 0
        User user = springSecurityService?.currentUser

        forecast.match.day.matchs.each { it ->

            if (Forecast.findByUserAndMatch(user, it).isDouble) {
                count ++
            }

        }

        return (count <= 1)

    }

    Boolean checkDate (Forecast forecast) {

        return (forecast?.match?.date >= new Date())

    }

}
