package be.cytomine.repository.ontology;

import be.cytomine.domain.ontology.*;
import be.cytomine.domain.project.Project;
import be.cytomine.domain.security.SecUser;
import be.cytomine.domain.security.User;
import be.cytomine.domain.security.UserJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AlgoAnnotationTermRepository extends JpaRepository<AlgoAnnotationTerm, Long>, JpaSpecificationExecutor<AlgoAnnotationTerm>  {
    default List<AlgoAnnotationTerm> findAllByAnnotation(AnnotationDomain annotation) {
        return findAllByAnnotationIdent(annotation.getId());
    }
    List<AlgoAnnotationTerm> findAllByAnnotationIdent(Long id);

    long countByProject(Project project);

    Optional<AlgoAnnotationTerm> findByAnnotationIdentAndTermAndUserJob(Long annotationId, Term term, UserJob userJob);

    Optional<AlgoAnnotationTerm> findByAnnotationIdentAndTerm(Long annotationId, Term term);

}
