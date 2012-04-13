<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="application/json; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="tictactoe.UserController" %>

{
	"username" : "<s:property value="username" />",
	"hasErrors" : <s:property value="hasActionErrors()" />,
	"errorMessages" : [
		<s:iterator value="actionErrors">
		"<s:property escape="false"/>",
		</s:iterator>
		""
	]
}
