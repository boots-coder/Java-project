package com.bootscoder.shopping_admin_service;

import com.bootscoder.shopping_admin_service.mapper.AdminMapper;
import com.bootscoder.shopping_common.pojo.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ShoppingAdminServiceApplicationTests {

	@Autowired
	private AdminMapper adminMapper;

	@Test
	void contextLoads() {
		List<Permission> permission = adminMapper.findAllPermission("baizhan");
		System.out.println(permission);
	}

}
