// src/main/java/webservices/UniteEnseignementResource.java
package webservices;

import entities.UniteEnseignement;
import metiers.UniteEnseignementBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/UE")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_JSON)
public class UniteEnseignementResource {

    private UniteEnseignementBusiness ueBusiness = new UniteEnseignementBusiness();

    // 1) Création d'une nouvelle unité d'enseignement - POST /UE
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response createUniteEnseignement(UniteEnseignement ue) {
        if (ueBusiness.getUEByCode(ue.getCode()) != null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        ueBusiness.addUniteEnseignement(ue);
        return Response.status(Response.Status.CREATED)
                .location(URI.create("/UE?code=" + ue.getCode()))
                .build();
    }

    // 2) Récupération de la liste de toutes les unités d'enseignements - GET /UE
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUniteEnseignement(@QueryParam("semestre") Integer semestre,
                                            @QueryParam("code") Integer code) {
        // 6) Récupération par code spécifique
        if (code != null) {
            UniteEnseignement ue = ueBusiness.getUEByCode(code);
            if (ue == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(ue).build();
        }

        // 3) Récupération par semestre
        if (semestre != null) {
            List<UniteEnseignement> ues = ueBusiness.getUEBySemestre(semestre);
            return Response.ok(ues).build();
        }

        // Toutes les UE
        List<UniteEnseignement> ues = ueBusiness.getListeUE();
        return Response.ok(ues).build();
    }

    // 4) Suppression d'une unité d'enseignement - DELETE /UE/{code}
    @DELETE
    @Path("/{code}")
    public Response deleteUniteEnseignement(@PathParam("code") int code) {
        boolean deleted = ueBusiness.deleteUniteEnseignement(code);
        if (deleted) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // 5) Modification d'une unité d'enseignement - PUT /UE/{code}
    @PUT
    @Path("/{code}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response updateUniteEnseignement(@PathParam("code") int code, UniteEnseignement ue) {
        UniteEnseignement existing = ueBusiness.getUEByCode(code);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ue.setCode(code);
        boolean updated = ueBusiness.updateUniteEnseignement(code, ue);
        if (updated) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}