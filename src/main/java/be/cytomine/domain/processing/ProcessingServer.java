package be.cytomine.domain.processing;

import be.cytomine.domain.CytomineDomain;
import be.cytomine.domain.middleware.AmqpQueue;
import be.cytomine.domain.ontology.Ontology;
import be.cytomine.domain.project.Project;
import be.cytomine.domain.security.User;
import be.cytomine.utils.JsonObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class ProcessingServer extends CytomineDomain {

    @NotNull
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull
    @NotBlank
    private String host = "localhost";

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    private Integer port = 22;

    private String type;

    @NotBlank
    private String processingMethodName;

    @ManyToOne
    private AmqpQueue amqpQueue;

    private String persistentDirectory;

    private String workingDirectory = "";

    private Integer index;


    public CytomineDomain buildDomainFromJson(JsonObject json, EntityManager entityManager) {
        ProcessingServer processingServer = (ProcessingServer)this;
        processingServer.id = json.getJSONAttrLong("id",null);
        processingServer.name = json.getJSONAttrStr("name", true);
        processingServer.host = json.getJSONAttrStr("host", true);
        processingServer.username = json.getJSONAttrStr("username", true);
        processingServer.port = json.getJSONAttrInteger("name", null);
        processingServer.type = json.getJSONAttrStr("type", false);

        processingServer.processingMethodName = json.getJSONAttrStr("processingMethodName", false);
        processingServer.amqpQueue = (AmqpQueue) json.getJSONAttrDomain(entityManager, "amqpQueue", new AmqpQueue(), false);
        processingServer.persistentDirectory = json.getJSONAttrStr("persistentDirectory", false);
        processingServer.workingDirectory = json.getJSONAttrStr("workingDirectory", false);

        processingServer.index = json.getJSONAttrInteger("index", 10);

        processingServer.created = json.getJSONAttrDate("created");
        processingServer.updated = json.getJSONAttrDate("updated");
        return processingServer;
    }

    public static JsonObject getDataFromDomain(CytomineDomain domain) {
        JsonObject returnArray = CytomineDomain.getDataFromDomain(domain);
        ProcessingServer processingServer = (ProcessingServer)domain;
        returnArray.put("name", processingServer.getName());
        returnArray.put("host", processingServer.getHost());
        returnArray.put("username", processingServer.getUsername());
        returnArray.put("port", processingServer.getPort());
        returnArray.put("type", processingServer.getType());

        returnArray.put("processingMethodName", processingServer.getProcessingMethodName());
        returnArray.put("amqpQueue", (processingServer.amqpQueue != null ? processingServer.amqpQueue.toJsonObject() : null));
        returnArray.put("persistentDirectory", processingServer.getPersistentDirectory());
        returnArray.put("workingDirectory", processingServer.getWorkingDirectory());

        returnArray.put("index", processingServer.getIndex());

        return returnArray;
    }

    @Override
    public String toJSON() {
        return toJsonObject().toJsonString();
    }

    @Override
    public JsonObject toJsonObject() {
        return getDataFromDomain(this);
    }

}