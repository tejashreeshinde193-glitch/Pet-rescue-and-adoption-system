package com.petrescue.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/AdoptionServlet")
public class AdoptionServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.html");
            return;
        }

        int petId = Integer.parseInt(request.getParameter("petId"));
        String name = request.getParameter("name");
        String houseType = request.getParameter("houseType");
        String experience = request.getParameter("experience");
        String availability = request.getParameter("availability");

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO adopter (pet_id, name, house_type, experience, availability, status) VALUES (?, ?, ?, ?, ?, 'Pending')"
            );
            ps.setInt(1, petId);
            ps.setString(2, name);
            ps.setString(3, houseType);
            ps.setString(4, experience);
            ps.setString(5, availability);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("PetListServlet");
    }
}