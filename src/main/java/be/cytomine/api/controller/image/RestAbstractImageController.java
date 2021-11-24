package be.cytomine.api.controller.image;

import be.cytomine.api.controller.RestCytomineController;
import be.cytomine.domain.image.AbstractImage;
import be.cytomine.domain.project.Project;
import be.cytomine.domain.security.SecUser;
import be.cytomine.exceptions.ObjectNotFoundException;
import be.cytomine.service.dto.CropParameter;
import be.cytomine.service.dto.ImageParameter;
import be.cytomine.service.dto.LabelParameter;
import be.cytomine.service.dto.WindowParameter;
import be.cytomine.service.image.AbstractImageService;
import be.cytomine.service.image.SliceCoordinatesService;
import be.cytomine.service.middleware.ImageServerService;
import be.cytomine.service.project.ProjectService;
import be.cytomine.utils.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class RestAbstractImageController extends RestCytomineController {

    private final AbstractImageService abstractImageService;

    private final ProjectService projectService;

    private final ImageServerService imageServerService;

    private final SliceCoordinatesService sliceCoordinatesService;

    @GetMapping("/abstractimage.json")
    public ResponseEntity<String> list(
            @RequestParam(value = "project", required = false) Long idProject
    ) {
        log.debug("REST request to list abstract image");
        Project project = idProject == null ? null : projectService.find(idProject).orElseThrow(() -> new ObjectNotFoundException("Project", idProject));
        return responseSuccess(abstractImageService.list(project, retrieveSearchParameters(), retrievePageable()));
    }
//
//    @GetMapping("/abstractimage/{id}.json")
//    public ResponseEntity<String> show(
//           @PathVariable Long id
//    ) {
//        log.debug("REST request to get abstract image {}", id);
//        return abstractImageService.find(id)
//                .map(this::responseSuccess)
//                .orElseGet(() -> responseNotFound("AbstractImage", id));
//    }
//
//    @GetMapping("/uploadedfile/{id}/abstractimage.json")
//    public ResponseEntity<String> getByUploadedFile(
//            @PathVariable Long id
//    ) {
//        log.debug("REST request to get abstract image {}", id);
//        return abstractImageService.findByUploadedFile(id)
//                .map(this::responseSuccess)
//                .orElseGet(() -> responseNotFound("AbstractImage", id));
//    }
//
//
//    @PostMapping("/abstractimage.json")
//    public ResponseEntity<String> add(@RequestBody JsonObject json) {
//        log.debug("REST request to save abstractimage : " + json);
//        return add(abstractImageService, json);
//    }
//
//    @PutMapping("/abstractimage/{id}.json")
//    public ResponseEntity<String> edit(@PathVariable String id, @RequestBody JsonObject json) {
//        log.debug("REST request to edit abstractimage : " + id);
//        return update(abstractImageService, json);
//    }
//
//    @DeleteMapping("/abstractimage/{id}.json")
//    public ResponseEntity<String> delete(@PathVariable String id) {
//        log.debug("REST request to delete abstractimage : " + id);
//        return delete(abstractImageService, JsonObject.of("id", id), null);
//    }
//
//    @GetMapping("/abstractimage/unused.json")
//    public ResponseEntity<String> listUnused() {
//        log.debug("REST request to list unused abstractimages");
//        return responseSuccess(abstractImageService.listUnused());
//    }
//
//    @GetMapping("/abstractimage/{id}/user.json")
//    public ResponseEntity<String> showUploaderOfImage(@PathVariable Long id) {
//        log.debug("REST request to show image uploader");
//        SecUser user = abstractImageService.getImageUploader(id);
//        if (user !=null) {
//            return responseSuccess(abstractImageService.getImageUploader(id));
//        } else {
//            return responseNotFound("AbstractImage", "User", id);
//        }
//
//    }
//
//    // TODO: GET params vs POST params!
//    @RequestMapping(value = "/abstractimage/{id}/thumb.{format}", method = {RequestMethod.GET, RequestMethod.POST})
//    public void thumb(
//            @PathVariable Long id,
//            @PathVariable String format,
//            @RequestParam(defaultValue = "false", required = false) Boolean refresh,
//            @RequestParam(defaultValue = "512", required = false) Integer maxSize,
//            @RequestParam(defaultValue = "", required = false) String colormap,
//            @RequestParam(defaultValue = "false", required = false) Boolean inverse,
//            @RequestParam(defaultValue = "0", required = false) Double contrast,
//            @RequestParam(defaultValue = "0", required = false) Double gamma,
//            @RequestParam(defaultValue = "0", required = false) String bits
//
//    ) {
//        log.debug("REST request get abstractimage {} thumb {}", id, format);
//        ImageParameter thumbParameter = new ImageParameter();
//        thumbParameter.setFormat(format);
//        thumbParameter.setMaxSize(maxSize);
//        thumbParameter.setColormap(colormap);
//        thumbParameter.setInverse(inverse);
//        thumbParameter.setContrast(contrast);
//        thumbParameter.setGamma(gamma);
//        thumbParameter.setMaxBits(bits.equals("max"));
//        thumbParameter.setBits(!bits.equals("max") ? Integer.parseInt(bits): 0);
//        thumbParameter.setRefresh(refresh);
//
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        responseByteArray(imageServerService.thumb(sliceCoordinatesService.getReferenceSlice(abstractImage), thumbParameter), format
//        );
//    }
//
//
//    @RequestMapping(value = "/abstractimage/{id}/preview.{format}", method = {RequestMethod.GET, RequestMethod.POST})
//    public void preview(
//            @PathVariable Long id,
//            @PathVariable String format,
//            @RequestParam(defaultValue = "1024", required = false) Integer maxSize,
//            @RequestParam(defaultValue = "", required = false) String colormap,
//            @RequestParam(defaultValue = "false", required = false) Boolean inverse,
//            @RequestParam(defaultValue = "0", required = false) Double contrast,
//            @RequestParam(defaultValue = "0", required = false) Double gamma,
//            @RequestParam(defaultValue = "0", required = false) String bits
//
//    ) {
//        log.debug("REST request get abstractimage {} preview {}", id, format);
//        ImageParameter previewParameter = new ImageParameter();
//        previewParameter.setFormat(format);
//        previewParameter.setMaxSize(maxSize);
//        previewParameter.setColormap(colormap);
//        previewParameter.setInverse(inverse);
//        previewParameter.setContrast(contrast);
//        previewParameter.setGamma(gamma);
//        previewParameter.setMaxBits(bits.equals("max"));
//        previewParameter.setBits(!bits.equals("max") ? Integer.parseInt(bits): 0);
//
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        responseByteArray(imageServerService.thumb(sliceCoordinatesService.getReferenceSlice(abstractImage), previewParameter), format
//        );
//    }
//
//
//    @GetMapping("/abstractimage/{id}/associated.json")
//    public ResponseEntity<String> associated(@PathVariable Long id) {
//        log.debug("REST request to get available associated images");
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        return responseSuccess(imageServerService.associated(abstractImage));
//    }
//
//    @RequestMapping(value = "/abstractimage/{id}/associated/{label}.{format})", method = {RequestMethod.GET, RequestMethod.POST})
//    public ResponseEntity<String> label(
//            @PathVariable Long id,
//            @PathVariable String label,
//            @PathVariable String format,
//            @RequestParam(defaultValue = "256") Integer maxSize) {
//        log.debug("REST request to get associated image of a abstract image");
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        LabelParameter labelParameter = new LabelParameter();
//        labelParameter.setFormat(format);
//        labelParameter.setLabel(label);
//        labelParameter.setMaxSize(maxSize);
//        return responseByteArray(imageServerService.label(abstractImage), format);
//    }
//
//    @RequestMapping(value = "/abstractimage/{id}/crop.{format}", method = {RequestMethod.GET, RequestMethod.POST})
//    public ResponseEntity<String> associated(
//            @PathVariable Long id,
//            @PathVariable String format,
//            @RequestParam(defaultValue = "256") Integer maxSize,
//            @RequestParam(required = false) String geometry,
//            @RequestParam(required = false) String location,
//            @RequestParam(required = false) String boundaries,
//            @RequestParam(defaultValue = "false") Boolean complete,
//            @RequestParam(required = false) Integer zoom,
//            @RequestParam(required = false) Double increaseArea,
//            @RequestParam(required = false) Boolean safe,
//            @RequestParam(required = false) Boolean square,
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) Boolean draw,
//            @RequestParam(required = false) Boolean mask,
//            @RequestParam(required = false) Boolean alphaMask,
//            @RequestParam(required = false) Boolean drawScaleBar,
//            @RequestParam(required = false) Double resolution,
//            @RequestParam(required = false) Double magnification,
//            @RequestParam(required = false) String colormap,
//            @RequestParam(required = false) Boolean inverse,
//            @RequestParam(required = false) Double contrast,
//            @RequestParam(required = false) Double gamma,
//            @RequestParam(required = false) String bits,
//            @RequestParam(required = false) Integer alpha,
//            @RequestParam(required = false) Integer thickness,
//            @RequestParam(required = false) String color,
//            @RequestParam(required = false) Integer jpegQuality
//    ) {
//        log.debug("REST request to get associated image of a abstract image");
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//
//        CropParameter cropParameter = new CropParameter();
//        cropParameter.setGeometry(geometry);
//        cropParameter.setLocation(location);
//        cropParameter.setBoundaries(boundaries);
//        cropParameter.setComplete(complete);
//        cropParameter.setZoom(zoom);
//        cropParameter.setIncreaseArea(increaseArea);
//        cropParameter.setSafe(safe);
//        cropParameter.setSquare(square);
//        cropParameter.setType(type);
//        cropParameter.setDraw(draw);
//        cropParameter.setMask(mask);
//        cropParameter.setAlphaMask(alphaMask);
//        cropParameter.setDrawScaleBar(drawScaleBar);
//        cropParameter.setResolution(resolution);
//        cropParameter.setMagnification(magnification);
//        cropParameter.setColormap(colormap);
//        cropParameter.setInverse(inverse);
//        cropParameter.setGamma(gamma);
//        cropParameter.setAlpha(alpha);
//        cropParameter.setContrast(contrast);
//        cropParameter.setThickness(thickness);
//        cropParameter.setColor(color);
//        cropParameter.setJpegQuality(jpegQuality);
//        cropParameter.setMaxBits(bits.equals("max"));
//        cropParameter.setBits(!bits.equals("max") ? Integer.parseInt(bits): 0);
//
//
//        return responseByteArray(imageServerService.crop(sliceCoordinatesService.getSliceCoordinates(abstractImage), cropParameter), format);
//    }
//
//    @RequestMapping(value = "/abstractimage/{id}/camera_url-{x}-{y}-{w}-{h}.{format}", method = {RequestMethod.GET, RequestMethod.POST})
//    public void windowUrl(
//            @PathVariable Long id,
//            @PathVariable String format,
//            @PathVariable Integer x,
//            @PathVariable Integer y,
//            @PathVariable Integer w,
//            @PathVariable Integer h,
//            @RequestParam(defaultValue = "false", required = false) Boolean withExterior
//    ) {
//        log.debug("REST request get abstractimage {} camera url {}", id, format);
//        WindowParameter windowParameter = new WindowParameter();
//        windowParameter.setX(x);
//        windowParameter.setY(y);
//        windowParameter.setW(w);
//        windowParameter.setH(h);
//        windowParameter.setWithExterior(false);
//
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        String url = imageServerService.cameraUrl(sliceCoordinatesService.getReferenceSlice(abstractImage), windowParameter);
//        responseSuccess(JsonObject.of("url", url));
//    }
//
//    @RequestMapping(value = "/abstractimage/{id}/camera-{x}-{y}-{w}-{h}.{format}", method = {RequestMethod.GET, RequestMethod.POST})
//    public void windowUrl(
//            @PathVariable Long id,
//            @PathVariable String format,
//            @PathVariable Integer x,
//            @PathVariable Integer y,
//            @PathVariable Integer w,
//            @PathVariable Integer h
//    ) {
//        log.debug("REST request get abstractimage {} camera {}", id, format);
//        WindowParameter windowParameter = new WindowParameter();
//        windowParameter.setX(x);
//        windowParameter.setY(y);
//        windowParameter.setW(w);
//        windowParameter.setH(h);
//        windowParameter.setWithExterior(false);
//
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        return responseByteArray(imageServerService.camera(sliceCoordinatesService.getSliceCoordinates(abstractImage), windowParameter), format);
//    }
//
//
//
//    @GetMapping("/abstractimage/{id}/download.json")
//    public RedirectView associated(@PathVariable Long id) {
//        log.debug("REST request to get available associated images");
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        String url = imageServerService.downloadUri(abstractImage);
//        return new RedirectView(url);
//    }
//
//    @GetMapping("/abstractimage/{id}/associated.json")
//    public ResponseEntity<String> associated(@PathVariable Long id) {
//        log.debug("REST request to get available associated images");
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        return responseSuccess(imageServerService.associated(abstractImage));
//    }
//
//
//    @PostMapping("/abstractimage/{id}/properties/clear.json")
//    public ResponseEntity<String> clearProperties(@PathVariable Long id) {
//        log.debug("REST request to get available associated images");
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        imagePropertiesService.clear(abstractImage);
//        return responseSuccess(new JsonObject());
//    }
//
//    @PostMapping("/abstractimage/{id}/properties/populate.json")
//    public ResponseEntity<String> populateProperties(@PathVariable Long id) {
//        log.debug("REST request to get available associated images");
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        imagePropertiesService.populate(abstractImage);
//        return responseSuccess(new JsonObject());
//    }
//
//
//    @PostMapping("/abstractimage/{id}/properties/extract.json")
//    public ResponseEntity<String> populateProperties(@PathVariable Long id) {
//        log.debug("REST request to get available associated images");
//        AbstractImage abstractImage = abstractImageService.find(id)
//                .orElseThrow(() -> new ObjectNotFoundException("AbstractImage", id));
//        imagePropertiesService.extractUseful(abstractImage);
//        return responseSuccess(new JsonObject());
//    }
//
//    // TODO: imageserver by abstract image is deprecated in server
//    @GetMapping("/abstractimage/{id}/imageServers.json")
//    public ResponseEntity<String> imageServers(@PathVariable Long id) {
//        log.debug("REST request to list abstractimage {id} imageservers");
//        throw new RuntimeException("DEPRECATED?");
////        return responseSuccess(abstractImageService.imageServers());
//    }


//    /**
//     * Get all image servers URL for an image
//     */
//    @RestApiMethod(description="Get all image servers URL for an image")
//    @RestApiParams(params=[
//            @RestApiParam(name="id", type="long", paramType = RestApiParamType.PATH,description = "The image id"),
//            ])
//    @RestApiResponseObject(objectIdentifier = "URL list")
//    @Deprecated
//    def imageServers() {
//        try {
//            def id = params.long('id')
//            responseSuccess(abstractImageService.imageServers(id))
//        } catch (CytomineException e) {
//            log.error(e)
//            response([success: false, errors: e.msg], e.code)
//        }
//    }
//

}