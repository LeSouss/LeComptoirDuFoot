<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'league.label', default: 'League')}"/>
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-league" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <sec:access expression="hasRole('ROLE_ADMIN')">
            <div class="nav" role="navigation">
                <ul>
                    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                </ul>
            </div>

            <g:form resource="${this.league}" method="PUT">
                <fieldset class="buttons">
                    <g:actionSubmit class="edit" value="UpdateScore" />
                </fieldset>
            </g:form>
        </sec:access>

        <div id="show-league" class="content scaffold-show" role="main">
            <h1>${this.league.name}</h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <div class="svg" role="presentation">
                <div class="grails-logo-container">
                    <asset:image src="Podium.png" class="grails-logo"/>
                </div>
            </div>

            <g:if test="${this.scoresList}">
                <table>
                    <tr>
                        <th>User</th>
                        <th>Points</th>
                    </tr>
                    <g:each var="score" in="${this.scoresList}">
                        <tr>
                            <td>${score.user}</td>
                            <td><g:formatNumber number="${score.points}" type="number"/></td>
                        </tr>
                    </g:each>
                </table>
            </g:if>

            <g:if test="${this.daysList}">
                <table>
                    <tr>
                        <th>Journées</th>
                        <th>Points</th>
                    </tr>
                    <g:each var="day" in="${this.daysList}">
                        <tr>
                            <td><g:link class="edit" action="show" resource="${day}">${day?.name} ${day?.dayNumber}</g:link></td>
                            <td><g:formatNumber number="${dayScoresMap?.get(day?.dayNumber)?.points}" type="number"/></td>
                        </tr>
                    </g:each>
                </table>
            </g:if>

            <sec:access expression="hasRole('ROLE_ADMIN')">
                <g:form resource="${this.league}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.league}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>
            </sec:access>

        </div>
    </body>
</html>
