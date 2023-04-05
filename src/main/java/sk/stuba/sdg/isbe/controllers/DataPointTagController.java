package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.DataPointSave;
import sk.stuba.sdg.isbe.services.DataPointSaveService;

import java.util.List;

@RestController
@RequestMapping("api/datapoint/datapointtag")
public class DataPointTagController {

    @Autowired
    private DataPointSaveService dataPointSaveService;

    @GetMapping
    public List<DataPointSave> getDataPointSaves() {return dataPointSaveService.getDataPointSaves();}

    @Operation(summary = "Add new Data point Save into the system")
    @PostMapping("/create")
    public DataPointSave createDataPointSave(@Valid @RequestBody DataPointSave dataPointSave) {
        return dataPointSaveService.createDataPointSave(dataPointSave);
    }

    @Operation(summary = "Get Data point Save by uid")
    @GetMapping("/getDataPointSaveById/{dataPointSaveId}")
    public DataPointSave getDataPointSaveById(@PathVariable String dataPointSaveId) {
        return dataPointSaveService.getDataPointSaveById(dataPointSaveId);
    }

    @Operation(summary = "Update Data point Save")
    @PostMapping("/updateDataPointSave/{dataPointSaveId}")
    public DataPointSave updateDataPointSave(@PathVariable String dataPointSaveId, @Valid @RequestBody DataPointSave changeDataPointSave) {
        return dataPointSaveService.updateDataPointSave(dataPointSaveId, changeDataPointSave);
    }

    @Operation(summary = "Delete Data point Save")
    @DeleteMapping("deleteDataPointSave/{dataPointSaveId}")
    public DataPointSave deleteDataPointSave(@PathVariable String dataPointSaveId) {
        return dataPointSaveService.deleteDataPointSave(dataPointSaveId);
    }
}
