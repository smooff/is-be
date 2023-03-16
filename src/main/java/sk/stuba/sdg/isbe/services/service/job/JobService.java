package sk.stuba.sdg.isbe.services.service.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.services.repository.job.JobRepository;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;
}
