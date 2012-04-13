<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="application/json; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="tictactoe.UserController" %>

{
	"opponent" : "<s:property value="opponent" />",
	"gameID" : <s:property value="gameID" />,
	"hasErrors" : <s:property value="hasActionErrors()" />,
	"errorMessages" : [
		<s:iterator value="actionErrors">
		"<s:property escape="false"/>",
		</s:iterator>
		""
	]
}
