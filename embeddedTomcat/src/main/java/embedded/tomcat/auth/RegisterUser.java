package embedded.tomcat.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import embedded.tomcat.db.Dao;
import embedded.tomcat.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RegisterUser extends HttpServlet {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    MessageDigest md = null;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        Dao d = new Dao();

        StringBuilder sb1 = new StringBuilder();
        String js = request.getReader().readLine();
        while (js != null) {
            sb1.append(js);
            js = request.getReader().readLine();
        }

        User user = gson.fromJson(sb1.toString(), User.class);
        String password = user.password;

        try {
           md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        md.update(salt);

        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : hashedPassword)
            sb.append(String.format("%02x", b));

        user.password = sb.toString();
        user.salt = salt;
        d.registerUser(user);
        out.println(gson.toJson(user.id));
    }
}
