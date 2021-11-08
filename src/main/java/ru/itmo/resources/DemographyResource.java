package ru.itmo.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itmo.ServerResponse;
import ru.itmo.XMLUtils.PersonList;
import ru.itmo.XMLUtils.XMLConverter;
import ru.itmo.data.Color;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

@Path("/demography")
public class DemographyResource {
    private final XMLConverter converter;
    private final String personServiceBaseURL;

    public DemographyResource() {
        converter = new XMLConverter();
        personServiceBaseURL = "https://localhost:10081/soa_lab_1-1.0-SNAPSHOT/persons";
    }

    @AllArgsConstructor
    @Getter
    public static class Pair{
        private final Long chosen;
        private final Long amount;
    }

    private Pair countPersonsByHairColor(Color hair_color) throws IOException, JAXBException {
        System.out.println("Count Persons By Hair color");
        URL apiUrl = new URL(personServiceBaseURL + "?hair_color=" + hair_color.toString());
        HttpsURLConnection con = (HttpsURLConnection) apiUrl.openConnection();
        con.setRequestMethod("GET");

        System.out.println("Count Persons Get Response");
        if (con.getResponseCode() != 200) {
            throw new IOException();
        }

        System.out.println("Count Persons Read Buffer");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));

        System.out.println("Count Persons Read Content");
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        PersonList personList = converter.fromStr(content.toString(), PersonList.class);

        System.out.println("Count Persons Check Response");
        if (personList.getPersons() == null)
            return new Pair(0L, 0L);

        return new Pair((long) personList.getPersons().size(), personList.getCount());
    }

    @GET
    @Path("/hair-color/{hair_color}")
    @Produces({MediaType.APPLICATION_XML})
    public Response getAmountOfPeopleByHairColor(@PathParam("hair_color") Color hair_color){
        Long personsCount;

        try{
         personsCount = countPersonsByHairColor(hair_color).getChosen();
        } catch (Exception e) {
            System.out.println(e.toString());
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }

        return Response.ok().entity(converter.toStr(new ServerResponse<>(personsCount))).build();
    }


    @GET
    @Path("/hair-color/{hair_color}/percentage")
    @Produces({MediaType.APPLICATION_XML})
    public Response getPercentOfPeopleByHairColor(@PathParam("hair_color") Color hair_color){
        double personsPercent;

        try{
            Pair countResult = countPersonsByHairColor(hair_color);

            if (countResult.getAmount() == 0)
               personsPercent = 0;
            else
                personsPercent = Double.valueOf(countResult.getChosen()) * 100 / Double.valueOf(countResult.getAmount());
        } catch (Exception e) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }

        return Response.ok().entity(converter.toStr(new ServerResponse<>(personsPercent))).build();
    }

}
