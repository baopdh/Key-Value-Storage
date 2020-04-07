<%--
  Created by IntelliJ IDEA.
  User: bao
  Date: 07/04/2020
  Time: 21:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.baopdh.dbserver.profiler.ApiStat" %>
<%@ page import="com.baopdh.dbserver.profiler.ApiList" %>
<html>
<head>
    <title>Database statistics</title>
</head>
<body>
    <h1>Hello JSP</h1>
    <% ApiList list = ApiList.getInstance(); %>
    <% for (ApiStat a: list) { %>
        <tr>
            <td><%=a.getName()%></td>
        </tr>
    <% } %>
</body>
</html>
