/*
* Copyright (c) 2021 PSPACE, inc. KSAN Development Team ksan@pspace.co.kr
* KSAN is a suite of free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 
* 3 of the License.  See LICENSE for details
*
* 본 프로그램 및 관련 소스코드, 문서 등 모든 자료는 있는 그대로 제공이 됩니다.
* KSAN 프로젝트의 개발자 및 개발사는 이 프로그램을 사용한 결과에 따른 어떠한 책임도 지지 않습니다.
* KSAN 개발팀은 사전 공지, 허락, 동의 없이 KSAN 개발에 관련된 모든 결과물에 대한 LICENSE 방식을 변경 할 권리가 있습니다.
*/
package org.example.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import org.example.Data.MainData;
import org.example.Utility.Utils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.AbortIncompleteMultipartUpload;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.NoncurrentVersionExpiration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration.Rule;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.ListMultipartUploadsRequest;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import com.amazonaws.services.s3.model.lifecycle.LifecyclePrefixPredicate;

public class LifeCycle extends TestBase
{
	@org.junit.jupiter.api.BeforeAll
	static public void BeforeAll()
	{
		System.out.println("LifeCycle Start");
	}

	@org.junit.jupiter.api.AfterAll
	static public void AfterAll()
	{
		System.out.println("LifeCycle End");
	}

