package com.petrescue.servlet;

import java.io.File;
import java.io.PrintWriter;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/AdminPetServlet")
@MultipartConfig
public class AdminPetServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        String rescueIdStr = request.getParameter("rescueId");
        if (rescueIdStr == null || rescueIdStr.isEmpty()) {
            response.getWriter().println("Error: rescueId not provided!");
            return;
        }

        int rescueId = Integer.parseInt(request.getParameter("rescueId"));
        String petType = "";
        String condition = "";
        String name = "";
        String location = "";

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM rescue_request WHERE rescue_id=?");
            ps.setInt(1, rescueId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                petType = rs.getString("pet_type");
                condition = rs.getString("pet_condition");
                name = rs.getString("name");
                location = rs.getString("location");
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Admin Panel</title>");

        out.println("<style>");
        out.println("body { font-family: Arial; background:#f4f6f9; margin:0; padding:0; }");
        out.println("h1 { text-align:center; color:#333; margin-top:30px; }");
        out.println("form { width:40%; margin:30px auto; background:#fff; padding:25px; border-radius:10px; box-shadow:0 0 10px rgba(0,0,0,0.1);} ");
        out.println("input[type=text], input[type=file] { width:100%; padding:10px; margin:10px 0; border-radius:5px; border:1px solid #ccc;} ");
        out.println("input[type=submit] { background:#28a745; color:white; padding:12px; border:none; width:100%; border-radius:5px; cursor:pointer;} ");
        out.println("input[type=submit]:hover { background:#218838;} ");
        out.println("a.button { display:inline-block; padding:10px 20px; background:#007bff; color:white; text-decoration:none; border-radius:5px; font-size:14px; }");
        out.println("a.button:hover { background:#0056b3; }");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");

        out.println("<h1>Approve Rescue Request & Add Pet</h1>");
        out.println("<form action='AdminPetServlet' method='post' enctype='multipart/form-data'>");
        out.println("<input type='hidden' name='rescueId' value='" + rescueId + "'>");
        out.println("Pet Name: <input type='text' name='petName' value='" + petType + "' required><br><br>");
        out.println("Breed: <input type='text' name='breed' value='" + condition + "' required><br><br>");
        out.println("Location: <input type='text' name='location' value='" + location + "' required><br><br>");
        out.println("Image: <input type='file' name='image' required><br><br>");
        out.println("<input type='submit' value='Add to Adoption'>");
        out.println("</form>");

        out.println("<div style='text-align:center; margin-top:20px;'>");
        out.println("<a href='Admin.html' class='button'>Back to Admin Dashboard</a>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int rescueId = Integer.parseInt(request.getParameter("rescueId"));
        String petName = request.getParameter("petName");
        String breed = request.getParameter("breed");
        String location = request.getParameter("location");

        // Handle file upload
        Part filePart = request.getPart("image");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        String uploadPath = getServletContext().getRealPath("") + File.separator + "images";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdir();

        filePart.write(uploadPath + File.separator + fileName);

        try (Connection con = DBConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO pets(name, breed, status, location, image) VALUES (?, ?, 'Available', ?, ?)"
            );
            ps.setString(1, petName);
            ps.setString(2, breed);
            ps.setString(3, location);
            ps.setString(4, fileName);
            ps.executeUpdate();
            ps.close();

            PreparedStatement ps2 = con.prepareStatement(
                "UPDATE rescue_request SET status='Approved' WHERE rescue_id=?"
            );
            ps2.setInt(1, rescueId);
            ps2.executeUpdate();
            ps2.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.sendRedirect("ViewRescueServlet");
    }
}