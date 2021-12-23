package be.cytomine.service.utils;

import be.cytomine.BasicInstanceBuilder;
import be.cytomine.CytomineCoreApplication;
import be.cytomine.TestUtils;
import be.cytomine.domain.ontology.UserAnnotation;
import be.cytomine.dto.SimplifiedAnnotation;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.transaction.Transactional;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CytomineCoreApplication.class)
@AutoConfigureMockMvc
@WithMockUser(authorities = "ROLE_SUPER_ADMIN", username = "superadmin")
@Transactional
public class SimplifyGeometryServiceTests {

    @Autowired
    BasicInstanceBuilder builder;

    @Autowired
    SimplifyGeometryService simplifyGeometryService;

    @Test
    public void simplify_big_annotation() throws ParseException {

        //create annotation
        UserAnnotation annotation = builder.given_a_user_annotation();

        //add very big geometry
        annotation.setLocation(new WKTReader().read(TestUtils.getResourceFileAsString("dataset/big_annotation.txt")));

        builder.persistAndReturn(annotation);

        assertThat(annotation.getLocation().getNumPoints()).isGreaterThanOrEqualTo(500);

        long maxPoint;
        long minPoint;

        //simplify
        maxPoint = 150;
        minPoint = 100;

        SimplifiedAnnotation result = simplifyGeometryService.simplifyPolygon(annotation.getLocation(), minPoint, maxPoint);

        assertThat(result.getNewAnnotation().getNumPoints()).isLessThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), maxPoint));
        assertThat(result.getNewAnnotation().getNumPoints()).isGreaterThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), minPoint));

        maxPoint = 1000;
        minPoint = 400;

        result = simplifyGeometryService.simplifyPolygon(annotation.getLocation(), minPoint, maxPoint);

        assertThat(result.getNewAnnotation().getNumPoints()).isLessThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), maxPoint));
        assertThat(result.getNewAnnotation().getNumPoints()).isGreaterThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), minPoint));

        maxPoint = 1000;
        minPoint = 400;

        result = simplifyGeometryService.simplifyPolygon(annotation.getLocation(), minPoint, maxPoint);

        assertThat(result.getNewAnnotation().getNumPoints()).isLessThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), maxPoint));
        assertThat(result.getNewAnnotation().getNumPoints()).isGreaterThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), minPoint));
    }

    @Test
    public void simplify_very_big_annotation() throws ParseException {

        //create annotation
        UserAnnotation annotation = builder.given_a_user_annotation();

        //add very big geometry
        annotation.setLocation(new WKTReader().read(TestUtils.getResourceFileAsString("dataset/very_big_annotation.txt")));

        builder.persistAndReturn(annotation);

        assertThat(annotation.getLocation().getNumPoints()).isGreaterThanOrEqualTo(500);

        long maxPoint;
        long minPoint;

        //simplify
        maxPoint = 50;
        minPoint = 10;

        SimplifiedAnnotation result = simplifyGeometryService.simplifyPolygon(annotation.getLocation(), minPoint, maxPoint);

        assertThat(result.getNewAnnotation().getNumPoints()).isLessThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), maxPoint));
        assertThat(result.getNewAnnotation().getNumPoints()).isGreaterThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), minPoint));
    }

    @Test
    public void simplify_annotation_with_empty_space() throws ParseException {

        //create annotation
        UserAnnotation annotation = builder.given_a_user_annotation();

        //add very big geometry
        annotation.setLocation(new WKTReader().read(TestUtils.getResourceFileAsString("dataset/annotationbig_emptyspace.txt")));

        builder.persistAndReturn(annotation);

        assertThat(annotation.getLocation().getNumPoints()).isGreaterThanOrEqualTo(500);

        long maxPoint;
        long minPoint;

        //simplify
        maxPoint = 5000*10;
        minPoint = 1000;

        SimplifiedAnnotation result = simplifyGeometryService.simplifyPolygon(annotation.getLocation(), minPoint, maxPoint);

        assertThat(result.getNewAnnotation().getNumPoints()).isLessThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), maxPoint));
        assertThat(result.getNewAnnotation().getNumPoints()).isGreaterThanOrEqualTo((int)getPointMultiplyByGeometriesOrInteriorRings(annotation.getLocation(), minPoint));
    }

    @Test
    public void simplify_annotation_with_rate() throws ParseException {

        String expected = "POLYGON ((120 120, 140 199, 160 200, 180 199, 220 120, 122 122, 120 120))";
        Double geometryCompression = 10.0;

        String location = "POLYGON ((120 120, 121 121, 122 122, 220 120, 180 199, 160 200, 140 199, 120 120))";

        SimplifiedAnnotation simplifiedAnnotation = simplifyGeometryService.simplifyPolygon(location, geometryCompression);

        assertThat(simplifiedAnnotation.getNewAnnotation().toText()).isEqualTo(expected);

    }


    private long getPointMultiplyByGeometriesOrInteriorRings(Geometry geometry, long numberOfPoints){
        long result = 0;
        if (geometry instanceof MultiPolygon) {
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                Geometry geom = geometry.getGeometryN(i);
                int nbInteriorRing = 1;
                if(geom instanceof Polygon)
                    nbInteriorRing = ((Polygon)geom).getNumInteriorRing();
                result +=  geom.getNumGeometries() * nbInteriorRing;
            }
        } else {
            int nbInteriorRing = 1;
            if(geometry instanceof Polygon)
                nbInteriorRing = ((Polygon)geometry).getNumInteriorRing();
            result = geometry.getNumGeometries() * nbInteriorRing;
        }
        result = Math.max(1, result);

        if (result > 10) result/= 2;
        result = Math.min(10, result);

        result*=numberOfPoints;
        return result;
    }
}