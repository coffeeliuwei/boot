package com.coffee.controller;

import javax.sound.midi.Track;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.Data;
import lombok.ToString;

@Data
/**
 * 一锅端涵盖：
 * @ToString，
 * @EqualsAndHashCode，
 * @Getter 在所有领域，
 * @Setter 所有非final字段， 
 * @RequiredArgsConstructor
 **/
@ApiModel("用户实体")
public class User {
	// lombok会将final修饰属性作为构造参数
	@ApiModelProperty(value = "用户名",required =true )
	private final String name;
	@Setter(AccessLevel.PACKAGE)
	@ApiModelProperty(value = "年龄",example = "18")
	private int age;
	@ApiModelProperty("成绩")
	private double score;
	@ApiModelProperty(value = "标签",hidden = false,allowEmptyValue = true)
	private String[] tags;
	
	@ApiModel(value = "lombok泛型演示",parent = User.class)
	@ToString(includeFieldNames = true)
	@Data(staticConstructor = "of")
	// staticConstructor增加对泛型构造函数的支持
	public static class Exercise<T> {
		@ApiModelProperty(value = "用户名",required = true)
		private final String name;
		@ApiModelProperty(value = "泛型value",allowableValues = "Object")
		private final T value;
	}
}
