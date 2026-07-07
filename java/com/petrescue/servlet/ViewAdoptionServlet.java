package com.petrescue.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/ViewAdoptionServlet")
public class ViewAdoptionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔒 Admin check
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // HTML start
        out.println("<!DOCTYPE html>");
        out.println("<html><head><meta charset='UTF-8'>");
        out.println("<title>Adoption Requests</title>");

        // 🎨 CSS
        out.println("<style>");
        out.println("body { font-family: Arial; background-color: #f0f8ff; text-align: center; }");
        out.println("h1 { color: darkgreen; }");
        out.println("table { margin: 20px auto; border-collapse: collapse; width: 90%; }");
        out.println("th, td { border: 1px solid #2e8b57; padding: 10px; }");
        out.println("th { background-color: #2e8b57; color: white; }");
        out.println("tr:nth-child(even) { background-color: #e0f2f1; }");
        out.println("img { border-radius: 10px; }");
        out.println("a { text-decoration: none; color: blue; font-weight: bold; }");
        out.println("</style>");

        out.println("</head><body>");

        out.println("<h1>Adoption Requests</h1>");

        out.println("<table>");
        out.println("<tr><th>ID</th><th>Adopter Name</th><th>Image</th><th>House Type</th><th>Experience</th><th>Availability</th><th>Status</th><th>Action</th></tr>");

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT a.*, p.image FROM adopter a JOIN pets p ON a.pet_id = p.pet_id";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("adopter_id");
                String adopterName = rs.getString("name");
                String houseType = rs.getString("house_type");
                String experience = rs.getString("experience");
                String availability = rs.getString("availability");
                String status = rs.getString("status");
                String image = rs.getString("image");

                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + adopterName + "</td>");

                out.println("<td><img src='images/" + image + "' width='100' height='100'></td>");

                out.println("<td>" + houseType + "</td>");
                out.println("<td>" + experience + "</td>");
                out.println("<td>" + availability + "</td>");
                out.println("<td>" + status + "</td>");

                if ("Pending".equals(status)) {
                    out.println("<td><a href='AdminApproveAdoptionServlet?id=" + id + "'>Approve</a></td>");
                } else {
                    out.println("<td style='color: green; font-weight: bold;'>Approved</td>");
                }

                out.println("</tr>");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            out.println("<tr><td colspan='8' style='color:red;'>Error: " + e.getMessage() + "</td></tr>");
        }

        out.println("</table>");

        out.println("<br><a href='Admin.html'>Back to Admin Dashboard</a>");

        out.println("</body></html>");
    }
}