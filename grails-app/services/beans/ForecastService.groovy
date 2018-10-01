package beans

import grails.transaction.Transactional
import security.User

@Transactional
class ForecastService {

    def springSecurityService

    Boolean verifyForecast (Forecast forecast) {

        Boolean res = Boolean.FALSE
        Integer count = determineCase(forecast)

        switch (count) {
            case 0 :
                res = verifyNone(forecast)
                break
            case 1 :
                res = verifySimple(forecast)
                break
            case 2 :
                res = verifyDouble(forecast)
                break
            case 3 :
                res = verifyTriple(forecast)
                break
            default :
                break
        }

        return res

    }

    Integer determineCase (Forecast forecast) {

        Integer res = 0

        if (forecast.homeBet) {
            res ++
        }

        if (forecast.drawBet) {
            res ++
        }

        if (forecast.awayBet) {
            res ++
        }

        return res
    }

    Boolean checkDate (Forecast forecast) {

        return (forecast?.match?.date >= new Date())

    }

    Boolean verifyNone (Forecast forecast) {

        Boolean res = checkDate(forecast)

        User user = springSecurityService?.currentUser
        Day day = forecast?.match?.day

        if (res) {

            Bonus matchBonus = Bonus.findByUserAndForecastAndDay(user, forecast, day)

            matchBonus?.forecast = null
            matchBonus?.save(flush: true)

            forecast.save(flush: true)

        }

        return res
    }

    Boolean verifySimple (Forecast forecast) {

        Boolean res = checkDate(forecast)

        User user = springSecurityService?.currentUser
        Day day = forecast?.match?.day

        if (res) {

            Bonus matchBonus = Bonus.findByUserAndForecastAndDay(user, forecast, day)

            matchBonus?.forecast = null
            matchBonus?.save(flush: true)

            forecast.save(flush: true)

        }

        return res

    }

    Boolean verifyDouble (Forecast forecast) {

        Boolean res = Boolean.TRUE

        User user = springSecurityService?.currentUser
        Day day = forecast?.match?.day

        Bonus matchBonus = Bonus.findByUserAndForecastAndDay(user, forecast, day)
        Bonus doubleBonus = Bonus.findByUserAndForecastAndDayAndType(user, null, day, "Double")

        if (matchBonus) {

            if (matchBonus?.type == "Double") {

                matchBonus.forecast = forecast

            } else {

                if (matchBonus?.type == "Triple") {

                    if (doubleBonus) {

                        matchBonus.forecast = null
                        doubleBonus.forecast = forecast

                    } else {

                        res = Boolean.FALSE

                    }


                } else {

                    res = Boolean.FALSE

                }


            }

        } else {

            if (doubleBonus) {

                doubleBonus.forecast = forecast

            } else {

                res = Boolean.FALSE

            }

        }

        res = res && checkDate(forecast)
        if (res) {

            forecast.save(flush: true)

            if (doubleBonus) {

                doubleBonus.save(flush: true)

            }

            if (matchBonus) {

                matchBonus.save(flush: true)

            }

        }

        return res

    }

    Boolean verifyTriple (Forecast forecast) {

        Boolean res = Boolean.TRUE

        User user = springSecurityService?.currentUser
        Day day = forecast?.match?.day

        Bonus matchBonus = Bonus.findByUserAndForecastAndDay(user, forecast, day)
        Bonus tripleBonus = Bonus.findByUserAndForecastAndDayAndType(user, null, day, "Triple")

        if (matchBonus) {

            if (matchBonus?.type == "Triple") {

                matchBonus.forecast = forecast

            } else {

                if (matchBonus?.type == "Double") {

                    if (tripleBonus) {

                        matchBonus.forecast = null
                        tripleBonus.forecast = forecast

                    } else {

                        res = Boolean.FALSE

                    }


                } else {

                    res = Boolean.FALSE

                }


            }

        } else {

            if (tripleBonus) {

                tripleBonus.forecast = forecast

            } else {

                res = Boolean.FALSE

            }

        }

        res = res && checkDate(forecast)
        if (res) {

            forecast.save(flush: true)

            if (tripleBonus) {

                tripleBonus.save(flush: true)

            }

            if (matchBonus) {

                matchBonus.save(flush: true)

            }

        }

        return res

    }

}
