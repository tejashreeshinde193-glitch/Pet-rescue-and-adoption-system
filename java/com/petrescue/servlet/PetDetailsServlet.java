package com.petrescue.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import com.petrescue.db.DBConnection;

@WebServlet("/PetDetailsServlet")
public class PetDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");

        if (idParam == null) {
            out.println("<p style='color:red;'>Invalid Pet ID</p>");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM pets WHERE pet_id=?");
            ps.setInt(1, Integer.parseInt(idParam));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                out.println("<html><body style='font-family:Arial;'>");
                out.println("<h2>" + rs.getString("name") + "</h2>");
                out.println("Species: " + rs.getString("species") + "<br>");
                out.println("Breed: " + rs.getString("breed") + "<br>");
                out.println("Age: " + rs.getInt("age") + "<br>");
                out.println("Gender: " + rs.getString("gender") + "<br>");
                out.println("Vaccinated: " + rs.getString("vaccinated") + "<br>");
                out.println("Location: " + rs.getString("location") + "<br>");
                out.println("<b>Description:</b> " + rs.getString("description") + "<br><br>");

                out.println("<form action='AdoptionServlet' method='post'>");
                out.println("<input type='hidden' name='pet_id' value='" + rs.getInt("pet_id") + "'>");
                out.println("Your Name: <input type='text' name='name' required><br>");
                out.println("House Type: <input type='text' name='houseType' required><br>");
                out.println("Experience: <input type='text' name='experience'><br>");
                out.println("Availability: <input type='text' name='availability'><br>");
                out.println("<input type='submit' value='Adopt Now'>");
                out.println("</form>");

                out.println("</body></html>");
            } else {
                out.println("<p style='color:red;'>Pet not found</p>");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}
