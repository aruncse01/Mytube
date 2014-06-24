<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<link href="styles/styles.css" rel="stylesheet" type="text/css"/>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<br/><br/><br/><br/><br/>
<div id="status">
      <div>
      	<span>Last Status Message:
        <c:if test="${sessionScope.result!=null}">
   			<c:out value="${sessionScope.result}"/>
		</c:if>
		</span>
      </div>
</div>