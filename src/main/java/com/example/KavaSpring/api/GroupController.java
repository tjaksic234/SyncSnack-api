package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.GroupAlreadyExistsException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.SortCondition;
import com.example.KavaSpring.services.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/groups")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class GroupController {

    private final GroupService groupService;

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
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("leaderboard")
    public ResponseEntity<List<GroupMemberResponse>> getLeaderboard(
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
        }
    }

    @PatchMapping("edit")
    public ResponseEntity<String> editGroupInfo(@RequestHeader(value = "groupId") String groupId, @RequestBody GroupEditRequest request) {
        try {
            log.info("Editing group info");
            groupService.editGroupInfo(groupId, request);
            return ResponseEntity.ok("Success");
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("top-scorer")
    public ResponseEntity<GroupMemberResponse> getTopScorer(@RequestHeader(value = "groupId") String groupId) {
        try {
            log.info("Fetching top scorer in the group");
            return ResponseEntity.ok(groupService.getTopScorer(groupId));
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
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
}
