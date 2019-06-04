package com.ipayroll.dingtalk.data.entity;

import lombok.Data;

/**
 * <p>
 * <b>ResponseEntity</b> is
 * </p>
 *
 * @author Kazyle
 * @version 1.0.0
 * @since 2017/8/14
 */
@Data
public class ResponseEntity {

    private long code;
    private Object error;
    private String msg;

    public ResponseEntity() {
        this(ResponseCode.SUCCESS);
    }
    public ResponseEntity(long code) {
        super();
        this.code = code;
    }

    public ResponseEntity(long code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public ResponseEntity(String msg) {
        this(ResponseCode.SUCCESS, msg);
    }

    public ResponseEntity(long code, Object error, String msg) {
        super();
        this.code = code;
        this.error = error;
        this.msg = msg;
    }
}
