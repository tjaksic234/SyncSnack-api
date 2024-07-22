package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UnauthorizedException;
import com.example.KavaSpring.models.dto.GroupDto;
import com.example.KavaSpring.service.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/groups")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class GroupController {

    private final GroupService groupService;

    //TODO treba jos vidjeti hoce li se dodavati group password
    @PostMapping
    public ResponseEntity<GroupResponse> create(@RequestBody GroupRequest request) {

    }

    @GetMapping("{id}")
    public ResponseEntity<GroupDto> getUserById(@PathVariable("id") String id) {
        try {
            return ResponseEntity.ok(groupService.getGroupById(id));
        } catch (UnauthorizedException | NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
