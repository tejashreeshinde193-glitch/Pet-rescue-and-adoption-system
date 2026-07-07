package com.petrescue.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                // ✅ Take values from DB (IMPORTANT)
                String dbUsername = rs.getString("username");
                String role = rs.getString("role");

                HttpSession session = request.getSession(true);

                session.setAttribute("username", dbUsername);
                session.setAttribute("role", role);

                
                if ("user".equals(role)) {
                    
                    response.sendRedirect("index.html");
                    }else {
                        response.sendRedirect("Admin.html");

                    }
                
                
                
                
               // response.sendRedirect("PetListServlet");

            } else {
                response.getWriter().println("Invalid Login!");
            }

        } catch (Exception e) {
            response.getWriter().println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}