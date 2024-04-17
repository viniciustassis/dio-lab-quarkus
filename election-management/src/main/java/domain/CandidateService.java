package domain;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CandidateService {
    private final CandidateRepository repository;
    
    public CandidateService(CandidateRepository repository) {
        this.repository = repository;
    }

    public void save(Candidate candidate) {
        repository.save(candidate);
    }

    public List<Candidate> findAll() {
        return repository.findAll();
    }

    public Candidate findById(String id) {
        return repository.findById(id).orElseThrow();
    }

    
}
