package com.yuhtin.lauren.core.vote;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */

@Data
@Builder
public class VoteResponse implements Serializable {

    private final long user;
    private final long guild;

}
