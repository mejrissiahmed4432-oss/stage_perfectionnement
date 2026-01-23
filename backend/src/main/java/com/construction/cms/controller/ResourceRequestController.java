package com.construction.cms.controller;

import com.construction.cms.model.ResourceRequest;
import com.construction.cms.model.RequestStatus;
import com.construction.cms.service.ResourceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.construction.cms.security.services.UserDetailsImpl;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/requests")
public class ResourceRequestController {

    @Autowired
    private ResourceRequestService requestService;

    @PostMapping
    @PreAuthorize("hasRole('ENGINEER')")
    public ResourceRequest createRequest(@RequestBody ResourceRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return requestService.createRequest(request, userDetails.getId());
    }

    @GetMapping
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('ADMIN')")
    public List<ResourceRequest> getAllRequests() {
        return requestService.getAllRequests();
    }
    
    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasRole('DIRECTOR') or hasRole('ENGINEER')")
    public List<ResourceRequest> getRequestsByProject(@PathVariable Long projectId) {
        return requestService.getRequestsByProject(projectId);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam RequestStatus status) {
        try {
            return ResponseEntity.ok(requestService.updateStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
