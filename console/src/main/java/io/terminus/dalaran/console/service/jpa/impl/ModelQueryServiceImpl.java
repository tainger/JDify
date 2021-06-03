package io.terminus.dalaran.console.service.jpa.impl;

import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.repository.ModelRepository;
import io.terminus.dalaran.console.service.jpa.ModelQueryService;
import io.terminus.dalaran.console.service.jpa.model.QueryModelInfo;
import io.terminus.dalaran.model.ModelTargetType;
import io.terminus.dalaran.model.dto.basic.BasicModelInfo;
import io.terminus.dalaran.model.query.ModelQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Service
public class ModelQueryServiceImpl implements ModelQueryService {

    @Autowired
    private ModelRepository modelQueryRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<ModelEntity> query(ModelQuery query) {

        Specification<ModelEntity> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getModuleId() != null) {
                Predicate moduleId = criteriaBuilder.equal(root.get("moduleId"), query.getModuleId());
                predicates.add(moduleId);
            }

            if (CollectionUtils.isNotEmpty(query.getModelIds())) {
                Predicate modelIds = criteriaBuilder.and(root.get("resourceKey").in(query.getModelIds()));
                predicates.add(modelIds);
            }

            if (StringUtils.isNoneBlank(query.getName())) {
                Predicate name = criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%");
                predicates.add(name);
            }
            Predicate isExist = criteriaBuilder.equal(root.get("isExist"),true);
            predicates.add(isExist);

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return modelQueryRepository.findAll(specification);
    }

    @Override
    public List<BasicModelInfo> listBasicInfoByModuleId(String moduleId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<QueryModelInfo> criteriaQuery = builder.createQuery(QueryModelInfo.class);
        Root<ModelEntity> root = criteriaQuery.from(ModelEntity.class);
        criteriaQuery.multiselect(root.get("resourceKey"), root.get("moduleId"), root.get("name"), root.get("type"), root.get("isExist"))
                .where(builder.equal(root.get("moduleId"), moduleId), builder.equal(root.get("isExist"),true));
        List<QueryModelInfo> models = entityManager.createQuery(criteriaQuery).getResultList();
        List<BasicModelInfo> basicModels = new ArrayList<>();
        models.forEach(model -> {
            BasicModelInfo basicModel = new BasicModelInfo();
            try {
                BeanUtils.copyProperties(basicModel, model);
                basicModel.setId(model.getResourceKey());
                basicModels.add(basicModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return basicModels;
    }
}
