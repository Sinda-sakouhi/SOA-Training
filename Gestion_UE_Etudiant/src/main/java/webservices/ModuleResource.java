package webservices;

import entities.Module;
import entities.UniteEnseignement;
import metiers.ModuleBusiness;
import metiers.UniteEnseignementBusiness;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/modules")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ModuleResource {

    private ModuleBusiness moduleBusiness = new ModuleBusiness();
    private UniteEnseignementBusiness ueBusiness = new UniteEnseignementBusiness();

    // 1) Création d'un nouveau Module - POST /modules
    @POST
    public Response createModule(Module module) {
        if (module.getUniteEnseignement() == null ||
                moduleBusiness.getModuleByMatricule(module.getMatricule()) != null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Vérifier que l'UE existe
        UniteEnseignement ue = ueBusiness.getUEByCode(module.getUniteEnseignement().getCode());
        if (ue == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        module.setUniteEnseignement(ue);
        boolean added = moduleBusiness.addModule(module);
        if (added) {
            return Response.status(Response.Status.CREATED)
                    .location(URI.create("/modules/" + module.getMatricule()))
                    .entity(module)
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // 2) Récupération de la liste de tous les modules - GET /modules
    // 6) Récupérer les modules par UE - GET /modules/UE?codeUE=1
    @GET
    public Response getAllModules(@QueryParam("codeUE") Integer codeUE) {
        // Récupération par UE
        if (codeUE != null) {
            UniteEnseignement ue = ueBusiness.getUEByCode(codeUE);
            if (ue == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            List<Module> modules = moduleBusiness.getModulesByUE(ue);
            return Response.ok(modules).build();
        }

        // Tous les modules
        List<Module> modules = moduleBusiness.getAllModules();
        return Response.ok(modules).build();
    }

    // 3) Récupération d'un module par matricule - GET /modules/{matricule}
    @GET
    @Path("/{matricule}")
    public Response getModuleByMatricule(@PathParam("matricule") String matricule) {
        Module module = moduleBusiness.getModuleByMatricule(matricule);
        if (module == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(module).build();
    }

    // 4) Suppression d'un module - DELETE /modules/{matricule}
    @DELETE
    @Path("/{matricule}")
    public Response deleteModule(@PathParam("matricule") String matricule) {
        boolean deleted = moduleBusiness.deleteModule(matricule);
        if (deleted) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    // 5) Modification d'un module - PUT /modules/{matricule}
    @PUT
    @Path("/{matricule}")
    public Response updateModule(@PathParam("matricule") String matricule, Module module) {
        Module existing = moduleBusiness.getModuleByMatricule(matricule);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // Vérifier que l'UE existe si elle est fournie
        if (module.getUniteEnseignement() != null) {
            UniteEnseignement ue = ueBusiness.getUEByCode(module.getUniteEnseignement().getCode());
            if (ue == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            module.setUniteEnseignement(ue);
        } else {
            module.setUniteEnseignement(existing.getUniteEnseignement());
        }

        module.setMatricule(matricule);
        boolean updated = moduleBusiness.updateModule(matricule, module);
        if (updated) {
            return Response.ok(module).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}