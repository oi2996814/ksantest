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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.hc.core5.http.HttpStatus;
import org.example.Data.MainData;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Execution(ExecutionMode.CONCURRENT)
public class BucketOwner extends TestBase {
	@Test
	@Tag("Check")
	public void testBucketOwnerHeadBucket() {
		var client = getClient();
		var bucketName = createBucket(client, 1);

		client.headBucket(h -> h.bucket(bucketName).expectedBucketOwner(getOwner()));
	}

	@Test
	@Tag("ERROR")
	public void testBucketOwnerHeadBucketMismatch() {
		var client = getClient();
		var bucketName = createBucket(client, 2);

		var e = assertThrows(S3Exception.class,
				() -> client.headBucket(h -> h.bucket(bucketName).expectedBucketOwner(getWrongOwner())));
		assertEquals(HttpStatus.SC_FORBIDDEN, e.statusCode());
	}

	@Test
	@Tag("Check")
	public void testBucketOwnerListObjectsV2() {
		var key = "testBucketOwnerListObjectsV2";
		var client = getClient();
		var bucketName = createObjects(client, 3, key);

		var response = client.listObjectsV2(l -> l.bucket(bucketName).expectedBucketOwner(getOwner()));
		assertEquals(1, response.keyCount());
	}

	@Test
	@Tag("ERROR")
	public void testBucketOwnerListObjectsV2Mismatch() {
		var key = "testBucketOwnerListObjectsV2Mismatch";
		var client = getClient();
		var bucketName = createObjects(client, 4, key);

		var e = assertThrows(S3Exception.class,
				() -> client.listObjectsV2(l -> l.bucket(bucketName).expectedBucketOwner(getWrongOwner())));
		assertEquals(HttpStatus.SC_FORBIDDEN, e.statusCode());
		assertEquals(MainData.ACCESS_DENIED, e.awsErrorDetails().errorCode());
	}

	@Test
	@Tag("Check")
	public void testBucketOwnerPutObject() {
		var key = "testBucketOwnerPutObject";
		var client = getClient();
		var bucketName = createBucket(client, 5);

		client.putObject(p -> p.bucket(bucketName).key(key).expectedBucketOwner(getOwner()),
				RequestBody.fromString(key));

		succeedGetObject(client, bucketName, key, key);
	}

	@Test
	@Tag("ERROR")
	public void testBucketOwnerPutObjectMismatch() {
		var key = "testBucketOwnerPutObjectMismatch";
		var client = getClient();
		var bucketName = createBucket(client, 6);

		var e = assertThrows(S3Exception.class,
				() -> client.putObject(p -> p.bucket(bucketName).key(key).expectedBucketOwner(getWrongOwner()),
						RequestBody.fromString(key)));
		assertEquals(HttpStatus.SC_FORBIDDEN, e.statusCode());
		assertEquals(MainData.ACCESS_DENIED, e.awsErrorDetails().errorCode());
	}

	@Test
	@Tag("Check")
	public void testBucketOwnerGetObject() {
		var key = "testBucketOwnerGetObject";
		var client = getClient();
		var bucketName = createObjects(client, 7, key);

		var response = client.getObject(g -> g.bucket(bucketName).key(key).expectedBucketOwner(getOwner()));
		assertEquals(key, getBody(response));
	}

	@Test
	@Tag("ERROR")
	public void testBucketOwnerGetObjectMismatch() {
		var key = "testBucketOwnerGetObjectMismatch";
		var client = getClient();
		var bucketName = createObjects(client, 8, key);

		var e = assertThrows(S3Exception.class,
				() -> client.getObject(g -> g.bucket(bucketName).key(key).expectedBucketOwner(getWrongOwner())));
		assertEquals(HttpStatus.SC_FORBIDDEN, e.statusCode());
		assertEquals(MainData.ACCESS_DENIED, e.awsErrorDetails().errorCode());
	}

	@Test
	@Tag("Check")
	public void testBucketOwnerDeleteObject() {
		var key = "testBucketOwnerDeleteObject";
		var client = getClient();
		var bucketName = createObjects(client, 9, key);

		client.deleteObject(d -> d.bucket(bucketName).key(key).expectedBucketOwner(getOwner()));

		failedGetObject(client, bucketName, key, HttpStatus.SC_NOT_FOUND, MainData.NO_SUCH_KEY);
	}

	@Test
	@Tag("ERROR")
	public void testBucketOwnerDeleteObjectMismatch() {
		var key = "testBucketOwnerDeleteObjectMismatch";
		var client = getClient();
		var bucketName = createObjects(client, 10, key);

		var e = assertThrows(S3Exception.class,
				() -> client.deleteObject(d -> d.bucket(bucketName).key(key).expectedBucketOwner(getWrongOwner())));
		assertEquals(HttpStatus.SC_FORBIDDEN, e.statusCode());
		assertEquals(MainData.ACCESS_DENIED, e.awsErrorDetails().errorCode());

		succeedGetObject(client, bucketName, key, key);
	}

	@Test
	@Tag("Check")
	public void testSourceBucketOwnerCopyObject() {
		var source = "testSourceBucketOwnerCopyObjectSource";
		var target = "testSourceBucketOwnerCopyObjectTarget";
		var client = getClient();
		var sourceBucket = createObjects(client, 11, source);
		var targetBucket = createBucket(client, 11);

		client.copyObject(c -> c
				.sourceBucket(sourceBucket).sourceKey(source)
				.destinationBucket(targetBucket).destinationKey(target)
				.expectedSourceBucketOwner(getOwner())
				.expectedBucketOwner(getOwner()));

		succeedGetObject(client, targetBucket, target, source);
	}

