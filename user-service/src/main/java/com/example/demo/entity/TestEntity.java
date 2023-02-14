package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.demo.enumeration.StatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * <p>
 *
 * </p>
 *
 * @author chenx
 * @since 2020-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("pf_test")
@ApiModel(value = "TestEntity对象", description = "")
public class TestEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "生日")
    private LocalDate birthday;

    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "家庭住址")
    private String homeAddress;

    @ApiModelProperty(value = "照片")
    private String photo;

    @ApiModelProperty(value = "是否结婚(1 是 0 否)")
    private Boolean isMarry;

    @ApiModelProperty(value = "状态")
    private StatusEnum status;

}
