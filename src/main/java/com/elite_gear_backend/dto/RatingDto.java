package com.elite_gear_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
    private Long ratingId;
    private String userName;
    private Long userId;
    private Long rate;
    private String comment;
    private String createdTime;
}
