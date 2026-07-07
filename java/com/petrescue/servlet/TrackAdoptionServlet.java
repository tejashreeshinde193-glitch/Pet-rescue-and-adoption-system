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

@WebServlet("/TrackAdoptionServlet")
public class TrackAdoptionServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // 🔥 TEMP FIX (no redirect issue)
        if (username == null) {
            out.println("<h3>Please login first</h3>");
            return;
        }

        out.println("<html><head><title>Track Adoption</title>");

        out.println("<style>");
        out.println("body { font-family: Arial; background:#f0f8ff; text-align:center; }");
        out.println("h2 { color:#2e8b57; }");
        out.println(".card { width:300px; margin:10px auto; padding:15px; background:white;");
        out.println("border-radius:10px; box-shadow:0 4px 10px rgba(0,0,0,0.2); }");
        out.println("</style>");

        out.println("</head><body>");
        out.println("<h2>Adoption Status</h2>");

        try (Connection con = DBConnection.getConnection()) {

            String sql = "SELECT pet_name, status FROM adopter WHERE name=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            boolean found = false;

            while (rs.next()) {
                found = true;

           //     String petName = rs.getString("pet_name");
                String status = rs.getString("status");

                String color = "black";

                if ("Pending".equalsIgnoreCase(status)) color = "orange";
                else if ("Approved".equalsIgnoreCase(status)) color = "green";
                else if ("Rejected".equalsIgnoreCase(status)) color = "red";

                out.println("<div class='card'>");
              //  out.println("<p><b>Pet:</b> " + petName + "</p>");
                out.println("<p>Status: <span style='color:" + color + ";'>" + status + "</span></p>");
                out.println("</div>");
            }

            if (!found) {
                out.println("<h3>No adoption requests found</h3>");
            }

        } catch (Exception e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }

        out.println("<br><a href='index.html'>Back</a>");
        out.println("</body></html>");
    }
}