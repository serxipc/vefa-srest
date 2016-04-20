package no.sr.ringo.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.sr.ringo.account.RingoAccount;
import no.sr.ringo.account.RingoAccountProvider;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Retrieves the SrAccount object from the account DBMS table and places it into the request scoped context,
 * before forwarding to the upload.jsp page.
 *
 * @author Steinar Overbeck Cook steinar@sendregning.no
 */
@Singleton      // Required by Google Guice
public class UploadServlet extends HttpServlet {


    private final RingoAccountProvider ringoAccountProvider;

    @Inject
    public UploadServlet(RingoAccountProvider ringoAccountProvider) {
        this.ringoAccountProvider = ringoAccountProvider;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new IllegalStateException("POST not supported!");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Retrieves the SrAccount for the authenticated user and stuffs it into the request context.
        request.setAttribute(RingoAccount.class.getSimpleName(), ringoAccountProvider.getAccount(request.getUserPrincipal()));

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/pages/upload.jsp");
        requestDispatcher.forward(request, response);
    }
}
