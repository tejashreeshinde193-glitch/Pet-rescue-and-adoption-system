package com.petrescue.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/AdminAddPetServlet")
@MultipartConfig
public class AdminAddPetServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect("adminAddPet.html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String breed = request.getParameter("breed");
        String location = request.getParameter("location");

        Part filePart = request.getPart("image");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        String uploadPath = getServletContext().getRealPath("") + File.separator + "images";

        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdir();

        filePart.write(uploadPath + File.separator + fileName);

        try (Connection con = DBConnection.getConnection()) {

            String sql = "INSERT INTO pets(name, breed, location, image, status) VALUES (?, ?, ?, ?, 'Available')";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            ps.setString(2, breed);
            ps.setString(3, location);
            ps.setString(4, fileName);

            ps.executeUpdate();

            response.sendRedirect("PetListServlet");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}