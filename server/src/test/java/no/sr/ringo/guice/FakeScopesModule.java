package no.sr.ringo.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.servlet.RequestScoped;

import javax.servlet.ServletContext;

/**
 * User: andy
 * Date: 10/4/12
 * Time: 8:37 AM
 */
public class FakeScopesModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bindScope(RequestScoped.class, new FakeScope());
        binder.bind(ServletContext.class).toInstance(new FakeServletContext());
    }
}