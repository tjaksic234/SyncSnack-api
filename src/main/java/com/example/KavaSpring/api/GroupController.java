package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.*;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.Role;
import com.example.KavaSpring.models.enums.SortCondition;
import com.example.KavaSpring.security.services.AuthService;
import com.example.KavaSpring.services.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/groups")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class GroupController {

    private final GroupService groupService;

    private final AuthService authService;

    @PostMapping("create")
    public ResponseEntity<GroupResponse> createGroup(@RequestBody GroupRequest request) {
        try {
            log.info("Create a group requested");
            return ResponseEntity.ok(groupService.createGroup(request));
        } catch (GroupAlreadyExistsException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable("id") String id) {
        try {
            log.info("Fetching group by id");
            return ResponseEntity.ok(groupService.getGroupById(id));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("join")
    public ResponseEntity<GroupResponse> joinGroup(@RequestBody GroupRequest request) {
        try {
            log.info("Join group started");
            return ResponseEntity.ok(groupService.joinGroup(request));
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("leaderboard")
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard(
            @RequestHeader(value = "groupId") String groupId,
            @RequestParam SortCondition sortCondition,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        try {
            log.info("Fetching leaderboard for group");
            return ResponseEntity.ok(groupService.getLeaderboard(groupId, sortCondition, PageRequest.of(page, size)));
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("count")
    public ResponseEntity<List<GroupOrderCountDto>> countGroupOrders(@RequestHeader(value = "groupId") String groupId) {
        try {
            log.info("Fetching group order count");
            return ResponseEntity.ok(groupService.countGroupOrders(groupId));
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping(value = "edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GroupEditResponse> editGroupInfo(
            @RequestHeader(value = "groupId") String groupId,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if (!authService.hasRole(groupId, Role.ADMIN, Role.PRESIDENT)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            log.info("Editing group info");
            return ResponseEntity.ok(groupService.editGroupInfo(groupId, name, description, file));
        } catch (NotFoundException | NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("top-scorer")
    public ResponseEntity<LeaderboardResponse> getTopScorer(@RequestHeader(value = "groupId") String groupId) {
        try {
            log.info("Fetching top scorer in the group");
            return ResponseEntity.ok(groupService.getTopScorer(groupId));
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("me")
    public ResponseEntity<List<GroupDto>> getProfileGroups() {
        try {
            log.info("Fetching user profile groups");
            return ResponseEntity.ok(groupService.getProfileGroups());
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("members")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(
            @RequestHeader(value = "groupId") String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        try {
            log.info("Fetching all members of the group");
            return ResponseEntity.ok(groupService.getGroupMembers(groupId, PageRequest.of(page, size)));
        } catch (NoGroupFoundException | NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("kick")
    public ResponseEntity<Void> kickUserFromGroup(
            @RequestHeader(value = "groupId") String groupId,
            @RequestParam String userProfileId
    ) {
        if (!authService.hasRole(groupId, Role.ADMIN, Role.PRESIDENT)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            log.info("Kicking user from group");
            groupService.kickUserFromGroup(groupId, userProfileId);
            return ResponseEntity.ok().build();
        } catch (NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("promote")
    public ResponseEntity<Void> promoteUser(
            @RequestHeader(value = "groupId") String groupId,
            @RequestParam String userProfileId,
            @RequestParam Role role
    ) {
        if (!authService.hasRole(groupId, Role.PRESIDENT)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        try {
            log.info("Promoting user");
            groupService.promoteUser(groupId, userProfileId, role);
            return ResponseEntity.ok().build();
        } catch (NoGroupFoundException | NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("sendInvitation")
    public ResponseEntity<String> generateInvitation(@RequestHeader String groupId, @RequestParam String invitedBy) {
        try {
            log.info("Generating group invitation");
            return ResponseEntity.ok(groupService.generateInvitation(groupId, invitedBy));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("joinViaInvitation/{code}")
    public ResponseEntity<GroupDto> joinViaInvitation(@PathVariable String code) {
        try {
            log.info("Joining group through link");
            return ResponseEntity.ok(groupService.joinViaInvitation(code));
        } catch (NotFoundException | NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (ExpiredInvitationException | AlreadyMemberException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