	@Test
	@Tag("Check")
	//버킷의 Lifecycle 규칙을 추가 가능한지 확인
	public void test_lifecycle_set() {
		var bucketName = getNewBucket();
		var client = getClient();
		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpirationInDays(1)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));
		Rules.add(new Rule().withId("rule2").withExpirationInDays(2)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test2/")))
				.withStatus(BucketLifecycleConfiguration.DISABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);

		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
	}

	@Test
	@Tag("Get")
	//버킷에 설정한 Lifecycle 규칙을 가져올 수 있는지 확인
	public void test_lifecycle_get() {
		var bucketName = getNewBucket();
		var client = getClient();
		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpirationInDays(31)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));
		Rules.add(new Rule().withId("rule2").withExpirationInDays(120)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test2/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);

		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		var Response = client.getBucketLifecycleConfiguration(bucketName);
		PrefixLifecycleConfigurationCheck(Rules, Response.getRules());
	}

	@Test
	@Tag("Check")
	//ID 없이 버킷에 Lifecycle 규칙을 설정 할 수 있는지 확인
	public void test_lifecycle_get_no_id() {
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withExpirationInDays(31)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));
		Rules.add(new Rule().withExpirationInDays(120)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test2/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);

		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		var Response = client.getBucketLifecycleConfiguration(bucketName);
		var CurrentLifeCycle = Response.getRules();

		for (int i = 0; i < Rules.size(); i++) {
			assertNotNull(CurrentLifeCycle.get(i).getId());
			assertEquals(Rules.get(i).getExpirationDate(), CurrentLifeCycle.get(i).getExpirationDate());
			assertEquals(Rules.get(i).getExpirationInDays(), CurrentLifeCycle.get(i).getExpirationInDays());
			assertEquals(((LifecyclePrefixPredicate) Rules.get(i).getFilter().getPredicate()).getPrefix(),
					((LifecyclePrefixPredicate) CurrentLifeCycle.get(i).getFilter().getPredicate()).getPrefix());
			assertEquals(Rules.get(i).getStatus(), CurrentLifeCycle.get(i).getStatus());
		}
	}

	@Test
	@Tag("Version")
	//버킷에 버저닝 설정이 되어있는 상태에서 Lifecycle 규칙을 추가 가능한지 확인
	public void test_lifecycle_expiration_versioning_enabled()
	{
		var bucketName = getNewBucket();
		var client = getClient();
		var Key = "test1/a";
		checkConfigureVersioningRetry(bucketName, BucketVersioningConfiguration.ENABLED);
		CreateMultipleVersions(client, bucketName, Key, 1, true);
		client.deleteObject(bucketName, Key);

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpirationInDays(1)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("expire1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);

		var Response = client.listVersions(bucketName, null);
		var Versions = GetVersions(Response.getVersionSummaries());
		var DeleteMarkers = GetDeleteMarkers(Response.getVersionSummaries());
		assertEquals(1, Versions.size());
		assertEquals(1, DeleteMarkers.size());
	}

	@Test
	@Tag("Check")
	//버킷에 Lifecycle 규칙을 설정할때 ID의 길이가 너무 길면 실패하는지 확인
	public void test_lifecycle_id_too_long()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId(Utils.randomTextToLong(256)).withExpirationInDays(2)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("tset1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		var e = assertThrows(AmazonServiceException.class, () -> client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle));
		assertEquals(400, e.getStatusCode());
		assertEquals(MainData.InvalidArgument, e.getErrorCode());
	}

	@Test
	@Tag("Duplicate")
	//버킷에 Lifecycle 규칙을 설정할때 같은 ID로 규칙을 여러개 설정할경우 실패하는지 확인
	public void test_lifecycle_same_id()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpirationInDays(1)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("tset1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));
		Rules.add(new Rule().withId("rule1").withExpirationInDays(2)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("tset2/")))
				.withStatus(BucketLifecycleConfiguration.DISABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		var e = assertThrows(AmazonServiceException.class, () -> client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle));
		assertEquals(400, e.getStatusCode());
		assertEquals(MainData.InvalidArgument, e.getErrorCode());
	}

	@Test
	@Tag("ERROR")
	//버킷에 Lifecycle 규칙중 status를 잘못 설정할때 실패하는지 확인
	public void test_lifecycle_invalid_status()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpirationInDays(2)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("tset1/")))
				.withStatus("invalid"));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		var e = assertThrows(AmazonServiceException.class, () -> client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle));
		assertEquals(400, e.getStatusCode());
		assertEquals(MainData.MalformedXML, e.getErrorCode());
	}

	@Test
	@Tag("Date")
	//버킷의 Lifecycle규칙에 날짜를 입력가능한지 확인
	public void test_lifecycle_set_date()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpirationDate(new Calendar.Builder().setDate(2099, 10, 10).setTimeZone(TimeZone.getTimeZone("GMT")).build().getTime())
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("tset1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
	}

	@Test
	@Tag("ERROR")
	//버킷의 Lifecycle규칙에 날짜를 올바르지 않은 형식으로 입력했을때 실패 확인
	public void test_lifecycle_set_invalid_date()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpirationDate(new Calendar.Builder().setDate(2099, 10, 10).build().getTime())
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("tset1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		var e = assertThrows(AmazonServiceException.class, () -> client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle));
		assertEquals(400, e.getStatusCode());
		assertEquals(MainData.InvalidArgument, e.getErrorCode());
	}

	@Test
	@Tag("Version")
	//버킷의 버저닝설정이 없는 환경에서 버전관리용 Lifecycle이 올바르게 설정되는지 확인
	public void test_lifecycle_set_noncurrent()
	{
		var bucketName = createObjects(new ArrayList<>(Arrays.asList(new String[] { "past/foo", "future/bar" })));
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withNoncurrentVersionExpiration(new NoncurrentVersionExpiration().withDays(2))
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("past/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));
		Rules.add(new Rule().withId("rule2").withNoncurrentVersionExpiration(new NoncurrentVersionExpiration().withDays(3))
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("futrue/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
	}

	@Test
	@Tag("Version")
	//버킷의 버저닝설정이 되어있는 환경에서 Lifecycle 이 올바르게 동작하는지 확인
	public void test_lifecycle_noncur_expiration()
	{
		var bucketName = getNewBucket();
		var client = getClient();
		
		checkConfigureVersioningRetry(bucketName, BucketVersioningConfiguration.ENABLED);
		CreateMultipleVersions(client, bucketName, "test1/a", 3, true);
		CreateMultipleVersions(client, bucketName, "test2/abc", 3, false);

		var Response = client.listVersions(bucketName, null);
		var InitVersions = Response.getVersionSummaries();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withNoncurrentVersionExpiration(new NoncurrentVersionExpiration().withDays(2))
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		
		assertEquals(6, InitVersions.size());
	}

	@Test
	@Tag("DeleteMarker")
	//DeleteMarker에 대한 Lifecycle 규칙을 설정 할 수 있는지 확인
	public void test_lifecycle_set_deletemarker()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpiredObjectDeleteMarker(true)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		
	}

	@Test
	@Tag("Filter")
	//Lifecycle 규칙에 필터링값을 설정 할 수 있는지 확인
	public void test_lifecycle_set_filter()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpiredObjectDeleteMarker(true)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("foo")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		
	}

	@Test
	@Tag("Filter")
	//Lifecycle 규칙에 필터링에 비어있는 값을 설정 할 수 있는지 확인
	public void test_lifecycle_set_empty_filter()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpiredObjectDeleteMarker(true)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		
	}

	@Test
	@Tag("DeleteMarker")
	//DeleteMarker에 대한 Lifecycle 규칙이 올바르게 동작하는지 확인
	public void test_lifecycle_deletemarker_expiration()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		checkConfigureVersioningRetry(bucketName, BucketVersioningConfiguration.ENABLED);
		CreateMultipleVersions(client, bucketName, "test1/a", 1, true);
		CreateMultipleVersions(client, bucketName, "test2/abc", 1, false);
		client.deleteObject(bucketName, "test1/a");
		client.deleteObject(bucketName, "test2/abc");

		var Response = client.listVersions(bucketName, null);
		var TotalVersions = Response.getVersionSummaries();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withNoncurrentVersionExpiration(new NoncurrentVersionExpiration().withDays(1))
				.withExpiredObjectDeleteMarker(true)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		
		assertEquals(4, TotalVersions.size());
	}

	@Test
	@Tag("Multipart")
	//AbortIncompleteMultipartUpload에 대한 Lifecycle 규칙을 설정 할 수 있는지 확인
	public void test_lifecycle_set_multipart()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1")
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED)
				.withAbortIncompleteMultipartUpload(new AbortIncompleteMultipartUpload().withDaysAfterInitiation(2)));
		Rules.add(new Rule().withId("rule2")
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test2/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED)
				.withAbortIncompleteMultipartUpload(new AbortIncompleteMultipartUpload().withDaysAfterInitiation(3)));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		
	}

	@Test
	@Tag("Multipart")
	//AbortIncompleteMultipartUpload에 대한 Lifecycle 규칙이 올바르게 동작하는지 확인
	public void test_lifecycle_multipart_expiration()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var KeyNames = new ArrayList<>(Arrays.asList(new String[] { "test1/a", "test2/b" }));
		var UploadIDs = new ArrayList<String>();

		for (var Key : KeyNames)
		{
			var Response = client.initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, Key));
			UploadIDs.add(Response.getUploadId());
		}

		var ListResponse = client.listMultipartUploads(new ListMultipartUploadsRequest(bucketName));
		var InitUploads = ListResponse.getMultipartUploads();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1")
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED)
				.withAbortIncompleteMultipartUpload(new AbortIncompleteMultipartUpload().withDaysAfterInitiation(2)));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		assertEquals(2, InitUploads.size());
	}
	
	@Test
	@Tag("Delete")
	//버킷의 Lifecycle 규칙을 삭제 가능한지 확인
	public void test_lifecycle_delete()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpirationInDays(1)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));
		Rules.add(new Rule().withId("rule2").withExpirationInDays(2)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test2/")))
				.withStatus(BucketLifecycleConfiguration.DISABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);

		client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle);
		client.deleteBucketLifecycleConfiguration(bucketName);
	}

	@Test
	@Tag("ERROR")
	// Lifecycle 규칙에 0일을 설정할때 실패하는지 확인
	public void test_lifecycle_set_expiration_zero()
	{
		var bucketName = getNewBucket();
		var client = getClient();

		var Rules = new ArrayList<Rule>();
		Rules.add(new Rule().withId("rule1").withExpirationInDays(0)
				.withFilter(new LifecycleFilter(new LifecyclePrefixPredicate("test1/")))
				.withStatus(BucketLifecycleConfiguration.ENABLED));

		var MyLifeCycle = new BucketLifecycleConfiguration(Rules);
		var e = assertThrows(AmazonServiceException.class, () -> client.setBucketLifecycleConfiguration(bucketName, MyLifeCycle));
		assertEquals(400, e.getStatusCode());
		assertEquals(MainData.InvalidArgument, e.getErrorCode());
	}
}
