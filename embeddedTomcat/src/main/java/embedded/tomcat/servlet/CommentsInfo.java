package embedded.tomcat.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import embedded.tomcat.db.Dao;
import embedded.tomcat.model.Comment;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CommentsInfo extends HttpServlet {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Dao dao = new Dao();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postId = request.getParameter("postId");
        PrintWriter out = response.getWriter();

        if (postId != null) {
            int id = Integer.parseInt(postId);
            List<Comment> c = dao.getCommentsByPostId(id);
            String jsonInString = gson.toJson(c);
            out.println(jsonInString);
            return;
        }

        List<Comment> c = dao.getAllComments();
        String jsonInString = gson.toJson(c);
        out.println(jsonInString);
    }
}