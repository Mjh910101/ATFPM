package com.atfpm.main.dynamic;

import com.atfpm.box.DynamicObj;

public class DynamicObjBox {

	private static DynamicObj dynamicObj;

	public static void putDynamicObj(DynamicObj obj) {
		if (dynamicObj != null) {
			dynamicObj = null;
		}
		dynamicObj = obj;
	}

	public static DynamicObj getDynamicObj() {
		return dynamicObj;
	}

}
