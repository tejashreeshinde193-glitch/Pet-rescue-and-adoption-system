package com.petrescue.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/AdminApproveAdoptionServlet")
public class AdminApproveAdoptionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        int adopterId = Integer.parseInt(request.getParameter("id"));

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps1 = con.prepareStatement(
                "SELECT pet_id FROM adopter WHERE adopter_id=?"
            );
            ps1.setInt(1, adopterId);
            ResultSet rs = ps1.executeQuery();

            if (rs.next()) {
                int petId = rs.getInt("pet_id");

                PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE pets SET status='Adopted' WHERE pet_id=?"
                );
                ps2.setInt(1, petId);
                ps2.executeUpdate();
                ps2.close();
            }
            rs.close();
            ps1.close();

            PreparedStatement ps3 = con.prepareStatement(
                "UPDATE adopter SET status='Approved' WHERE adopter_id=?"
            );
            ps3.setInt(1, adopterId);
            ps3.executeUpdate();
            ps3.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("ViewAdoptionServlet");
    }
}