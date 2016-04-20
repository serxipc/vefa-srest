package no.sr.ringo.guice;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
* User: andy
* Date: 10/4/12
* Time: 8:39 AM
*/
class FakeServletContext implements ServletContext {

    @Override
    public String getContextPath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ServletContext getContext(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMajorVersion() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMinorVersion() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getMimeType(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set getResourcePaths(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public URL getResource(String s) throws MalformedURLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getResourceAsStream(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Servlet getServlet(String s) throws ServletException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration getServlets() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration getServletNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log(Exception e, String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void log(String s, Throwable throwable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getRealPath(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getServerInfo() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getInitParameter(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration getInitParameterNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getAttribute(String s) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAttribute(String s, Object o) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAttribute(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getServletContextName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
