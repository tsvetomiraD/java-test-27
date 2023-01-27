package embedded.tomcat.db;

import embedded.tomcat.auth.Token;
import embedded.tomcat.model.Comment;
import embedded.tomcat.model.Post;
import embedded.tomcat.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface Mapper {
    @Select("SELECT * FROM posts")
    public Post[] getAllPosts();

    @Select("SELECT * FROM posts WHERE id=#{id}")
    public Post getPostById(int id);

    @Delete("DELETE FROM posts WHERE id=#{id}")
    public int deletePost(int id);

    @Insert("INSERT INTO posts(userId, body, title) VALUES (#{userId},#{body},#{title}")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public int insertPost(Post post);

    @Update("UPDATE posts SET body = {body} WHERE id = #{id}")
    int updatePost(Post post, int id);

    @Select("SELECT * FROM comments")
    public Comment[] getAllComments();

    @Select("SELECT * FROM comments WHERE postId=#{id}")
    public Comment[] getCommentsByPostId(int id);

    @Select("SELECT * FROM users WHERE username=#{name} AND password=#{password}")
    public boolean validUser(String name, String password);

    @Select("SELECT * FROM users WHERE username=#{name}")
    public User getUserBYUsername(String name);


    @Insert("INSERT INTO users(name, username, email, phone, password) VALUES (#{name},#{username},#{email},#{phone},#{password}")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public int registerUser(User user);

    @Select("SELECT * FROM token WHERE userId=#{id}")
    public Token getTokenBYUserId(int id);

    @Select("SELECT * FROM token WHERE token=#{token}")
    public Token getToken(String token);

    @Insert("INSERT INTO token(userId, token, createdDate, expirationDate) VALUES (#{userId},#{token},#{createdDate},#{expirationDate}")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public int createToken(Token token);

    @Select("SELECT * FROM users")
    public List<User> getAllUsers();
}
