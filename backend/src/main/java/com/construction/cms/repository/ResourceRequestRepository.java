package com.construction.cms.repository;

import com.construction.cms.model.ResourceRequest;
import com.construction.cms.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRequestRepository extends JpaRepository<ResourceRequest, Long> {
    List<ResourceRequest> findByProjectId(Long projectId);
    List<ResourceRequest> findByStatus(RequestStatus status);
    List<ResourceRequest> findByProjectIdAndStatus(Long projectId, RequestStatus status);
    List<ResourceRequest> findByEngineerId(Long engineerId);
}
