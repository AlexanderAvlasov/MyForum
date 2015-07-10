<%@page import="my.QuestionsJpaController"%>
<%@page import="java.math.BigDecimal"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- 
    Document   : topics
    Created on : 26.03.2015, 7:43:12
    Author     : Sasha
--%>

<%@page import="javax.persistence.EntityManagerFactory"%>
<%@page import="my.TopicsJpaController"%>
<%@page import="my.TopicsJpaController"%>
<%@page import="my.Topics"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="windows-1251"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1251">
        <link rel="stylesheet" type="text/css" href="css/forumcss.css">
        <title>Questions Page</title>
    </head>
    <body>
        
        <%
            EntityManagerFactory emf=(EntityManagerFactory) request.getServletContext().getAttribute("emf");
            TopicsJpaController topicControler = new TopicsJpaController(emf);
            QuestionsJpaController questionControler = new QuestionsJpaController(emf);
            Topics topic= topicControler.findTopics(BigDecimal.valueOf(Integer.parseInt(request.getParameter("topicid"))));
            request.setAttribute("topic", topic);
        %>
        <table border="1" class="categoryTable">
            <thead>
                <tr>
                    <th><h1><b>${topic.getName()}</b></h1></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="question" items="${topic.getQuestionsCollection()}">
                    <tr>
                        <td>
                            <a href="Answers.jsp?questionid=${question.getId()}" >${question.getName()}</a>
                        </td>
                      </tr>  
                 </c:forEach>
            </tbody>
          </table>

        
    </body>
</html>
