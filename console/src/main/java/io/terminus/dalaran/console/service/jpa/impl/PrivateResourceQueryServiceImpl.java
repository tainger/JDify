package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.console.service.jpa.PrivateResourceQueryService;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.repository.PrivateRepositoryRepository;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrivateResourceQueryServiceImpl implements PrivateResourceQueryService {

    @Autowired
    private PrivateRepositoryRepository privateRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<PrivateRepositoryEntity> query(PrivateRepositoryQuery query) {

        Specification<PrivateRepositoryEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNoneBlank(query.getName())) {
                Predicate name = criteriaBuilder.equal(root.get("name"), query.getName());
                predicates.add(name);
            }

            if (StringUtils.isNoneBlank(query.getVersion())) {
                Predicate version = criteriaBuilder.equal(root.get("version"), query.getVersion());
                predicates.add(version);
            }

            if (StringUtils.isNoneBlank(query.getOrigin())) {
                Predicate origin = criteriaBuilder.equal(root.get("origin"), query.getOrigin());
                predicates.add(origin);
            }

            if (StringUtils.isNoneBlank(query.getTenantCode())) {
                Predicate tenantCode = criteriaBuilder.equal(root.get("tenantCode"), query.getTenantCode());
                predicates.add(tenantCode);
            }

            if (StringUtils.isNoneBlank(query.getType())) {
                Predicate type = criteriaBuilder.equal(root.get("type"), query.getType());
                predicates.add(type);
            }

            Predicate isExist = criteriaBuilder.equal(root.get("isExist"),true);
            predicates.add(isExist);

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return privateRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public Page<PrivateRepositoryEntity> paging(PrivateRepositoryQuery query, Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PrivateRepositoryEntity> criteriaQuery = builder.createQuery(PrivateRepositoryEntity.class);

        Root<PrivateRepositoryEntity> root = criteriaQuery.from(PrivateRepositoryEntity.class);

        CriteriaQuery<PrivateRepositoryEntity> select = criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();

        Predicate exist = builder.equal(root.get("isExist"), true);
        predicates.add(exist);
//        criteriaQuery.where(builder.equal(root.get("isExist"), true));

        if (StringUtils.isNoneBlank(query.getName())) {
            Predicate name = builder.equal(root.get("name"), query.getName());
            predicates.add(name);
//            criteriaQuery.where(builder.equal(root.get("name"), query.getName()));
        }

        if (StringUtils.isNoneBlank(query.getVersion())) {
            Predicate version = builder.equal(root.get("version"), query.getVersion());
            predicates.add(version);
//            criteriaQuery.where(builder.equal(root.get("version"), query.getVersion()));
        }

        if (StringUtils.isNoneBlank(query.getOrigin())) {
            Predicate origin = builder.equal(root.get("origin"), query.getOrigin());
            predicates.add(origin);
//            criteriaQuery.where(builder.equal(root.get("origin"), query.getOrigin()));
        }

        if (StringUtils.isNoneBlank(query.getTenantCode())) {
            Predicate tenant = builder.equal(root.get("tenantCode"), query.getTenantCode());
            predicates.add(tenant);
//            criteriaQuery.where(builder.equal(root.get("tenantCode"), query.getTenantCode()));
        }

        if (StringUtils.isNoneBlank(query.getType())) {
            Predicate type = builder.equal(root.get("type"), query.getType());
            predicates.add(type);
//            criteriaQuery.where(builder.equal(root.get("type"), query.getType()));
        }

        if (StringUtils.isNoneBlank(query.getId())) {
            Predicate resourceKey = builder.equal(root.get("resourceKey"), query.getId());
            predicates.add(resourceKey);
//            criteriaQuery.where(builder.equal(root.get("resourceKey"), query.getId()));
        }

        Predicate[] predicatesArray = {};
        criteriaQuery.where(predicates.toArray(predicatesArray));

        criteriaQuery.groupBy(root.get("resourceKey"));

        long totalSize = entityManager.createQuery(criteriaQuery).getResultList().size();

        TypedQuery<PrivateRepositoryEntity> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult((pageNumber - 1) * pageSize);
        typedQuery.setMaxResults(pageSize);

        List result = typedQuery.getResultList();

        return new PageImpl<>(result, pageable, totalSize);
    }

    @Override
    public List<PrivateRepositoryEntity> findByResourceKeyAndVersion(String resourceKey, String version) {
        Specification<PrivateRepositoryEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNoneBlank(resourceKey)) {
                Predicate id = criteriaBuilder.equal(root.get("resourceKey"), resourceKey);
                predicates.add(id);
            }

            if (StringUtils.isNoneBlank(version)) {
                Predicate id = criteriaBuilder.equal(root.get("version"), version);
                predicates.add(id);
            }

            Predicate isExist = criteriaBuilder.equal(root.get("isExist"),true);
            predicates.add(isExist);

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return privateRepository.findAll(specification);
    }

    @Override
    public List<PrivateRepositoryEntity> listPackageResource() {
        Specification<PrivateRepositoryEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            Predicate processor = criteriaBuilder.equal(root.get("type"), DalaranConstants.PROCESSOR);

            Predicate trigger = criteriaBuilder.equal(root.get("type"), DalaranConstants.TRIGGER);

            Predicate predicate
                    = criteriaBuilder.or(processor, trigger);
            predicates.add(predicate);

            Predicate isExist = criteriaBuilder.equal(root.get("isExist"),true);
            predicates.add(isExist);

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return privateRepository.findAll(specification);
    }

    @Override
    public List<String> listResourceVersion(String id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = builder.createQuery(String.class);
        Root<PrivateRepositoryEntity> root = criteriaQuery.from(PrivateRepositoryEntity.class);
        criteriaQuery.multiselect(root.get("version"))
                .where(builder.equal(root.get("resourceKey"), id), builder.equal(root.get("isExist"), true));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
