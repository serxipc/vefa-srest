package no.sr.ringo.servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.sr.ringo.account.Account;
import no.sr.ringo.account.RingoAccountProvider;
import no.sr.ringo.account.SrAccountNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    public static final Logger log = LoggerFactory.getLogger(UploadServlet.class);

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
        try {
            request.setAttribute(Account.class.getSimpleName(), ringoAccountProvider.getAccount(request.getUserPrincipal()));
        } catch (SrAccountNotFoundException e) {
            throw new IllegalStateException("No account for user " + request.getUserPrincipal());
        }

        log.info("Redirecting to /WEB-INF/pages/upload.jsp");

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/WEB-INF/pages/upload.jsp");
        requestDispatcher.forward(request, response);
    }
}
