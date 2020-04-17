package com.coffee;

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
public class User {
	// lombok会将final修饰属性作为构造参数
	private final String name;
	@Setter(AccessLevel.PACKAGE)
	private int age;
	private double score;
	private String[] tags;

	@ToString(includeFieldNames = true)
	@Data(staticConstructor = "of")
	// staticConstructor增加对泛型构造函数的支持
	public static class Exercise<T> {
		private final String name;
		private final T value;
	}
}
