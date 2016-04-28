package no.sr.ringo.resource;

import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.spi.container.ResourceFilters;
import no.sr.ringo.account.RegisterUseCase;
import no.sr.ringo.account.RegistrationData;
import no.sr.ringo.account.RegistrationProcessResult;
import no.sr.ringo.account.ValidationResult;
import org.json.JSONException;
import org.json.JSONStringer;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;

/**
 * Register a new user (customer account) in the system.
 * PEPPOL network.
 *
 * @author adam
 */
@Path("/register")
@ResourceFilters(ClientVersionNumberResponseFilter.class)
@RequestScoped
public class RegisterResource extends AbstractMessageResource {

    private final RegisterUseCase registerUseCase;

    @Inject
    public RegisterResource(RegisterUseCase registerUseCase) {
        this.registerUseCase = registerUseCase;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String post(String regDataJSON) {

        try {
            RegistrationData registrationData = RegistrationData.fromJson(regDataJSON);

            ValidationResult validation = registerUseCase.validateData(registrationData);
            if (!validation.isValid()) {
                RegistrationResponse result = new RegistrationResponse("error", validation.getMessage(), RegistrationProcessResult.RegistrationSource.RINGO.name());
                return result.toJSON();
            }

            RegistrationProcessResult registrationProcessResult = registerUseCase.registerUser(registrationData);

            String status = registrationProcessResult.isSuccess() ? "ok" : "error";
            RegistrationResponse result = new RegistrationResponse(status, registrationProcessResult.getMessage(), registrationProcessResult.getSource());

            return result.toJSON();
        } catch (JSONException e) {
            throw new IllegalArgumentException("Wrong data from form", e);
        }

    }

    /**
     * The response of the registration.
     */
    private class RegistrationResponse implements Serializable{

        private String status;
        private String message;
        private String source;

        RegistrationResponse(String status, String message, String source) {
            this.status = status;
            this.message = message;
            this.source = source;
        }

        public String toJSON() throws JSONException {
            return new JSONStringer().object().key("status").value(this.status).key("message").value(this.message).key("source").value(this.source).endObject().toString();
        }

    }

}
