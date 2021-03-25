package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.service.jpa.PrivateResourceQueryService;
import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.core.resource.repository.PrivateRepositoryRepository;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
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

            if (StringUtils.isNoneBlank(query.getOrigin())) {
                Predicate type = criteriaBuilder.equal(root.get("origin"), query.getOrigin());
                predicates.add(type);
            }

            if (StringUtils.isNoneBlank(query.getTenantCode())) {
                Predicate type = criteriaBuilder.equal(root.get("tenantCode"), query.getTenantCode());
                predicates.add(type);
            }

            if (StringUtils.isNoneBlank(query.getType())) {
                Predicate type = criteriaBuilder.equal(root.get("type"), query.getType());
                predicates.add(type);
            }

            if (StringUtils.isNoneBlank(query.getId())) {
                Predicate id = criteriaBuilder.equal(root.get("resourceKey"), query.getId());
                predicates.add(id);
            }

            Predicate isExist = criteriaBuilder.equal(root.get("isExist"),true);
            predicates.add(isExist);

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return privateRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"));
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
