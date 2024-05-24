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
package org.example.testV2;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.example.Data.MainData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import software.amazon.awssdk.awscore.exception.AwsServiceException;

public class DeleteBucket extends TestBase
{
	@org.junit.jupiter.api.BeforeAll
	public static void beforeAll()
	{
		System.out.println("DeleteBucket Start");
	}

	@org.junit.jupiter.api.AfterAll
	public static void afterAll()
	{
		System.out.println("DeleteBucket End");
	}

	@Test
	@Tag("ERROR")
	//존재하지 않는 버킷을 삭제하려 했을 경우 실패 확인
	public void testBucketDeleteNotExist() {
		var bucketName = getNewBucketNameOnly();
		var client = getClient();

		var e = assertThrows(AwsServiceException.class, () -> client.deleteBucket(d->d.bucket(bucketName)));

		assertEquals(404, e.statusCode());
		assertEquals(MainData.NoSuchBucket, e.getMessage());
		deleteBucketList(bucketName);
	}

	@Test
	@Tag("ERROR")
	//내용이 비어있지 않은 버킷을 삭제하려 했을 경우 실패 확인
	public void testBucketDeleteNonempty() {
		var bucketName = createObjects(List.of("foo"));
		var client = getClient();

		var e = assertThrows(AwsServiceException.class, () -> client.deleteBucket(d->d.bucket(bucketName)));

		assertEquals(409, e.statusCode());
		assertEquals(MainData.BucketNotEmpty, e.getMessage());
	}

	@Test
	@Tag("ERROR")
	//이미 삭제된 버킷을 다시 삭제 시도할 경우 실패 확인
	public void testBucketCreateDelete() {
		var bucketName = getNewBucket();
		var client = getClient();

		client.deleteBucket(d->d.bucket(bucketName));

		var e = assertThrows(AwsServiceException.class, () -> client.deleteBucket(d->d.bucket(bucketName)));

		assertEquals(404, e.statusCode());
		assertEquals(MainData.NoSuchBucket, e.getMessage());
		deleteBucketList(bucketName);
	}
}
