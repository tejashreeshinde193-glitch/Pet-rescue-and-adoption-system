package com.petrescue.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.petrescue.db.DBConnection;

@WebServlet("/PetListServlet")
public class PetListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        String role = "user"; 

        if (session != null && session.getAttribute("role") != null) {
            role = session.getAttribute("role").toString().toLowerCase();
        }

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Available Pets</title>");
        
        //changes
        if ("user".equals(role)) {
            
        out.println("<a href='TrackAdoptionServlet'>Track Adoption Status</a><br><br>");
        }
        
        
        out.println("<div style='margin:20px auto; text-align:center;'>");

        out.println("<form method='get' action='PetListServlet' " +
                "style='display:inline-flex; gap:10px; padding:15px; " +
                "background:white; border-radius:12px; box-shadow:0 4px 10px rgba(0,0,0,0.2); align-items:center;'>");

        out.println("<input type='text' name='search' placeholder='Search by name' " +
                "style='padding:10px; border-radius:8px; border:1px solid #ccc; width:180px;'>");

        out.println("<input type='text' name='type breed' placeholder='Search by breed' " +
                "style='padding:10px; border-radius:8px; border:1px solid #ccc; width:180px;'>");

        out.println("<input type='text' name='type location' placeholder='Search by location' " +
                "style='padding:10px; border-radius:8px; border:1px solid #ccc; width:180px;'>");


        out.println("<button type='submit' " +
                "style='padding:10px 18px; background:#2e8b57; color:white; border:none; " +
                "border-radius:8px; cursor:pointer;'>Search</button>");

        out.println("</form>");
        out.println("</div>");
        //end change
        
        
        
        


        out.println("<style>");
        out.println("body { font-family: Arial; background:#f0f8ff; text-align:center; }");
        out.println("h1 { color:#2e8b57; }");

        out.println(".container { display:flex; flex-wrap:wrap; justify-content:center; }");

        out.println(".card { width:200px; margin:15px; padding:10px; border-radius:10px;");
        out.println("box-shadow:0 4px 10px rgba(0,0,0,0.2); background:white; }");

        out.println(".card img { width:100%; height:150px; object-fit:cover; border-radius:10px; }");

        out.println(".btn { display:inline-block; margin-top:10px; background:#4CAF50;");
        out.println("color:white; padding:8px 12px; text-decoration:none; border-radius:5px; }");

        out.println(".btn:hover { background:#388e3c; }");
        out.println("</style>");

        out.println("</head><body>");
        out.println("<h1> Available Pets for Adoption</h1>");


        out.println("<div class='container'>");

        try (Connection con = DBConnection.getConnection()) {
        	
        	//change
        	String search = request.getParameter("search");
        	String breedFilter = request.getParameter("breed");
        	//end change

        	
        	//changes
        	String sql = "SELECT pet_id, name, breed, status, image FROM pets WHERE status='Available'";

        	if (search != null && !search.trim().isEmpty()) {
        	    sql += " AND name LIKE '%" + search + "%'";
        	}

        	if (breedFilter != null && !breedFilter.isEmpty()) {
        	    sql += " AND breed='" + breedFilter + "'";
        	}

        	// Prepare statement
        	PreparedStatement ps = con.prepareStatement(sql);

        	// Execute query
        	ResultSet rs = ps.executeQuery();
            //end change
            
            while (rs.next()) {

                int petId = rs.getInt("pet_id");
                String petName = rs.getString("name");
                String breed = rs.getString("breed");
                String status = rs.getString("status");
                String petImg = rs.getString("image");

                if (petImg == null || petImg.trim().isEmpty()) {
                    petImg = "default.jpg";
                }

                String encodedName = URLEncoder.encode(petName, "UTF-8");

                out.println("<div class='card'>");

                out.println("<img src='images/" + petImg + "'>");

                out.println("<h3>" + petName + "</h3>");
                out.println("<p><b>Breed:</b> " + (breed != null ? breed : "Not Specified") + "</p>");
                out.println("<p><b>Status:</b> " + status + "</p>");

                if ("admin".equals(role)) {
                    out.println("<p style='color:red; font-weight:bold;'>Admin View</p>");
                } else {
                    out.println("<a class='btn' href='adoption.html?petId=" + petId +
                            "&petName=" + encodedName +
                            "&petImg=" + petImg + "'>Adopt </a>");
                }

                out.println("</div>");
            }

            rs.close();
            ps.close();
            
            

        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Error: " + e.getMessage() + "</h3>");
        }
        

        out.println("</div>");


        if ("admin".equals(role)) {
            out.println("<br><a href='Admin.html'> <- Back to Dashboard</a>");
        } else {
            out.println("<br><a href='index.html'> <- Back to Dashboard</a>");
        }                
        out.println("</body></html>");
    }
}