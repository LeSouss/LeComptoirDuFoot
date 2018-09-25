<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'forecast.label', default: 'Forecast')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#list-forecast" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>

                <sec:access expression="hasRole('ROLE_ADMIN')">
                    <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                    <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
                </sec:access>

                <g:if test="${dayId}">
                    <li><a class="home" href=" <g:createLink controller="day" action="show" id="${dayId}" />">Précédent</a></li>
                </g:if>

            </ul>
        </div>
        <div id="list-bonus" class="content scaffold-list" role="main">
            <h1>Bonus</h1>
            <table>
                <tr>
                    <th>Type</th>
                    <th>Match</th>
                </tr>
                <g:each var="bonus" in="${this.bonusesList}">
                    <tr>
                        <td>${bonus?.type}</td>
                        <td>${bonus?.forecast?.match}</td>
                    </tr>
                </g:each>
            </table>

            <div class="pagination">
                <g:paginate total="${bonus?.size() ?: 0}" />
            </div>
        </div>

        <div id="list-forecast" class="content scaffold-list" role="main">
            <h1>Pronos</h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>
            <table>
                <tr>
                    <th>Heure</th>
                    <th>Match</th>
                    <th>Pronostic - 1/N/2</th>
                    <th></th>
                </tr>
                <g:each var="forecast" in="${this.forecastList}">
                    <tr>
                        <g:form resource="${forecast}" method="PUT">
                            <td>
                                <g:formatDate type="datetime" style="LONG" timeStyle="SHORT" date="${forecast.match.date}"/>
                                <g:hiddenField name="version" value="${this.forecast?.version}" />
                            </td>
                            <td>${forecast.match.home} - ${forecast.match.away}</td>
                            <td>
                                <div class="ck-button">
                                    <label>
                                        <g:checkBox name="homeBet" value="${forecast.homeBet}"/>
                                        <span>1</span>
                                    </label>
                                </div>
                                <div class="ck-button">
                                    <label>
                                        <g:checkBox name="drawBet" value="${forecast.drawBet}"/>
                                        <span>N</span>
                                    </label>
                                </div>
                                <div class="ck-button">
                                    <label>
                                        <g:checkBox name="awayBet" value="${forecast.awayBet}"/>
                                        <span>2</span>
                                    </label>
                                </div>
                            </td>
                            <!-- Add dans un fieldset pour envoie au controller + form -->
                            <td>
                                <input class="save" type="submit" value="${message(code: 'default.button.update.label', default: 'Update')}" />
                            </td>
                        </g:form>
                    </tr>
                </g:each>
            </table>

            <div class="pagination">
                <g:paginate total="${forecastCount ?: 0}" />
            </div>
        </div>
    </body>
</html>