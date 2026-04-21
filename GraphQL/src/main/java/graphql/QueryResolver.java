package graphql;

import business.ModuleBusiness;
import business.UniteEnseignementBusiness;
import com.coxautodev.graphql.tools.GraphQLRootResolver;
import entities.Module;
import entities.UniteEnseignement;

public class MutationResolver implements GraphQLRootResolver {

    private UniteEnseignementBusiness ueBusiness;
    private ModuleBusiness moduleBusiness;

    public MutationResolver() {
        this.ueBusiness = new UniteEnseignementBusiness();
        this.moduleBusiness = new ModuleBusiness();
    }

    // ============ MUTATIONS pour UniteEnseignement ============

    // 1. Ajouter une UE
    public boolean addUniteEnseignement(int code, String domaine, String responsable, int credits, int semestre) {
        // Vérifier si l'UE existe déjà
        if (ueBusiness.getUEByCode(code) != null) {
            return false;
        }
        UniteEnseignement ue = new UniteEnseignement(code, domaine, responsable, credits, semestre);
        return ueBusiness.addUniteEnseignement(ue);
    }

    // 2. Modifier une UE
    public boolean updateUniteEnseignement(int code, String domaine, String responsable, int credits, int semestre) {
        UniteEnseignement existingUE = ueBusiness.getUEByCode(code);
        if (existingUE == null) {
            return false;
        }
        UniteEnseignement updatedUE = new UniteEnseignement(code, domaine, responsable, credits, semestre);
        return ueBusiness.updateUniteEnseignement(code, updatedUE);
    }

    // 3. Supprimer une UE
    public boolean deleteUniteEnseignement(int code) {
        return ueBusiness.deleteUniteEnseignement(code);
    }

    // ============ MUTATIONS pour Module ============

    // 4. Ajouter un module
    public boolean addModule(String matricule, String nom, int coefficient, int volumeHoraire,
                             String type, int codeUE) {
        // Vérifier si le module existe déjà
        if (moduleBusiness.getModuleByMatricule(matricule) != null) {
            return false;
        }

        // Vérifier si l'UE existe
        UniteEnseignement ue = ueBusiness.getUEByCode(codeUE);
        if (ue == null) {
            return false;
        }

        Module.TypeModule moduleType = Module.TypeModule.valueOf(type);
        Module module = new Module(matricule, nom, coefficient, volumeHoraire, moduleType, ue);
        return moduleBusiness.addModule(module);
    }

    // 5. Modifier un module
    public boolean updateModule(String matricule, String nom, int coefficient, int volumeHoraire,
                                String type, int codeUE) {
        // Vérifier si le module existe
        Module existingModule = moduleBusiness.getModuleByMatricule(matricule);
        if (existingModule == null) {
            return false;
        }

        // Vérifier si l'UE existe
        UniteEnseignement ue = ueBusiness.getUEByCode(codeUE);
        if (ue == null) {
            return false;
        }

        Module.TypeModule moduleType = Module.TypeModule.valueOf(type);
        Module updatedModule = new Module(matricule, nom, coefficient, volumeHoraire, moduleType, ue);
        return moduleBusiness.updateModule(matricule, updatedModule);
    }

    // 6. Supprimer un module
    public boolean deleteModule(String matricule) {
        return moduleBusiness.deleteModule(matricule);
    }
}