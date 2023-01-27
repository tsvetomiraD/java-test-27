package embedded.tomcat.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import embedded.tomcat.db.Dao;
import embedded.tomcat.model.Post;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostsInfo extends HttpServlet {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Dao dao = new Dao();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getPathInfo();
        PrintWriter out = response.getWriter();

        if (path == null) {
            sendAllPost(out, request);
            return;
        }

        Pattern pattern = Pattern.compile("/([a-zA-z1-9]+)*");
        Matcher match = pattern.matcher(path);
        match.find();
        switch (match.groupCount()) {
            case 1 : showPostById(match.group(), out, request);
            case 2 : showCommentsForPost(match.group(), request, response);
            default : sendError(out);
        }
    }

    private void sendAllPost(PrintWriter out, HttpServletRequest request) {
        List<Post> eL = dao.getAllPosts();
        String jsonInString = gson.toJson(eL);
        out.println(jsonInString);
    }

    private void showCommentsForPost(String idString, HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(idString.substring(1));
        RequestDispatcher dispatcher = request.getRequestDispatcher("/comments?postId=" + id);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private void showPostById(String idString, PrintWriter out, HttpServletRequest request) {
        int id = Integer.parseInt(idString.substring(1));
        Post e = dao.getPostById(id);
        String jsonInString = gson.toJson(e);
        out.println(jsonInString);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        StringBuilder sb1 = new StringBuilder();
        String js = request.getReader().readLine();
        while (js != null) {
            sb1.append(js);
            js = request.getReader().readLine();
        }

        Post post = gson.fromJson(sb1.toString(), Post.class);
        dao.insertPost(post);
        out.println(gson.toJson(post.id));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String path = request.getPathInfo();

        if (path == null) {
            String jsonInString = gson.toJson(null);
            out.println(jsonInString);
            return;
        }
        Pattern pattern = Pattern.compile("([a-zA-z1-9]+)*");
        Matcher match = pattern.matcher(path);
        int id = Integer.parseInt(match.group());

        StringBuilder sb1 = new StringBuilder();
        String js = request.getReader().readLine();
        while (js != null) {
            sb1.append(js);
            js = request.getReader().readLine();
        }

        Post post = gson.fromJson(sb1.toString(), Post.class);
        post.id = id;
        dao.updatePost(post, id);
        out.println(gson.toJson(post));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();
        PrintWriter out = response.getWriter();

        if (path == null) {
            sendError(out);
            return;
        }

        Pattern pattern = Pattern.compile("([a-zA-z1-9]+)*");
        Matcher match = pattern.matcher(path);
        int id = Integer.parseInt(match.group());

        int n = dao.deletePost(id);
        String jsonInString = gson.toJson(n);
        out.println(jsonInString);
    }
    private void sendError(PrintWriter out) {
        String jsonInString = gson.toJson(null);
        out.println(jsonInString);
    }
}
