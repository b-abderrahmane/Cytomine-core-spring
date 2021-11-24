package be.cytomine.repository.image;


import be.cytomine.domain.image.AbstractImage;
import be.cytomine.domain.image.UploadedFile;
import be.cytomine.domain.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the abstract image entity.
 */
@Repository
public interface AbstractImageRepository extends JpaRepository<AbstractImage, Long>, JpaSpecificationExecutor<AbstractImage> {

    @Override
    @EntityGraph(attributePaths = {"uploadedFile"})
    Page<AbstractImage> findAll(@Nullable Specification<AbstractImage> spec, Pageable pageable);


    List<AbstractImage> findAllByUploadedFile(UploadedFile uploadedFile);

    @Query(value = "SELECT DISTINCT ii.baseImage.id FROM ImageInstance ii WHERE ii.project = :project")
    Set<Long> findAllIdsByProject(Project project);


}