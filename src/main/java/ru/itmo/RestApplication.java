package ru.itmo;

import ru.itmo.resources.DemographyResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class RestApplication extends Application {
    private Set<Object> singletons =
            new HashSet<Object>();

    public RestApplication() {
        singletons.add(
                new DemographyResource());
    }
}
