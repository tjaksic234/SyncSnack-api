package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Group;
import com.example.KavaSpring.models.dto.GroupDto;
import com.example.KavaSpring.models.dto.GroupRequest;
import com.example.KavaSpring.models.dto.GroupResponse;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.services.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final ConverterService converterService;

    @Override
    public GroupResponse createGroup(GroupRequest request) {
         Group group = new Group();
         group.setName(request.getName());
         group.setDescription(request.getDescription());
         //! This is kind of password generation is temporary and most likely this will be changed
         group.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
         groupRepository.save(group);

         log.info("Group created");
         return converterService.convertToGroupResponse(request);
    }

    @Override
    public GroupDto getGroupById(String id) {
        Group group = groupRepository.getById(id).orElseThrow(() -> new NotFoundException("No group associated with that id"));

        log.info("Get group by id finished");
        return converterService.convertToGroupDto(group);
    }

}
