<%@page import="java.text.SimpleDateFormat"%>
<%@page import="my.Messages"%>
<%@page import="my.Questions"%>
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
            QuestionsJpaController questionControler = new QuestionsJpaController(emf);
            //if(request.getParameter("questionid")==null)request.getServletContext().getRequestDispatcher("http://localhost:8084/NetCrackerForum/topics.jsp").forward(request, response);
            Questions question= questionControler.findQuestions(BigDecimal.valueOf(Integer.parseInt(request.getParameter("questionid"))));
            request.setAttribute("question", question);
            SimpleDateFormat formater = new SimpleDateFormat("dd-MM-YYY hh:mm:ss");
            request.setAttribute("formater", formater);
            for(Messages message:question.getMessagesCollection()){
               System.out.println(message.getMessage());
            }
        %>
        <table border="1" class="categoryTable">
            <thead>
                <tr>
                    <th><h1><b>${question.getName()}</b></h1></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="message" items="${question.getMessagesCollection()}">
                    <tr>
                        <td>
                            
                          <table border="0">
                  
                    <tbody>
                        <tr>
                            <td>${message.getSource()}</td>
                        </tr>
                        <tr>
                            <td>${message.getMessage()}</td>
                        </tr>
                        <tr>
                           <td> ${formater.format(message.getTime())}</td>
                        </tr>
                    </tbody>
                </table>   
                            
                        </td>
               

                      
                 </c:forEach>
            </tbody>
</table>
               
                <form action="AddAnswer" method="POST">
                <table border="0">
                    <tbody>
                        <tr>
                            <td><b>Sasha</b>
                                <input type="hidden" name="name" value="Sasha" />
                                <input type="hidden" name="questionid" value="${question.getId()}" />
                            </td>
                        </tr>
                        <tr>
                            <td><textarea name="message" rows="4" cols="20">
                                </textarea></td>
                        </tr>
                        <tr>
                            <td><input type="submit" value="Post" /></td>
                        </tr>
                    </tbody>
                </table>
            </form>
        
    </body>
</html>
