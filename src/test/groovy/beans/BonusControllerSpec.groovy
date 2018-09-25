package beans

import grails.test.mixin.*
import spock.lang.*

@TestFor(BonusController)
@Mock(Bonus)
class BonusControllerSpec extends Specification {

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
            !model.bonusList
            model.bonusCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.bonus!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def bonus = new Bonus()
            bonus.validate()
            controller.save(bonus)

        then:"The create view is rendered again with the correct model"
            model.bonus!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            bonus = new Bonus(params)

            controller.save(bonus)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/bonus/show/1'
            controller.flash.message != null
            Bonus.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def bonus = new Bonus(params)
            controller.show(bonus)

        then:"A model is populated containing the domain instance"
            model.bonus == bonus
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def bonus = new Bonus(params)
            controller.edit(bonus)

        then:"A model is populated containing the domain instance"
            model.bonus == bonus
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/bonus/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def bonus = new Bonus()
            bonus.validate()
            controller.update(bonus)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.bonus == bonus

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            bonus = new Bonus(params).save(flush: true)
            controller.update(bonus)

        then:"A redirect is issued to the show action"
            bonus != null
            response.redirectedUrl == "/bonus/show/$bonus.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/bonus/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def bonus = new Bonus(params).save(flush: true)

        then:"It exists"
            Bonus.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(bonus)

        then:"The instance is deleted"
            Bonus.count() == 0
            response.redirectedUrl == '/bonus/index'
            flash.message != null
    }
}
