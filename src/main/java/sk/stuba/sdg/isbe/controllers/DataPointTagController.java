package sk.stuba.sdg.isbe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.stuba.sdg.isbe.services.DataPointTagService;

@RestController
@RequestMapping("api/datapoint/datapointtag")
public class DataPointTagController {

    @Autowired
    private DataPointTagService dataPointTagService;
}
