package beans

import grails.transaction.Transactional
import security.User

@Transactional(readOnly = false)
class BonusService {

    def generateBonus(User user, Day day) {

        if (!Bonus.findAllByUserAndDay(user, day)){

            Day previousDay = Day.findByLeagueAndDayNumber(day.league, day.dayNumber-1)
            DayScore dayScore = DayScore.findByUserAndDay(user, previousDay)

            if (dayScore) {
                switch (dayScore?.points?.intValue()) {
                    case 7 :
                        new Bonus(user: user, day: day, type: "Double").save(flush: true)
                        break
                    case 8 :
                        new Bonus(user: user, day: day, forecast: null, type: "Double").save(flush: true)
                        new Bonus(user: user, day: day, forecast: null, type: "Double").save(flush: true)
                        break
                    case 9 :
                        new Bonus(user: user, day: day, forecast: null, type: "Triple").save(flush: true)
                        break
                    case 10 :
                        new Bonus(user: user, day: day, forecast: null, type: "Double").save(flush: true)
                        new Bonus(user: user, day: day, forecast: null, type: "Triple").save(flush: true)
                        break
                    default:
                        break
                }

            }

        }

    }

}
