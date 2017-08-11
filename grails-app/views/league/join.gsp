<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main" />
    <g:set var="entityName" value="${message(code: 'league.label', default: 'League')}" />
    <title><g:message code="default.edit.label" args="[entityName]" /></title>
</head>
<body>
<a href="#edit-league" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
<div class="nav" role="navigation">
    <ul>

        <sec:access expression="hasRole('ROLE_ADMIN')">
            <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
            <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
            <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
        </sec:access>

    </ul>
</div>


<div>

    <g:if test="${flash.message}">
        <div class="errors" role="alert">${flash.message}</div>
    </g:if>
    <g:form action="joinLeague" method="PUT">
        <fieldset class="form">
            Token :
            <input type="text" name="tokenToJoin"></input>
        </fieldset>
        <fieldset class="buttons">
            <input class="save" type="submit" value="${message(code: 'default.button.join.label', default: 'Joindre')}" />
        </fieldset>
    </g:form>

</div>
</body>
</html>
