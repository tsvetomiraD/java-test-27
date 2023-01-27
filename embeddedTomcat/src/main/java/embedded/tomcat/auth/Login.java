package embedded.tomcat.auth;

import embedded.tomcat.db.Dao;
import embedded.tomcat.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

public class Login extends HttpServlet {
    MessageDigest md = null;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        Dao d = new Dao();
        User user = d.getUserBYUsername(username);
        if (user == null) {
            throw new IOException();
        }
        PrintWriter out = response.getWriter();

        if (!password.equals(user.password)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println("Forbidden");

        }

        out.println("Welcome " + username);
    }
}
