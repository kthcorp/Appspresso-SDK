/*
 * Appspresso
 *
 * Copyright (c) 2011 KT Hitel Corp.
 *
 * Appspresso SDK may be freely distributed under the MIT license.
 */
package com.appspresso.api;

/**
 * JSON 직렬화 지원.
 * <p>
 * Boolean, Number, String, Map, Object[], null 등의 JSON으로 직렬화할 수 있는 객체를 제외한 값을 자바스크립트와 주고 받으려면 이
 * 인터페이스를 구현해야 함.
 * <p>
 * 
 * <pre>
 * class Employee implements AxPluginResult {
 * 		private String firstName;
 * 		private String lastName;
 * 		private String department;
 * 		private int age;
 * 		private enum { MALE, FEMALE, OTHER } gender;
 * 
 * 		...
 * 
 * 		public Object getPluginResult() {
 * 			// 객체를 JSON 직렬화할 수 있는 Map으로 변환
 * 			return new HashMap<String, Object>() {
 * 				{
 * 					put("fname", firstName);
 * 					put("lname", lastName);
 * 					put("dept", dept);
 * 					put("age", age);
 * 					put("gender", gender.ordinal());
 * 				}
 * 			};
 * 		}
 * }
 * </pre>
 * 
 * @version 1.0
 */
public interface AxPluginResult {

    /**
     * 자바 객체를 Boolean, Number, String, Map, Object[], null 등의 JSON으로 직렬화할 수 있는 객체로 변환.
     * 
     * @return JSON으로 직렬화 할 수 있는 객체.
     * @since 1.0
     */
    Object getPluginResult();

}
