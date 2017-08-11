<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'day.label', default: 'Day')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-day" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <sec:access expression="hasRole('ROLE_ADMIN')">
                    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                    <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                </sec:access>

                <li><a class="home" href="<g:createLink controller="league" action="show" id="${day.league.id}" />">${day.league.name}</a></li>

            </ul>
        </div>

        <g:form resource="${this.day}" method="PUT">
            <fieldset class="buttons">
                <g:link class="edit" action="bet" resource="${this.day}"><g:message code="default.button.bet.label" default="Bet" /></g:link>
                <sec:access expression="hasRole('ROLE_ADMIN')">
                    <g:actionSubmit value="Start" />
                </sec:access>
            </fieldset>
        </g:form>

        <div id="show-day" class="content scaffold-show" role="main">
            <h1>${day.name}</h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>

            <g:if test="${this.matchsList}">
                <table>
                    <tr>
                        <th>Heure</th>
                        <th>Match</th>
                        <th>Status</th>
                    </tr>
                    <g:each var="match" in="${this.matchsList}">
                        <tr>
                            <td><g:formatDate type="datetime" style="LONG" timeStyle="SHORT" date="${match.date}"/></td>
                            <td><g:link controller="match" action="show" id="${match.id}">${match.home} - ${match.away}</g:link></td>
                            <td>${match.status}</td>
                        </tr>
                    </g:each>
                </table>
            </g:if>

            <sec:access expression="hasRole('ROLE_ADMIN')">
                <g:form resource="${this.day}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.day}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                    </fieldset>
                </g:form>
            </sec:access>

        </div>
    </body>
</html>
