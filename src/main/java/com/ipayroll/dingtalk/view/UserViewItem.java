package com.ipayroll.dingtalk.view;

import lombok.Data;

import java.io.Serializable;

/**
 * @version 1.0.0
 * @author: lujiakang
 * @date: 2019/6/3
 */
@Data
public class UserViewItem implements Serializable {

    private String userId;

    private String userName;

}
