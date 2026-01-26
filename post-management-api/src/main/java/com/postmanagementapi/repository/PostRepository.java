package com.postmanagementapi.repository;

import com.postmanagementapi.model.Post;
import com.postmanagementapi.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByTagsContains(Tag tag);
}
