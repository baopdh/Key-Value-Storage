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
    <style>
        table, th, td {
            border: 1px solid black;
            border-collapse: collapse;
            text-align: center;
        }
        th, td {
            padding: 5px;
        }
        th {
            background-color: antiquewhite;
        }
        tr:nth-child(odd) {
            background-color: aliceblue;
        }
    </style>
</head>
<body>
    <h1>Key-Value Storage Profiler</h1>
    <table>
        <tr>
            <th>Api</th>
            <th>Total Request</th>
            <th>Pending Request</th>
            <th>Total Time Process (microsecs)</th>
            <th>Last Time Process (microsecs)</th>
            <th>Process Rate (procs/second)</th>
            <th>Request Rate (reqs/second)</th>
        </tr>
        <% ApiList list = ApiList.getInstance(); %>
        <% for (ApiStat a: list) { %>
            <tr>
                <td><%=a.getName()%></td>
                <td><%=a.getTotalReq()%></td>
                <td><%=a.getPendingReq()%></td>
                <td><%=a.getTotalTimeProc()%></td>
                <td><%=a.getLastTimeProc()%></td>
                <td><%=a.getProcessRate()%></td>
                <td><%=a.getRequestRate()%></td>
            </tr>
        <% } %>
    </table>
</body>
</html>
