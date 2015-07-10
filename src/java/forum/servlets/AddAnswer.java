/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package forum.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import my.Messages;
import my.MessagesJpaController;
import my.Questions;
import my.QuestionsJpaController;

/**
 *
 * @author Sasha
 */
public class AddAnswer extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String message=request.getParameter("message");
        String name = request.getParameter("name");
        BigDecimal id= BigDecimal.valueOf(Integer.parseInt(request.getParameter("questionid")));
        EntityManagerFactory emf=(EntityManagerFactory) request.getServletContext().getAttribute("emf");
        MessagesJpaController messageController = new MessagesJpaController(emf);
        QuestionsJpaController questionController= new QuestionsJpaController(emf);
        Messages newMessage= new Messages();
        Questions idquestion=questionController.findQuestions(id);
        newMessage.setIdqestion(idquestion);
        newMessage.setMessage(message);
        newMessage.setSource(name);
        newMessage.setTime(new Date());
        try {
            messageController.create(newMessage);
        } catch (Exception ex) {
            Logger.getLogger(AddAnswer.class.getName()).log(Level.SEVERE, null, ex);
        }
          response.sendRedirect("Answers.jsp?questionid="+questionController.findQuestions(id).getId());
        }
    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
