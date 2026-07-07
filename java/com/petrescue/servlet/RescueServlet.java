package com.petrescue.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/RescueServlet")
public class RescueServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.html");
            return;
        }

        String name = request.getParameter("name");
        String petType = request.getParameter("petType");
        String petCondition = request.getParameter("petCondition");
        String location = request.getParameter("location");
        String mobile = request.getParameter("mobile");

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO rescue_request (name, pet_type, pet_condition, location, mobile, status) VALUES (?, ?, ?, ?, ?, 'Pending')"
            );

            ps.setString(1, name);
            ps.setString(2, petType);
            ps.setString(3, petCondition);
            ps.setString(4, location);
            ps.setString(5, mobile);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("index.html");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect("rescue.html");
    }
}