package infrastructure.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;

import javax.transaction.Transactional;


import domain.Candidate;
import domain.CandidateQuery;
import domain.CandidateRepository;

@ApplicationScoped
public class SQLCandidateRepository implements CandidateRepository {
    private final EntityManager entityManager;

    public SQLCandidateRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(List<Candidate> candidates) {
        candidates.stream()
            .map(candidate -> infrastructure.repositories.entities.Candidate.fromDomain(candidate))
            //.forEach(entityManager::merge)
            .forEach(entity -> {
                entityManager.merge(entity);
            });
            ;
    }

    @Override
    public List<Candidate> find(CandidateQuery query) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(infrastructure.repositories.entities.Candidate.class);
        var root = cq.from(infrastructure.repositories.entities.Candidate.class);

        //var where = query.ids().map(id -> cb.in(root.get("id")).value(id)).get();

        //cq.select(root).where(where);
        cq.select(root).where(conditions(query, cb, root));

        return entityManager.createQuery(cq)
                .getResultStream()
                .map(infrastructure.repositories.entities.Candidate::toDomain)
                .toList()
        ;
    }

    private Predicate[] conditions(CandidateQuery query, CriteriaBuilder cb,              Root<infrastructure.repositories.entities.Candidate> root) {

        return Stream.of(
            query.ids().map(id -> cb.equal(root.get("id"), id)),
            query.name().map(name -> cb.or(
                    cb.like(cb.lower(root.get("familyName")), name.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("givenName")), name.toLowerCase() + "%"))))
            .flatMap(Optional::stream)
            .toArray(Predicate[]::new);
    }
    
}
