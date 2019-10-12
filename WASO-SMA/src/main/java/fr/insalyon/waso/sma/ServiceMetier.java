package fr.insalyon.waso.sma;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.insalyon.waso.util.JsonHttpClient;
import fr.insalyon.waso.util.JsonServletHelper;
import fr.insalyon.waso.util.exception.ServiceException;
import fr.insalyon.waso.util.exception.ServiceIOException;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author WASO Team
 */
public class ServiceMetier {

    protected final String somClientUrl;
    protected final String somPersonneUrl;
    protected final String somContactUrl;
    protected final String somStructureUrl;
    protected final String somProduitUrl;
    protected final String somContratUrl;
    protected final JsonObject container;

    protected JsonHttpClient jsonHttpClient;

    public ServiceMetier(String somClientUrl, String somPersonneUrl, String somContactUrl, String somStructureUrl, String somProduitUrl, String somContratUrl, JsonObject container) {
        this.somClientUrl = somClientUrl;
        this.somPersonneUrl = somPersonneUrl;
        this.somContactUrl = somContactUrl;
        this.somStructureUrl = somStructureUrl;
        this.somProduitUrl = somProduitUrl;
        this.somContratUrl = somContratUrl;
        this.container = container;

        this.jsonHttpClient = new JsonHttpClient();
    }

    public void release() {
        try {
            this.jsonHttpClient.close();
        } catch (IOException ex) {
            // Ignorer
        }
    }

    public void getListeClient() throws ServiceException {
        try {

            // 1. Obtenir la liste des Clients
            
            JsonObject clientContainer = null;
            try {
                clientContainer = this.jsonHttpClient.post(
                        this.somClientUrl,
                        new JsonHttpClient.Parameter("SOM", "getListeClient")
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somClientUrl, "Client", "getListeClient", ex);
            }

            JsonArray jsonOutputClientListe = clientContainer.getAsJsonArray("clients");


            // 2. Obtenir la liste des Personnes
            
            JsonObject personneContainer = null;
            try {
                personneContainer = this.jsonHttpClient.post(
                        this.somPersonneUrl,
                        new JsonHttpClient.Parameter("SOM", "getListePersonne")
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somPersonneUrl, "Personne", "getListePersonne", ex);
            }


            // 3. Indexer la liste des Personnes
            
            HashMap<Integer, JsonObject> personnes = new HashMap<Integer, JsonObject>();

            for (JsonElement p : personneContainer.getAsJsonArray("personnes")) {

                JsonObject personne = p.getAsJsonObject();

                personnes.put(personne.get("id").getAsInt(), personne);
            }


            // 4. Construire la liste des Personnes pour chaque Client (directement dans le JSON)

            for (JsonElement clientJsonElement : jsonOutputClientListe.getAsJsonArray()) {

                JsonObject client = clientJsonElement.getAsJsonObject();

                JsonArray personnesID = client.get("personnes-ID").getAsJsonArray();

                JsonArray outputPersonnes = new JsonArray();

                for (JsonElement personneID : personnesID) {
                    JsonObject personne = personnes.get(personneID.getAsInt());
                    outputPersonnes.add(personne);
                }

                client.add("personnes", outputPersonnes);

            }


            // 5. Ajouter la liste de Clients au conteneur JSON

            this.container.add("clients", jsonOutputClientListe);

        } catch (Exception ex) {
            throw JsonServletHelper.ServiceMetierExecutionException("getListeClient", ex);
        }
    }

    public void rechercherClientParDenomination(String denomination, String ville) throws ServiceException {
        try {

            // 1. Obtenir la liste des Clients
            
            JsonObject clientContainer = null;
            try {
                clientContainer = this.jsonHttpClient.post(
                        this.somClientUrl,
                        new JsonHttpClient.Parameter("SOM", "rechercherClientParDenomination"),
                        new JsonHttpClient.Parameter("denomination", denomination),
                        new JsonHttpClient.Parameter("ville", ville)
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somClientUrl, "Client", "rechercherClientParDenomination", ex);
            }

            JsonArray jsonOutputClientListe = clientContainer.getAsJsonArray("clients");


            // 2. Obtenir la liste des Personnes
            
            JsonObject personneContainer = null;
            try {
                personneContainer = this.jsonHttpClient.post(
                        this.somPersonneUrl,
                        new JsonHttpClient.Parameter("SOM", "getListePersonne")
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somPersonneUrl, "Personne", "getListePersonne", ex);
            }


            // 3. Indexer la liste des Personnes
            
            HashMap<Integer, JsonObject> personnes = new HashMap<Integer, JsonObject>();

            for (JsonElement p : personneContainer.getAsJsonArray("personnes")) {

                JsonObject personne = p.getAsJsonObject();

                personnes.put(personne.get("id").getAsInt(), personne);
            }


            // 4. Construire la liste des Personnes pour chaque Client (directement dans le JSON)

            for (JsonElement clientJsonElement : jsonOutputClientListe.getAsJsonArray()) {

                JsonObject client = clientJsonElement.getAsJsonObject();

                JsonArray personnesID = client.get("personnes-ID").getAsJsonArray();

                JsonArray outputPersonnes = new JsonArray();

                for (JsonElement personneID : personnesID) {
                    JsonObject personne = personnes.get(personneID.getAsInt());
                    outputPersonnes.add(personne);
                }

                client.add("personnes", outputPersonnes);

            }


            // 5. Ajouter la liste de Clients au conteneur JSON

            this.container.add("clients", jsonOutputClientListe);

        } catch (Exception ex) {
            throw JsonServletHelper.ServiceMetierExecutionException("rechercherClientParNomPersonne", ex);
        }
    }
    
