package embedded.tomcat;

import java.io.File;
import java.util.List;

import embedded.tomcat.db.Dao;
import embedded.tomcat.model.User;
import embedded.tomcat.servlet.CommentsInfo;
import embedded.tomcat.servlet.PostsInfo;
import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Main {
    private static final String AUTH_ROLE = "user";

    public static void main(String[] args) throws Exception {
        if (args.length != 2)
            throw new IllegalArgumentException("Port and thread count are required");

        int port = Integer.parseInt(args[0]);
        String thCount = args[1];

        Tomcat tomcat = setTomcat(port, thCount);

        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
        addServlets(ctx);

        tomcat.start();
        tomcat.getServer().await();
    }

    private static void creteUsersXmlFile() throws Exception {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("tomcat-users");
        document.appendChild(root);

        Dao dao = new Dao();
        List<User> users = dao.getAllUsers();

        for (User u: users) {
            Element role = document.createElement("role");
            Element user = document.createElement("user");
            root.appendChild(role);
            root.appendChild(user);

            Attr attr = document.createAttribute("rolename");
            attr.setValue("user");
            role.setAttributeNode(attr);

            Attr username = document.createAttribute("username");
            username.setValue(u.username);
            user.setAttributeNode(username);

            Attr password = document.createAttribute("password");
            password.setValue(u.password);
            user.setAttributeNode(password);

            Attr roles = document.createAttribute("roles");
            roles.setValue("user");
            user.setAttributeNode(roles);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("xmlfile.xml"));

        transformer.transform(domSource, streamResult);
    }

    public static void authentication(Context ctx) {
        LoginConfig config = new LoginConfig();
        config.setAuthMethod("BASIC");
        ctx.setLoginConfig(config);
        ctx.addSecurityRole(AUTH_ROLE);
        SecurityConstraint constraint = new SecurityConstraint();
        constraint.addAuthRole(AUTH_ROLE);
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/*");
        constraint.addCollection(collection);
        ctx.addConstraint(constraint);
    }

    private static void addServlets(Context ctx) {
        HttpServlet post = new PostsInfo();
        HttpServlet comment = new CommentsInfo();

        Tomcat.addServlet(ctx, "PostServlet", post);
        Tomcat.addServlet(ctx, "CommentServlet", comment);

        ctx.addServletMappingDecoded("/posts/*", "PostServlet");
        ctx.addServletMappingDecoded("/comments/*", "CommentServlet");
    }

    @NotNull
    private static Tomcat setTomcat(int port, String thCount) {
        Tomcat tomcat = new Tomcat();
        Connector connector = new Connector();

        connector.setPort(port);
        tomcat.getService().addConnector(connector);
        tomcat.getConnector().setProperty("maxThreads", thCount);
        tomcat.getConnector().setProperty("acceptCount", thCount);

        return tomcat;
    }
}
