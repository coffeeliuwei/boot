package com.coffee.controller;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import com.coffee.annotation.Phone;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
@ApiModel("用户表")
public class User {
    private Integer id;
    @NotEmpty(message="用户名不能为空")
    @Pattern(regexp ="([\\w]|[-_]){4,16}$",message = "请规范填写用户名")
    private String username;
    @NotEmpty(message="密码不能为空")
    @Length(min=6,message="密码长度不能小于6位")
    private String password;
    @Email(message="请输入正确的邮箱")
    private String email;
    @Phone
    private String phone;
}