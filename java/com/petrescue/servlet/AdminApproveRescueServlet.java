package com.petrescue.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/AdminApproveRescueServlet")
@MultipartConfig(fileSizeThreshold = 1024*1024, maxFileSize = 5*1024*1024, maxRequestSize = 10*1024*1024)
public class AdminApproveRescueServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if(session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        int rescueId = Integer.parseInt(request.getParameter("rescueId"));
        String status = request.getParameter("status");

        Part filePart = request.getPart("petImage");
        String fileName = null;

        if(filePart != null && filePart.getSize() > 0) {
            fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();
            String uploadPath = getServletContext().getRealPath("") + "images";
            filePart.write(uploadPath + "/" + fileName);
        }

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps1 = con.prepareStatement(
                "SELECT pet_type, pet_condition FROM rescue_request WHERE rescue_id=?"
            );
            ps1.setInt(1, rescueId);
            ResultSet rs = ps1.executeQuery();

            if(rs.next()) {
                String petType = rs.getString("pet_type");
                String condition = rs.getString("pet_condition");

                PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO pets(name, breed, status, image) VALUES (?, ?, 'Available', ?)"
                );
                ps2.setString(1, petType);
                ps2.setString(2, condition);
                ps2.setString(3, fileName);
                ps2.executeUpdate();
                ps2.close();
            }

            rs.close();
            ps1.close();

            PreparedStatement ps3 = con.prepareStatement(
                "UPDATE rescue_request SET status=? WHERE rescue_id=?"
            );
            ps3.setString(1, status);
            ps3.setInt(2, rescueId);
            ps3.executeUpdate();
            ps3.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("ViewRescueServlet");
    }
}