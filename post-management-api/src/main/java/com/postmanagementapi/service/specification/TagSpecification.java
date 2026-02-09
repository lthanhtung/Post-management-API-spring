package com.postmanagementapi.service.specification;

import com.postmanagementapi.model.Tag;
import com.postmanagementapi.model.dto.request.TagFilterRequestDTO;
import org.springframework.data.jpa.domain.Specification;

public class TagSpecification {
    public static Specification<Tag> hasName(TagFilterRequestDTO tagFilter){
        return (root, query, criteriaBuilder) ->{
            if (tagFilter.getName() == null) return criteriaBuilder.conjunction();

            return criteriaBuilder.equal(root.get("name"),tagFilter.getName());
        };
    }

    public static Specification<Tag> hasNameLike(TagFilterRequestDTO tagFilter){
        return (root, query, criteriaBuilder) ->{
            if (tagFilter.getName() == null) return criteriaBuilder.conjunction();

            return criteriaBuilder.like(root.get("name"),"%" + tagFilter.getName() + "%");
        };
    }
}
