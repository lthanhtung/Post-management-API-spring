package com.postmanagementapi.service.specification;


import com.postmanagementapi.model.User;
import com.postmanagementapi.model.dto.request.UserFilterRequestDTO;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasName(UserFilterRequestDTO userFilter){
       return (root, query, criteriaBuilder) ->{
           if (userFilter.getName() == null) return criteriaBuilder.conjunction();

           return criteriaBuilder.equal(root.get("userName"),userFilter.getName());
       };
    }

    public static Specification<User> hasEmail(UserFilterRequestDTO userFilter){
        return (root, query, criteriaBuilder) ->{
            if (userFilter.getEmail() == null) return criteriaBuilder.conjunction();

            return criteriaBuilder.like(root.get("email"),"%" + userFilter.getEmail() + "%");
        };
    }
}
