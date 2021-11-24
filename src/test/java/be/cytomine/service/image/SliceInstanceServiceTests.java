package be.cytomine.service.image;

import be.cytomine.BasicInstanceBuilder;
import be.cytomine.CytomineCoreApplication;
import be.cytomine.domain.image.AbstractImage;
import be.cytomine.domain.image.ImageInstance;
import be.cytomine.domain.image.SliceInstance;
import be.cytomine.domain.image.UploadedFile;
import be.cytomine.domain.project.Project;
import be.cytomine.exceptions.ObjectNotFoundException;
import be.cytomine.exceptions.WrongArgumentException;
import be.cytomine.repository.image.UploadedFileRepository;
import be.cytomine.service.CommandService;
import be.cytomine.service.command.TransactionService;
import be.cytomine.utils.CommandResponse;
import be.cytomine.utils.JsonObject;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@SpringBootTest(classes = CytomineCoreApplication.class)
@AutoConfigureMockMvc
@WithMockUser(username = "superadmin")
@Transactional
public class SliceInstanceServiceTests {

    @Autowired
    SliceInstanceService sliceInstanceService;

    @Autowired
    UploadedFileRepository uploadedFileRepository;

    @Autowired
    BasicInstanceBuilder builder;

    @Autowired
    CommandService commandService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ImageInstanceService imageInstanceService;

    @Test
    void list_all_slice_by_image_instance() {
        ImageInstance image1 = builder.given_an_image_instance();
        SliceInstance sliceInstance1 = builder.given_a_slice_instance(image1, 10, 100, 1000);
        SliceInstance sliceInstance2 = builder.given_a_slice_instance(image1, 1, 200, 500);
        SliceInstance sliceInstance3 = builder.given_a_slice_instance(image1, 5, 150, 750);
        ImageInstance image2 = builder.given_an_image_instance();
        SliceInstance sliceInstance4 = builder.given_a_slice_instance(image2, 5, 150, 750);

        assertThat(sliceInstanceService.list(image1)).containsExactly(sliceInstance2, sliceInstance3, sliceInstance1);
        assertThat(sliceInstanceService.list(image1)).doesNotContain(sliceInstance4);
    }

    @Test
    void find_slice_instance_by_coordinates() {
        ImageInstance image1 = builder.given_an_image_instance();
        SliceInstance sliceInstance1 = builder.given_a_slice_instance(image1, 10, 100, 1000);

        assertThat(sliceInstanceService.find(image1, 10, 100, 1000)).isPresent();
    }


    @Test
    void find_slice_instance_unexisting_coordinates_return_empty_response() {
        ImageInstance image1 = builder.given_an_image_instance();
        SliceInstance sliceInstance1 = builder.given_a_slice_instance(image1, 10, 100, 1000);

        assertThat(sliceInstanceService.find(image1, 0, 0, 0)).isEmpty();
    }

    @Test
    void get_unexisting_slice_instance_return_null() {
        assertThat(sliceInstanceService.get(0L)).isNull();
    }

    @Test
    void find_slice_instance_with_success() {
        SliceInstance sliceInstance = builder.given_a_slice_instance();
        assertThat(sliceInstanceService.find(sliceInstance.getId()).isPresent());
        assertThat(sliceInstance).isEqualTo(sliceInstanceService.find(sliceInstance.getId()).get());
    }

    @Test
    void find_unexisting_sliceInstance_return_empty() {
        assertThat(sliceInstanceService.find(0L)).isEmpty();
    }

    @Test
    void add_valid_slicte_instance_with_success() {
        SliceInstance sliceInstance = builder.given_a_not_persisted_slice_instance();

        CommandResponse commandResponse = sliceInstanceService.add(sliceInstance.toJsonObject());

        assertThat(commandResponse).isNotNull();
        assertThat(commandResponse.getStatus()).isEqualTo(200);
        AssertionsForClassTypes.assertThat(sliceInstanceService.find(commandResponse.getObject().getId())).isPresent();
        SliceInstance created = sliceInstanceService.find(commandResponse.getObject().getId()).get();
    }


    @Test
    void add_already_existing_slice_instance() {
        SliceInstance sliceInstance = builder.given_a_slice_instance();
        Assertions.assertThrows(WrongArgumentException.class, () -> {
            sliceInstanceService.add(sliceInstance.toJsonObject().withChange("image", null));
        });
    }

    @Test
    void add_slice_instance_with_null_image_fails() {
        SliceInstance sliceInstance = builder.given_a_not_persisted_slice_instance();
        Assertions.assertThrows(WrongArgumentException.class, () -> {
            sliceInstanceService.add(sliceInstance.toJsonObject().withChange("image", null));
        });
    }

    @Test
    void add_slice_instance_with_null_project_fails() {
        SliceInstance sliceInstance = builder.given_a_not_persisted_slice_instance();
        Assertions.assertThrows(ObjectNotFoundException.class, () -> {
            sliceInstanceService.add(sliceInstance.toJsonObject().withChange("project", null));
        });
    }

    @Test
    void edit_slicte_instance_with_success() {
        Project project1 = builder.given_a_project();
        Project project2 = builder.given_a_project();
        SliceInstance sliceInstance = builder.given_a_not_persisted_slice_instance();
        sliceInstance.setProject(project1);
        sliceInstance = builder.persistAndReturn(sliceInstance);

        JsonObject jsonObject = sliceInstance.toJsonObject();
        jsonObject.put("project", project2.getId());

        CommandResponse commandResponse = sliceInstanceService.edit(jsonObject, true);
        assertThat(commandResponse).isNotNull();
        assertThat(commandResponse.getStatus()).isEqualTo(200);
        AssertionsForClassTypes.assertThat(sliceInstanceService.find(commandResponse.getObject().getId())).isPresent();
        SliceInstance updated = sliceInstanceService.find(commandResponse.getObject().getId()).get();

        assertThat(updated.getProject()).isEqualTo(project2);
    }

    @Test
    void delete_slicte_instance_with_success() {
        SliceInstance sliceInstance = builder.given_a_slice_instance();

        CommandResponse commandResponse = sliceInstanceService.delete(sliceInstance, null, null, true);

        assertThat(commandResponse).isNotNull();
        assertThat(commandResponse.getStatus()).isEqualTo(200);
        AssertionsForClassTypes.assertThat(sliceInstanceService.find(sliceInstance.getId()).isEmpty());
    }

    @Test
    void delete_slice_instance_with_dependencies_with_success() {
        fail("not yet implemented");
    }



}