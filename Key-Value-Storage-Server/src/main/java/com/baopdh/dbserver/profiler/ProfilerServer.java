package com.baopdh.dbserver.profiler;

import com.baopdh.dbserver.util.ConfigGetter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProfilerServer {
    Server server;

    public ProfilerServer() {
        this.createServer(ConfigGetter.getInt("db.profiler.port", 6969));
    }

    public ProfilerServer(int port) {
        this.createServer(port);
    }

    public boolean start() {
        try {
            ApiList.getInstance(); // initialize ApiList
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void createServer(int port) {
        this.server = new Server(port);
        WebAppContext ctx = new WebAppContext();
        ctx.setResourceBase("src/main/jsp");
        ctx.setContextPath("/");

        // Including the JSTL jars for the webapp.
        ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/[^/]*jstl.*\\.jar$");

        // Enabling the Annotation based configuration
        Configuration.ClassList classlist = Configuration.ClassList.setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");

        // Setting the handler and starting the Server
        this.server.setHandler(ctx);
    }
}