package com.petrescue.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = "user";

        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO users(username, email, password, role) VALUES (?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);  // ✅ correct
            ps.setString(2, email);     // ✅ correct
            ps.setString(3, password);  // ✅ correct
            ps.setString(4, role);      // ✅ correct

            ps.executeUpdate();

            response.getWriter().println("Signup Successful!");

        } catch (Exception e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}