<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
	</li>
	<openmrs:hasPrivilege privilege="Manage SMART Apps">
		<li <c:if test='<%= request.getRequestURI().contains("smartcontainerForm") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/smartcontainer/smartcontainerLink.form">
				<spring:message code="smartcontainer.admin.manage"/>
			</a>
		</li>
		<li <c:if test='<%= request.getRequestURI().contains("addAppUserLevel") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/smartcontainer/manageUserAppLink.form">
				<spring:message code="smartcontainer.admin.manage.user"/>
			</a>
		</li>
		<li <c:if test='<%= request.getRequestURI().contains("problemsetup") %>'>class="active"</c:if>>
			<a href="${pageContext.request.contextPath}/module/smartcontainer/problemsetup.form">
				<spring:message code="smartcontainer.admin.problemsetup"/>
			</a>
		</li>
	</openmrs:hasPrivilege>
	
</ul>