	@Test
	@Tag("ERROR")
	public void testSourceBucketOwnerCopyObjectMismatch() {
		var source = "testSourceBucketOwnerCopyObjectMismatchSource";
		var target = "testSourceBucketOwnerCopyObjectMismatchTarget";
		var client = getClient();
		var sourceBucket = createObjects(client, 12, source);
		var targetBucket = createBucket(client, 12);

		var e = assertThrows(S3Exception.class, () -> client.copyObject(c -> c
				.sourceBucket(sourceBucket).sourceKey(source)
				.destinationBucket(targetBucket).destinationKey(target)
				.expectedSourceBucketOwner(getWrongOwner())
				.expectedBucketOwner(getOwner())));
		assertEquals(HttpStatus.SC_FORBIDDEN, e.statusCode());
		assertEquals(MainData.ACCESS_DENIED, e.awsErrorDetails().errorCode());

		failedGetObject(client, targetBucket, target, HttpStatus.SC_NOT_FOUND, MainData.NO_SUCH_KEY);
	}

	@Test
	@Tag("ERROR")
	public void testBucketOwnerCopyObjectMismatch() {
		var source = "testBucketOwnerCopyObjectMismatchSource";
		var target = "testBucketOwnerCopyObjectMismatchTarget";
		var client = getClient();
		var sourceBucket = createObjects(client, 13, source);
		var targetBucket = createBucket(client, 13);

		var e = assertThrows(S3Exception.class, () -> client.copyObject(c -> c
				.sourceBucket(sourceBucket).sourceKey(source)
				.destinationBucket(targetBucket).destinationKey(target)
				.expectedSourceBucketOwner(getOwner())
				.expectedBucketOwner(getWrongOwner())));
		assertEquals(HttpStatus.SC_FORBIDDEN, e.statusCode());
		assertEquals(MainData.ACCESS_DENIED, e.awsErrorDetails().errorCode());

		failedGetObject(client, targetBucket, target, HttpStatus.SC_NOT_FOUND, MainData.NO_SUCH_KEY);
	}

	@Test
	@Tag("Check")
	public void testSourceBucketOwnerUploadPartCopy() {
		var source = "testSourceBucketOwnerUploadPartCopySource";
		var target = "testSourceBucketOwnerUploadPartCopyTarget";
		var client = getClient();
		var sourceBucket = createObjects(client, 14, source);
		var targetBucket = createBucket(client, 14);

		var uploadId = client.createMultipartUpload(c -> c.bucket(targetBucket).key(target)).uploadId();

		var response = client.uploadPartCopy(c -> c
				.sourceBucket(sourceBucket).sourceKey(source)
				.destinationBucket(targetBucket).destinationKey(target)
				.uploadId(uploadId).partNumber(1)
				.expectedSourceBucketOwner(getOwner())
				.expectedBucketOwner(getOwner()));
		assertNotNull(response.copyPartResult().eTag());

		client.abortMultipartUpload(a -> a.bucket(targetBucket).key(target).uploadId(uploadId));
	}

	@Test
	@Tag("ERROR")
	public void testSourceBucketOwnerUploadPartCopyMismatch() {
		var source = "testSourceBucketOwnerUploadPartCopyMismatchSource";
		var target = "testSourceBucketOwnerUploadPartCopyMismatchTarget";
		var client = getClient();
		var sourceBucket = createObjects(client, 15, source);
		var targetBucket = createBucket(client, 15);

		var uploadId = client.createMultipartUpload(c -> c.bucket(targetBucket).key(target)).uploadId();

		var e = assertThrows(S3Exception.class, () -> client.uploadPartCopy(c -> c
				.sourceBucket(sourceBucket).sourceKey(source)
				.destinationBucket(targetBucket).destinationKey(target)
				.uploadId(uploadId).partNumber(1)
				.expectedSourceBucketOwner(getWrongOwner())
				.expectedBucketOwner(getOwner())));
		assertEquals(HttpStatus.SC_FORBIDDEN, e.statusCode());
		assertEquals(MainData.ACCESS_DENIED, e.awsErrorDetails().errorCode());

		client.abortMultipartUpload(a -> a.bucket(targetBucket).key(target).uploadId(uploadId));
	}

	@Test
	@Tag("ERROR")
	public void testBucketOwnerUploadPartCopyMismatch() {
		var source = "testBucketOwnerUploadPartCopyMismatchSource";
		var target = "testBucketOwnerUploadPartCopyMismatchTarget";
		var client = getClient();
		var sourceBucket = createObjects(client, 16, source);
		var targetBucket = createBucket(client, 16);

		var uploadId = client.createMultipartUpload(c -> c.bucket(targetBucket).key(target)).uploadId();

		var e = assertThrows(S3Exception.class, () -> client.uploadPartCopy(c -> c
				.sourceBucket(sourceBucket).sourceKey(source)
				.destinationBucket(targetBucket).destinationKey(target)
				.uploadId(uploadId).partNumber(1)
				.expectedSourceBucketOwner(getOwner())
				.expectedBucketOwner(getWrongOwner())));
		assertEquals(HttpStatus.SC_FORBIDDEN, e.statusCode());
		assertEquals(MainData.ACCESS_DENIED, e.awsErrorDetails().errorCode());

		client.abortMultipartUpload(a -> a.bucket(targetBucket).key(target).uploadId(uploadId));
	}
}
