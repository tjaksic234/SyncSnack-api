package com.example.KavaSpring.security.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    private String userId;
    private String userProfileId;
    private String groupId;
}
