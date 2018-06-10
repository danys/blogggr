package com.blogggr.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Sunnen on 11.12.16.
 */
@Getter
@Setter
public class FriendData {

    @NotNull
    private Long userId1;

    @NotNull
    private Long userId2;

    @NotNull
    private Integer action;
}
