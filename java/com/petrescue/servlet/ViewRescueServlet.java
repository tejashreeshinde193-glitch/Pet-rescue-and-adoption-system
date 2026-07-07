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

@WebServlet("/ViewRescueServlet")
public class ViewRescueServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<h1 style='color: #2e8b57; text-align:center;'>Rescue Requests Dashboard</h1>");
        out.println("<body style='text-align:center;'>");
        out.println("<table border='1' style='width:90%; margin:auto; text-align:center; border-collapse: collapse;'>");
        out.println("<tr style='background-color:#2e8b57; color:white;'>"
                + "<th>ID</th>"
                + "<th>Name</th>"
                + "<th>Mobile</th>"
                + "<th>Pet Type</th>"
                + "<th>Condition</th>"
                + "<th>Location</th>"
                + "<th>Status</th>"
                + "<th>Action</th>"
               // + "<th>Move to Adoption</th>"
                + "</tr>");

        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM rescue_request";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("rescue_id");
                String name = rs.getString("name");
                String mobile = rs.getString("mobile");
                String petType = rs.getString("pet_type");
                String condition = rs.getString("pet_condition");
                String location = rs.getString("location");
                String status = rs.getString("status");

                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + mobile + "</td>");
                out.println("<td>" + petType + "</td>");
                out.println("<td>" + condition + "</td>");
                out.println("<td>" + location + "</td>");

                out.println("<td>");
                out.println("<form action='UpdateRescueStatusServlet' method='post'>");
                out.println("<input type='hidden' name='rescueId' value='" + id + "'>");
                out.println("<select name='status'>");
                out.println("<option value='Pending'" + ("Pending".equals(status) ? " selected" : "") + ">Pending</option>");
                out.println("<option value='Rescued'" + ("Rescued".equals(status) ? " selected" : "") + ">Rescued</option>");
                out.println("<option value='Approved'" + ("Approved".equals(status) ? " selected" : "") + ">Approved</option>");
                out.println("</select>");
                out.println("<input type='submit' value='Update'>");
                out.println("</form>");
                out.println("</td>");

                if ("Rescued".equals(status)) {
                    out.println("<td><a href='AdminPetServlet?rescueId=" + id + "'>Add to Adoption</a></td>");
                } else {
                    out.println("<td>--</td>");
                }

                out.println("</tr>");
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Error: " + e.getMessage() + "</h3>");
        }

        out.println("</table>");
        
        out.println("<br><a href='Admin.html'>    Back to Admin Dashboard</a>");
        
        out.println("</body></html>");

    }
}