    public void rechercherClientParNomPersonne(String nom, String ville) throws ServiceException {
        try {

            // 1. Obtenir la liste des Personnes
            
            JsonObject personneContainer = null;
            try {
                personneContainer = this.jsonHttpClient.post(
                        this.somPersonneUrl,
                        new JsonHttpClient.Parameter("SOM", "rechercherPersonneParNom"),
                        new JsonHttpClient.Parameter("nom-personne", nom)
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somPersonneUrl, "Personne", "rechercherPersonneParNom", ex);
            }
            
            String listeIdPersonnes = "";
            
            for (JsonElement p : personneContainer.getAsJsonArray("personnes")) {
                JsonObject personne = p.getAsJsonObject();
                String personneId = personne.get("id").getAsString();
                System.out.println(personneId);
                if(listeIdPersonnes.length() == 0){
                    listeIdPersonnes = personneId;
                    continue;
                }
                listeIdPersonnes += ("," + p.getAsString());
            }
            
            // 2. Obtenir la liste des Clients
            
            JsonObject clientContainer = null;
            try {
                clientContainer = this.jsonHttpClient.post(
                        this.somClientUrl,
                        new JsonHttpClient.Parameter("SOM", "rechercherClientParPersonne"),
                        new JsonHttpClient.Parameter("personneIds", listeIdPersonnes),
                        new JsonHttpClient.Parameter("ville", ville)
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somClientUrl, "Client", "rechercherClientParPersonne", ex);
            }

            JsonArray jsonOutputClientListe = clientContainer.getAsJsonArray("clients");
            
            // 3. Obtenir la liste des Personnes des Clients
            
            try {
                personneContainer = this.jsonHttpClient.post(
                        this.somPersonneUrl,
                        new JsonHttpClient.Parameter("SOM", "getListePersonne")
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somPersonneUrl, "Personne", "getListePersonne", ex);
            }

            // 4. Indexer la liste des Personnes
            
            HashMap<Integer, JsonObject> personnes = new HashMap<Integer, JsonObject>();

            for (JsonElement p : personneContainer.getAsJsonArray("personnes")) {

                JsonObject personne = p.getAsJsonObject();

                personnes.put(personne.get("id").getAsInt(), personne);
            }


            // 5. Construire la liste des Personnes pour chaque Client (directement dans le JSON)

            for (JsonElement clientJsonElement : jsonOutputClientListe.getAsJsonArray()) {

                JsonObject client = clientJsonElement.getAsJsonObject();

                JsonArray personnesID = client.get("personnes-ID").getAsJsonArray();

                JsonArray outputPersonnes = new JsonArray();

                for (JsonElement personneID : personnesID) {
                    JsonObject personne = personnes.get(personneID.getAsInt());
                    outputPersonnes.add(personne);
                }

                client.add("personnes", outputPersonnes);

            }


            // 6. Ajouter la liste de Clients au conteneur JSON

            this.container.add("clients", jsonOutputClientListe);

        } catch (Exception ex) {
            throw JsonServletHelper.ServiceMetierExecutionException("rechercherClientParDenomination", ex);
        }
    }
    
    public void rechercherClientParNumero(Integer numero) throws ServiceException {
        try {

            // 1. Obtenir la liste des Clients
            
            JsonObject clientContainer = null;
            try {
                clientContainer = this.jsonHttpClient.post(
                        this.somClientUrl,
                        new JsonHttpClient.Parameter("SOM", "rechercherClientParNumero"),
                        new JsonHttpClient.Parameter("numero", Integer.toString(numero))
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somClientUrl, "Client", "rechercherClientParNumero", ex);
            }

            JsonArray jsonOutputClientListe = clientContainer.getAsJsonArray("clients");


            // 2. Obtenir la liste des Personnes
            
            JsonObject personneContainer = null;
            try {
                personneContainer = this.jsonHttpClient.post(
                        this.somPersonneUrl,
                        new JsonHttpClient.Parameter("SOM", "getListePersonne")
                );
            }
            catch (ServiceIOException ex) {
                throw JsonServletHelper.ServiceObjectMetierCallException(this.somPersonneUrl, "Personne", "getListePersonne", ex);
            }


            // 3. Indexer la liste des Personnes
            
            HashMap<Integer, JsonObject> personnes = new HashMap<Integer, JsonObject>();

            for (JsonElement p : personneContainer.getAsJsonArray("personnes")) {

                JsonObject personne = p.getAsJsonObject();

                personnes.put(personne.get("id").getAsInt(), personne);
            }


            // 4. Construire la liste des Personnes pour chaque Client (directement dans le JSON)

            for (JsonElement clientJsonElement : jsonOutputClientListe.getAsJsonArray()) {

                JsonObject client = clientJsonElement.getAsJsonObject();

                JsonArray personnesID = client.get("personnes-ID").getAsJsonArray();

                JsonArray outputPersonnes = new JsonArray();

                for (JsonElement personneID : personnesID) {
                    JsonObject personne = personnes.get(personneID.getAsInt());
                    outputPersonnes.add(personne);
                }

                client.add("personnes", outputPersonnes);

            }


            // 5. Ajouter la liste de Clients au conteneur JSON

            this.container.add("clients", jsonOutputClientListe);

        } catch (Exception ex) {
            throw JsonServletHelper.ServiceMetierExecutionException("rechercherClientParNumero", ex);
        }
    }
}
