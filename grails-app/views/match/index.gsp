<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'match.label', default: 'Match')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <a href="#list-match" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
        <div class="nav" role="navigation">
            <ul>
                <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
                <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
            </ul>
        </div>
        <div id="list-match" class="content scaffold-list" role="main">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message" role="status">${flash.message}</div>
            </g:if>

            <g:if test="${this.matchList}">
                <table>
                    <tr>
                        <th>Heure</th>
                        <th>Match</th>
                        <th>Status</th>
                    </tr>
                    <g:each var="match" in="${this.matchList}">
                        <tr>
                            <td><g:formatDate type="datetime" style="LONG" timeStyle="SHORT" date="${match.date}"/></td>
                            <td><g:link controller="match" action="show" id="${match.id}">${match.home} - ${match.away}</g:link></td>
                            <td>${match.status}</td>
                        </tr>
                    </g:each>
                </table>
            </g:if>

            <div class="pagination">
                <g:paginate total="${matchCount ?: 0}" />
            </div>
        </div>
    </body>
</html>