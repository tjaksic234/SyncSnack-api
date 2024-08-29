package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.models.dao.GroupMembership;
import com.example.KavaSpring.models.dto.GroupMembershipCreateRequest;
import com.example.KavaSpring.repository.GroupMembershipRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("api/memberships")
@Slf4j
@ShowAPI
public class GroupMembershipController {

    private final GroupMembershipRepository groupMembershipRepository;

    @PostMapping("create")
    public ResponseEntity<?> createGroupMembership(@RequestBody GroupMembershipCreateRequest request) {
        try {
            GroupMembership groupMembership = new GroupMembership();
            groupMembership.setUserProfileId(request.getUserProfileId());
            groupMembership.setGroupId(request.getGroupId());
            return ResponseEntity.ok(groupMembershipRepository.save(groupMembership));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
