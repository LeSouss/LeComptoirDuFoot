package beans

import grails.transaction.Transactional
import security.User

@Transactional(readOnly = false)
class DayScoreService {

    def generateDayScoreBonus(User user, Day day) {

        if (!DayScore.findByUserAndDay(user, day)) {

            new DayScore(user: user, day: day).save(flush: true)

        }

    }

    def update (Match match) {

        List<Forecast> winningForecasts = getWinningForecasts(match)

        winningForecasts?.each {it ->
            DayScore dayScore = DayScore.findByUserAndDay(it.user, it.match.day)
            //dayScore.points ++

            /*
            if (winningForecasts?.size() < 3) {
                dayScore.bonusPoints ++
            }
             */
            Float score
            if (it.match.awayResult) {
                score = it.match.awayQuote
            } else if (it.match.drawResult){
                score = it.match.drawQuote
            } else {
                score = it.match.homeQuote
            }

            dayScore.nbMatchsOk ++
            dayScore.points = dayScore.points + score
            dayScore.save(flush: true)
            it.isUpdated = Boolean.TRUE
            it.save(flush: true)

        }

        List<Forecast> losesForecasts = getLosesForecasts(match)

        losesForecasts?.each { it ->
            it.isUpdated = Boolean.TRUE
            it.save(flush: true)
        }

    }

    def getWinningForecasts (Match match) {

        List<Forecast> forecasts = new ArrayList<>()

        if (match.homeResult) {
            forecasts.addAll(
                    Forecast.findAllByHomeBetAndMatchAndIsUpdated(Boolean.TRUE, match, Boolean.FALSE)
            )
        } else if (match.drawResult) {
            forecasts.addAll(
                    Forecast.findAllByDrawBetAndMatchAndIsUpdated(Boolean.TRUE, match, Boolean.FALSE)
            )
        } else if (match.awayResult) {
            forecasts.addAll(
                    Forecast.findAllByAwayBetAndMatchAndIsUpdated(Boolean.TRUE, match, Boolean.FALSE)
            )
        }

        return forecasts
    }

    def getLosesForecasts (Match match) {

        List<Forecast> forecasts = new ArrayList<>()

        if (match.homeResult) {
            forecasts.addAll(
                    Forecast.findAllByHomeBetAndMatchAndIsUpdated(Boolean.FALSE, match, Boolean.FALSE)
            )
        } else if (match.drawResult) {
            forecasts.addAll(
                    Forecast.findAllByDrawBetAndMatchAndIsUpdated(Boolean.FALSE, match, Boolean.FALSE)
            )
        } else if (match.awayResult) {
            forecasts.addAll(
                    Forecast.findAllByAwayBetAndMatchAndIsUpdated(Boolean.FALSE, match, Boolean.FALSE)
            )
        }

        return forecasts
    }

}
