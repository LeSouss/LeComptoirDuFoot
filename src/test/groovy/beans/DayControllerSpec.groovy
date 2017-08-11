package beans

import grails.test.mixin.*
import spock.lang.*

@TestFor(DayController)
@Mock(Day)
class DayControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null

        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
        assert false, "TODO: Provide a populateValidParams() implementation for this generated test suite"
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.dayList
            model.dayCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.day!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def day = new Day()
            day.validate()
            controller.save(day)

        then:"The create view is rendered again with the correct model"
            model.day!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            day = new Day(params)

            controller.save(day)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/day/show/1'
            controller.flash.message != null
            Day.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def day = new Day(params)
            controller.show(day)

        then:"A model is populated containing the domain instance"
            model.day == day
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def day = new Day(params)
            controller.edit(day)

        then:"A model is populated containing the domain instance"
            model.day == day
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/day/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def day = new Day()
            day.validate()
            controller.update(day)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.day == day

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            day = new Day(params).save(flush: true)
            controller.update(day)

        then:"A redirect is issued to the show action"
            day != null
            response.redirectedUrl == "/day/show/$day.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/day/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def day = new Day(params).save(flush: true)

        then:"It exists"
            Day.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(day)

        then:"The instance is deleted"
            Day.count() == 0
            response.redirectedUrl == '/day/index'
            flash.message != null
    }
}
