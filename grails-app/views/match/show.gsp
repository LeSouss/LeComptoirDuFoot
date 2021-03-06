<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'match.label', default: 'Match')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#show-match" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>

            <sec:access expression="hasRole('ROLE_ADMIN')">
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </sec:access>

                <li><a class="home" href="<g:createLink controller="day" action="show" id="${this.match.day.id}" />">${this.match.day.name}</a></li>
            </ul>
        </div>
        <div id="show-match" class="content scaffold-show" role="main">
            <h1>${this.match}</h1>
            <g:if test="${flash.message}">
            <div class="message" role="status">${flash.message}</div>
            </g:if>

            <g:if test="${this.forecastsList}">
                <table>
                    <tr>
                        <th>User</th>
                        <th>Prono</th>
                        <th>Bonus</th>
                    </tr>
                    <g:each var="forecast" in="${this.forecastsList}">
                        <tr>
                            <td>${forecast.user}</td>

                            <td>
                                <g:if test="${forecast.homeBet}">
                                   ${forecast.match.home}
                                </g:if>
                                <g:if test="${forecast.drawBet}">
                                    N
                                </g:if>
                                <g:if test="${forecast.awayBet}">
                                    ${forecast.match.away}
                                </g:if>
                            </td>

                            <td>
                                ${forecast.toStringMultiple()}
                            </td>

                        </tr>
                    </g:each>
                </table>
            </g:if>

            <sec:access expression="hasRole('ROLE_ADMIN')">
                <g:form resource="${this.match}" method="DELETE">
                    <fieldset class="buttons">
                        <g:link class="edit" action="edit" resource="${this.match}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
                        <input class="delete" type="submit" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
                        <g:link class="edit" action="updateHome" resource="${this.match}"><g:message code="default.button.update.home.label" default="Update Home" /></g:link>
                        <g:link class="edit" action="updateDraw" resource="${this.match}"><g:message code="default.button.update.draw.label" default="Update Draw" /></g:link>
                        <g:link class="edit" action="updateAway" resource="${this.match}"><g:message code="default.button.update.away.label" default="Update Away" /></g:link>
                    </fieldset>
                </g:form>
            </sec:access>

        </div>
    </body>
</html>
