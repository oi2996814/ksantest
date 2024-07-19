/*
* Copyright (c) 2021 PSPACE, inc. KSAN Development Team ksan@pspace.co.kr
* KSAN is a suite of free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version
* 3 of the License. See LICENSE for details
*
* 본 프로그램 및 관련 소스코드, 문서 등 모든 자료는 있는 그대로 제공이 됩니다.
* KSAN 프로젝트의 개발자 및 개발사는 이 프로그램을 사용한 결과에 따른 어떠한 책임도 지지 않습니다.
* KSAN 개발팀은 사전 공지, 허락, 동의 없이 KSAN 개발에 관련된 모든 결과물에 대한 LICENSE 방식을 변경 할 권리가 있습니다.
*/
package org.example.s3tests;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class Inventory {
	org.example.test.Inventory test = new org.example.test.Inventory();
	org.example.testV2.Inventory testV2 = new org.example.testV2.Inventory();

	@AfterEach
	public void clear(TestInfo testInfo) {
		test.clear(testInfo);
		testV2.clear(testInfo);
	}

	@Test
	@Tag("List")
	// 버킷에 인벤토리를 설정하지 않은 상태에서 조회가 가능한지 확인
	void testListBucketInventory() {
		test.testListBucketInventory();
		testV2.testListBucketInventory();
	}

	@Test
	@Tag("Put")
	// 버킷에 인벤토리를 설정할 수 있는지 확인
	void testPutBucketInventory() {
		test.testPutBucketInventory();
		testV2.testPutBucketInventory();
	}

	@Test
	@Tag("Check")
	// 버킷에 인벤토리 설정이 되었는지 확인
	void testCheckBucketInventory() {
		test.testCheckBucketInventory();
		testV2.testCheckBucketInventory();
	}

	@Test
	@Tag("Get")
	// 버킷에 설정된 인벤토리를 조회할 수 있는지 확인
	void testGetBucketInventory() {
		test.testGetBucketInventory();
		testV2.testGetBucketInventory();
	}

	@Test
	@Tag("Delete")
	// 버킷에 설정된 인벤토리를 삭제할 수 있는지 확인
	void testDeleteBucketInventory() {
		test.testDeleteBucketInventory();
		testV2.testDeleteBucketInventory();
	}

	@Test
	@Tag("Error")
	// 존재하지 않은 인벤토리를 가져오려고 할 경우 실패하는지 확인
	void testGetBucketInventoryNotExist() {
		test.testGetBucketInventoryNotExist();
		testV2.testGetBucketInventoryNotExist();
	}

	@Test
	@Tag("Error")
	// 존재하지 않은 인벤토리를 삭제하려고 할 경우 실패하는지 확인
	void testDeleteBucketInventoryNotExist() {
		test.testDeleteBucketInventoryNotExist();
		testV2.testDeleteBucketInventoryNotExist();
	}

	@Test
	@Tag("Error")
	// 존재하지 않은 버킷에 인벤토리를 설정하려고 할 경우 실패하는지 확인
	void testPutBucketInventoryNotExist() {
		test.testPutBucketInventoryNotExist();
		testV2.testPutBucketInventoryNotExist();
	}

	@Test
	@Tag("Error")
	// 인벤토리 아이디를 빈값으로 설정하려고 할 경우 실패하는지 확인
	void testPutBucketInventoryIdNotExist() {
		test.testPutBucketInventoryIdNotExist();
		testV2.testPutBucketInventoryIdNotExist();
	}

	@Test
	@Tag("Error")
	// 인벤토리 아이디를 중복으로 설정하려고 할 경우 실패하는지 확인
	void testPutBucketInventoryIdDuplicate(){
		test.testPutBucketInventoryIdDuplicate();
		testV2.testPutBucketInventoryIdDuplicate();
	}

	@Disabled("aws에서 타깃 버킷이 존재하는지 확인하지 않음")
	@Test
	@Tag("Error")
	// 타깃 버킷이 존재하지 않을 경우 실패하는지 확인
	void testPutBucketInventoryTargetNotExist() {
		test.testPutBucketInventoryTargetNotExist();
		testV2.testPutBucketInventoryTargetNotExist();
	}

	@Test
	@Tag("Error")
	// 지원하지 않는 파일 형식의 인벤토리를 설정하려고 할 경우 실패하는지 확인
	void testPutBucketInventoryInvalidFormat() {
		test.testPutBucketInventoryInvalidFormat();
		testV2.testPutBucketInventoryInvalidFormat();
	}

	@Test
	@Tag("Error")
	// 올바르지 않은 주기의 인벤토리를 설정하려고 할 경우 실패하는지 확인
	void testPutBucketInventoryInvalidFrequency() {
		test.testPutBucketInventoryInvalidFrequency();
		testV2.testPutBucketInventoryInvalidFrequency();
	}

	@Test
	@Tag("Error")
	// 대소문자를 잘못 입력하여 인벤토리를 설정하려고 할 경우 실패하는지 확인
	void testPutBucketInventoryInvalidCase() {
		test.testPutBucketInventoryInvalidCase();
		testV2.testPutBucketInventoryInvalidCase();
	}

	@Test
	@Tag("Put")
	// 접두어를 포함한 인벤토리 설정이 올바르게 적용되는지 확인
	void testPutBucketInventoryPrefix() {
		test.testPutBucketInventoryPrefix();
		testV2.testPutBucketInventoryPrefix();
	}

	@Test
	@Tag("Put")
	// 옵션을 포함한 인벤토리 설정이 올바르게 적용되는지 확인
	void testPutBucketInventoryOptional() {
		test.testPutBucketInventoryOptional();
		testV2.testPutBucketInventoryOptional();
	}

	@Test
	@Tag("Error")
	// 올바르지 않은 옵션을 포함한 인벤토리를 설정하려고 할 경우 실패하는지 확인
	void testPutBucketInventoryInvalidOptional() {
		test.testPutBucketInventoryInvalidOptional();
		testV2.testPutBucketInventoryInvalidOptional();
	}
}
