package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.domain.model.StoredData;
import sk.stuba.sdg.isbe.services.DataPointTagService;

import java.util.List;

@RestController
@RequestMapping("api/datapoint/datapointtag")
public class DataPointTagController {

    @Autowired
    private DataPointTagService dataPointTagService;

    @GetMapping
    public List<DataPointTag> getDataPointTags() {return dataPointTagService.getDataPointTags();}

    @Operation(summary = "Add new Data point tag into the system")
    @PostMapping("/create")
    public DataPointTag createDataPointTag(@Valid @RequestBody DataPointTag dataPointTag) {
        return dataPointTagService.createDataPointTag(dataPointTag);
    }

    @Operation(summary = "Get Data point tag by uid")
    @GetMapping("/getDataPointTagById/{dataPointTagId}")
    public DataPointTag getDataPointTagById(@PathVariable String dataPointTagId) {
        return dataPointTagService.getDataPointTagById(dataPointTagId);
    }

    @Operation(summary = "Get all stored data from data point")
    @GetMapping("/getStoredData/{dataPointTagId}")
    public List<StoredData> getStoredData(@PathVariable String dataPointTagId) {
        return dataPointTagService.getStoredData(dataPointTagId);
    }

    @Operation(summary = "Update Data point tag")
    @PostMapping("/updateDataPointTag/{dataPointTagId}")
    public DataPointTag updateDataPointTag(@PathVariable String dataPointTagId, @Valid @RequestBody DataPointTag changeDataPointTag) {
        return dataPointTagService.updateDataPointTag(dataPointTagId, changeDataPointTag);
    }

    @Operation(summary = "Delete Data point tag")
    @DeleteMapping("deleteDataPointTag/{dataPointTagId}")
    public DataPointTag deleteDataPointTag(@PathVariable String dataPointTagId) {
        return dataPointTagService.deleteDataPointTag(dataPointTagId);
    }
}
