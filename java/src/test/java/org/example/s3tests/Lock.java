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
package org.example.s3tests;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DefaultRetention;
import com.amazonaws.services.s3.model.DeleteVersionRequest;
import com.amazonaws.services.s3.model.GetObjectLegalHoldRequest;
import com.amazonaws.services.s3.model.GetObjectLockConfigurationRequest;
import com.amazonaws.services.s3.model.GetObjectRetentionRequest;
import com.amazonaws.services.s3.model.ObjectLockConfiguration;
import com.amazonaws.services.s3.model.ObjectLockEnabled;
import com.amazonaws.services.s3.model.ObjectLockLegalHold;
import com.amazonaws.services.s3.model.ObjectLockLegalHoldStatus;
import com.amazonaws.services.s3.model.ObjectLockRetention;
import com.amazonaws.services.s3.model.ObjectLockRetentionMode;
import com.amazonaws.services.s3.model.ObjectLockRule;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.SetObjectLegalHoldRequest;
import com.amazonaws.services.s3.model.SetObjectLockConfigurationRequest;
import com.amazonaws.services.s3.model.SetObjectRetentionRequest;

public class Lock extends TestBase {
	@Test
	@DisplayName("test_object_lock_put_obj_lock")
	@Tag("Check")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 잠금 설정이 가능한지 확인")
	public void test_object_lock_put_obj_lock() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Conf = new ObjectLockConfiguration().withObjectLockEnabled(ObjectLockEnabled.ENABLED)
				.withRule(new ObjectLockRule().withDefaultRetention(
						new DefaultRetention().withMode(ObjectLockRetentionMode.COMPLIANCE).withYears(1)));
		Client.setObjectLockConfiguration(
				new SetObjectLockConfigurationRequest().withBucketName(BucketName).withObjectLockConfiguration(Conf));

		var VersionResponse = Client.getBucketVersioningConfiguration(BucketName);
		assertEquals(BucketVersioningConfiguration.ENABLED, VersionResponse.getStatus());
	}

	@Test
	@DisplayName("test_object_lock_put_obj_lock_invalid_bucket")
	@Tag("ERROR")
	//@Tag("버킷을 Lock옵션을 활성화 하지않을 경우 lock 설정이 실패")
	public void test_object_lock_put_obj_lock_invalid_bucket() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(BucketName);

		var Conf = new ObjectLockConfiguration().withObjectLockEnabled(ObjectLockEnabled.ENABLED)
				.withRule(new ObjectLockRule().withDefaultRetention(
						new DefaultRetention().withMode(ObjectLockRetentionMode.GOVERNANCE).withYears(1)));

		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectLockConfiguration(
				new SetObjectLockConfigurationRequest().withBucketName(BucketName).withObjectLockConfiguration(Conf)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(409, StatusCode);
		assertEquals(MainData.InvalidBucketState, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_put_obj_lock_with_days_and_years")
	@Tag("ERROR")
	//@Tag("[버킷의 Lock옵션을 활성화] Days, Years값 모두 입력하여 Lock 설정할경우 실패")
	public void test_object_lock_put_obj_lock_with_days_and_years() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Conf = new ObjectLockConfiguration().withObjectLockEnabled(ObjectLockEnabled.ENABLED)
				.withRule(new ObjectLockRule().withDefaultRetention(
						new DefaultRetention().withMode(ObjectLockRetentionMode.GOVERNANCE).withYears(1).withDays(1)));
		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectLockConfiguration(
				new SetObjectLockConfigurationRequest().withBucketName(BucketName).withObjectLockConfiguration(Conf)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.MalformedXML, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_put_obj_lock_invalid_days")
	@Tag("ERROR")
	//@Tag("[버킷의 Lock옵션을 활성화] Days값을 0이하로 입력하여 Lock 설정할경우 실패")
	public void test_object_lock_put_obj_lock_invalid_days() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Conf = new ObjectLockConfiguration().withObjectLockEnabled(ObjectLockEnabled.ENABLED)
				.withRule(new ObjectLockRule().withDefaultRetention(
						new DefaultRetention().withMode(ObjectLockRetentionMode.GOVERNANCE).withDays(0)));

		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectLockConfiguration(
				new SetObjectLockConfigurationRequest().withBucketName(BucketName).withObjectLockConfiguration(Conf)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.InvalidArgument, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_put_obj_lock_invalid_years")
	@Tag("ERROR")
	//@Tag("[버킷의 Lock옵션을 활성화] Years값을 0이하로 입력하여 Lock 설정할경우 실패")
	public void test_object_lock_put_obj_lock_invalid_years() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Conf = new ObjectLockConfiguration().withObjectLockEnabled(ObjectLockEnabled.ENABLED)
				.withRule(new ObjectLockRule().withDefaultRetention(
						new DefaultRetention().withMode(ObjectLockRetentionMode.GOVERNANCE).withYears(-1)));

		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectLockConfiguration(
				new SetObjectLockConfigurationRequest().withBucketName(BucketName).withObjectLockConfiguration(Conf)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.InvalidArgument, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_put_obj_lock_invalid_mode")
	@Tag("ERROR")
	//@Tag("[버킷의 Lock옵션을 활성화] mode값이 올바르지 않은상태에서 Lock 설정할 경우 실패")
	public void test_object_lock_put_obj_lock_invalid_mode() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Conf = new ObjectLockConfiguration().withObjectLockEnabled(ObjectLockEnabled.ENABLED).withRule(
				new ObjectLockRule().withDefaultRetention(new DefaultRetention().withMode("invalid").withYears(1)));

		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectLockConfiguration(
				new SetObjectLockConfigurationRequest().withBucketName(BucketName).withObjectLockConfiguration(Conf)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.MalformedXML, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_put_obj_lock_invalid_status")
	@Tag("ERROR")
	//@Tag("[버킷의 Lock옵션을 활성화] status값이 올바르지 않은상태에서 Lock 설정할 경우 실패")
	public void test_object_lock_put_obj_lock_invalid_status() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Conf = new ObjectLockConfiguration().withObjectLockEnabled("Disabled")
				.withRule(new ObjectLockRule().withDefaultRetention(
						new DefaultRetention().withMode(ObjectLockRetentionMode.GOVERNANCE).withYears(1)));

		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectLockConfiguration(
				new SetObjectLockConfigurationRequest().withBucketName(BucketName).withObjectLockConfiguration(Conf)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.MalformedXML, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_suspend_versioning")
	@Tag("Version")
	//@Tag("[버킷의 Lock옵션을 활성화] 버킷의 버저닝을 일시중단하려고 할경우 실패")
	public void test_object_lock_suspend_versioning() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var e = assertThrows(AmazonServiceException.class,
				() -> Client.setBucketVersioningConfiguration(new SetBucketVersioningConfigurationRequest(BucketName,
						new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED))));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(409, StatusCode);
		assertEquals(MainData.InvalidBucketState, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_get_obj_lock")
	@Tag("Check")
	//@Tag("[버킷의 Lock옵션을 활성화] 버킷의 lock설정이 올바르게 되었는지 확인")
	public void test_object_lock_get_obj_lock() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Conf = new ObjectLockConfiguration().withObjectLockEnabled(ObjectLockEnabled.ENABLED)
				.withRule(new ObjectLockRule().withDefaultRetention(
						new DefaultRetention().withMode(ObjectLockRetentionMode.GOVERNANCE).withDays(1)));

		Client.setObjectLockConfiguration(
				new SetObjectLockConfigurationRequest().withBucketName(BucketName).withObjectLockConfiguration(Conf));

		var Response = Client
				.getObjectLockConfiguration(new GetObjectLockConfigurationRequest().withBucketName(BucketName));
		LockCompare(Conf, Response.getObjectLockConfiguration());
	}

	@Test
	@DisplayName("test_object_lock_get_obj_lock_invalid_bucket")
	@Tag("ERROR")
	//@Tag("버킷을 Lock옵션을 활성화 하지않을 경우 lock 설정 조회 실패")
	public void test_object_lock_get_obj_lock_invalid_bucket() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(BucketName);

		var e = assertThrows(AmazonServiceException.class, () -> Client
				.getObjectLockConfiguration(new GetObjectLockConfigurationRequest().withBucketName(BucketName)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(404, StatusCode);
		assertEquals(MainData.ObjectLockConfigurationNotFoundError, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_put_obj_retention")
	@Tag("Retention")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트에 Lock 유지기한 설정이 가능한지 확인")
	public void test_object_lock_put_obj_retention() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));
		var Key = "file1";

		var PutResponse = Client.putObject(BucketName, Key, "abc");
		var VersionID = PutResponse.getVersionId();

		var Retention = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());

		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention));

		Client.deleteVersion(new DeleteVersionRequest(BucketName, Key, VersionID).withBypassGovernanceRetention(true));
	}

	@Test
	@DisplayName("test_object_lock_put_obj_retention_invalid_bucket")
	@Tag("Retention")
	//@Tag("버킷을 Lock옵션을 활성화 하지않을 경우 오브젝트에 Lock 유지기한 설정 실패")
	public void test_object_lock_put_obj_retention_invalid_bucket() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(BucketName);
		var Key = "file1";

		Client.putObject(BucketName, Key, "abc");

		var Retention = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());

		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.InvalidRequest, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_put_obj_retention_invalid_mode")
	@Tag("Retention")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트에 Lock 유지기한 설정할때 Mode값이 올바르지 않을 경우 설정 실패")
	public void test_object_lock_put_obj_retention_invalid_mode() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));
		var Key = "file1";

		Client.putObject(BucketName, Key, "abc");

		var Retention = new ObjectLockRetention().withMode("invalid").withRetainUntilDate(new Calendar.Builder()
				.setDate(2030, 1, 1).setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());

		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.MalformedXML, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_get_obj_retention")
	@Tag("Retention")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트에 Lock 유지기한 설정이 올바른지 확인")
	public void test_object_lock_get_obj_retention() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));
		var Key = "file1";

		var PutResponse = Client.putObject(BucketName, Key, "abc");
		var VersionID = PutResponse.getVersionId();

		var Retention = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());

		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention));
		var Response = Client
				.getObjectRetention(new GetObjectRetentionRequest().withBucketName(BucketName).withKey(Key));
		RetentionCompare(Retention, Response.getRetention());
		Client.deleteVersion(new DeleteVersionRequest(BucketName, Key, VersionID).withBypassGovernanceRetention(true));
	}

	@Test
	@DisplayName("test_object_lock_get_obj_retention_invalid_bucket")
	@Tag("Retention")
	//@Tag("버킷을 Lock옵션을 활성화 하지않을 경우 오브젝트에 Lock 유지기한 조회 실패")
	public void test_object_lock_get_obj_retention_invalid_bucket() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(BucketName);
		var Key = "file1";

		Client.putObject(BucketName, Key, "abc");

		var e = assertThrows(AmazonServiceException.class, () -> Client
				.getObjectRetention(new GetObjectRetentionRequest().withBucketName(BucketName).withKey(Key)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.InvalidRequest, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_put_obj_retention_versionid")
	@Tag("Retention")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 특정 버전에 Lock 유지기한을 설정할 경우 올바르게 적용되었는지 확인")
	public void test_object_lock_put_obj_retention_versionid() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));
		var Key = "file1";

		Client.putObject(BucketName, Key, "abc");
		var PutResponse = Client.putObject(BucketName, Key, "abc");
		var VersionID = PutResponse.getVersionId();

		var Retention = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());

		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention));
		var Response = Client
				.getObjectRetention(new GetObjectRetentionRequest().withBucketName(BucketName).withKey(Key));
		RetentionCompare(Retention, Response.getRetention());
		Client.deleteVersion(new DeleteVersionRequest(BucketName, Key, VersionID).withBypassGovernanceRetention(true));
	}

	@Test
	@DisplayName("test_object_lock_put_obj_retention_override_default_retention")
	@Tag("Priority")
	//@Tag("[버킷의 Lock옵션을 활성화] 버킷에 설정한 Lock설정보다 오브젝트에 Lock설정한 값이 우선 적용됨을 확인")
	public void test_object_lock_put_obj_retention_override_default_retention() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Conf = new ObjectLockConfiguration().withObjectLockEnabled(ObjectLockEnabled.ENABLED)
				.withRule(new ObjectLockRule().withDefaultRetention(
						new DefaultRetention().withMode(ObjectLockRetentionMode.GOVERNANCE).withDays(1)));

		Client.setObjectLockConfiguration(
				new SetObjectLockConfigurationRequest().withBucketName(BucketName).withObjectLockConfiguration(Conf));

		var Key = "file1";
		var Body = "abc";
		var Metadata = new ObjectMetadata();
		Metadata.setContentMD5(GetMD5(Body));
		Metadata.setContentType("text/plain");
		Metadata.setContentLength(Body.length());

		var PutResponse = Client.putObject(BucketName, Key, CreateBody(Body), Metadata);
		var VersionID = PutResponse.getVersionId();

		var Retention = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());

		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention));
		var Response = Client
				.getObjectRetention(new GetObjectRetentionRequest().withBucketName(BucketName).withKey(Key));
		RetentionCompare(Retention, Response.getRetention());

		Client.deleteVersion(new DeleteVersionRequest(BucketName, Key, VersionID).withBypassGovernanceRetention(true));
	}

	@Test
	@DisplayName("test_object_lock_put_obj_retention_increase_period")
	@Tag("Overwrite")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 lock 유지기한을 늘렸을때 적용되는지 확인")
	public void test_object_lock_put_obj_retention_increase_period() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));
		var Key = "file1";

		Client.putObject(BucketName, Key, "abc");
		var PutResponse = Client.putObject(BucketName, Key, "abc");
		var VersionID = PutResponse.getVersionId();

		var Retention1 = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());
		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention1));

		var Retention2 = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 3, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());
		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention2));

		var Response = Client
				.getObjectRetention(new GetObjectRetentionRequest().withBucketName(BucketName).withKey(Key));
		RetentionCompare(Retention2, Response.getRetention());
		Client.deleteVersion(new DeleteVersionRequest(BucketName, Key, VersionID).withBypassGovernanceRetention(true));
	}

	@Test
	@DisplayName("test_object_lock_put_obj_retention_shorten_period")
	@Tag("Overwrite")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 lock 유지기한을 줄였을때 실패 확인")
	public void test_object_lock_put_obj_retention_shorten_period() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));
		var Key = "file1";

		Client.putObject(BucketName, Key, "abc");
		var PutResponse = Client.putObject(BucketName, Key, "abc");
		var VersionID = PutResponse.getVersionId();

		var Retention1 = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 3, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());
		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention1));

		var Retention2 = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());

		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention2)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(403, StatusCode);
		assertEquals(MainData.AccessDenied, ErrorCode);

		Client.deleteVersion(new DeleteVersionRequest(BucketName, Key, VersionID).withBypassGovernanceRetention(true));
	}

	@Test
	@DisplayName("test_object_lock_put_obj_retention_shorten_period_bypass")
	@Tag("Overwrite")
	//@Tag("[버킷의 Lock옵션을 활성화] 바이패스를 True로 설정하고 오브젝트의 lock 유지기한을 줄였을때 적용되는지 확인")
	public void test_object_lock_put_obj_retention_shorten_period_bypass() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));
		var Key = "file1";

		Client.putObject(BucketName, Key, "abc");
		var PutResponse = Client.putObject(BucketName, Key, "abc");
		var VersionID = PutResponse.getVersionId();

		var Retention1 = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 3, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());
		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention1));

		var Retention2 = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());
		Client.setObjectRetention(new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key)
				.withRetention(Retention2).withBypassGovernanceRetention(true));

		var Response = Client
				.getObjectRetention(new GetObjectRetentionRequest().withBucketName(BucketName).withKey(Key));
		RetentionCompare(Retention2, Response.getRetention());
		Client.deleteVersion(new DeleteVersionRequest(BucketName, Key, VersionID).withBypassGovernanceRetention(true));
	}

	@Test
	@DisplayName("test_object_lock_delete_object_with_retention")
	@Tag("ERROR")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 lock 유지기한내에 삭제를 시도할 경우 실패 확인")
	public void test_object_lock_delete_object_with_retention() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));
		var Key = "file1";

		var PutResponse = Client.putObject(BucketName, Key, "abc");
		var VersionID = PutResponse.getVersionId();

		var Retention = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());
		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention));

		var e = assertThrows(AmazonServiceException.class, () -> Client.deleteVersion(BucketName, Key, VersionID));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(403, StatusCode);
		assertEquals(MainData.AccessDenied, ErrorCode);

		Client.deleteVersion(new DeleteVersionRequest(BucketName, Key, VersionID).withBypassGovernanceRetention(true));
	}

	@Test
	@DisplayName("test_object_lock_put_legal_hold")
	@Tag("LegalHold")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 LegalHold를 활성화 가능한지 확인")
	public void test_object_lock_put_legal_hold() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Key = "file1";
		Client.putObject(BucketName, Key, "abc");

		var LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.ON);
		Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold));

		LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.OFF);
		Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold));

	}

	@Test
	@DisplayName("test_object_lock_put_legal_hold_invalid_bucket")
	@Tag("LegalHold")
	//@Tag("[버킷의 Lock옵션을 비활성화] 오브젝트의 LegalHold를 활성화 실패 확인")
	public void test_object_lock_put_legal_hold_invalid_bucket() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(BucketName);

		var Key = "file1";
		Client.putObject(BucketName, Key, "abc");

		var LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.ON);
		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.InvalidRequest, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_put_legal_hold_invalid_status")
	@Tag("LegalHold")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 LegalHold에 잘못된 값을 넣을 경우 실패 확인")
	public void test_object_lock_put_legal_hold_invalid_status() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Key = "file1";
		Client.putObject(BucketName, Key, "abc");

		var LegalHold = new ObjectLockLegalHold().withStatus("abc");
		var e = assertThrows(AmazonServiceException.class, () -> Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.MalformedXML, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_get_legal_hold")
	@Tag("LegalHold")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 LegalHold가 올바르게 적용되었는지 확인")
	public void test_object_lock_get_legal_hold() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Key = "file1";
		Client.putObject(BucketName, Key, "abc");

		var LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.ON);
		Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold));
		var Response = Client
				.getObjectLegalHold(new GetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key));
		assertEquals(LegalHold.getStatus(), Response.getLegalHold().getStatus());

		LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.OFF);
		Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold));
		Response = Client.getObjectLegalHold(new GetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key));
		assertEquals(LegalHold.getStatus(), Response.getLegalHold().getStatus());
	}

	@Test
	@DisplayName("test_object_lock_get_legal_hold_invalid_bucket")
	@Tag("LegalHold")
	//@Tag("[버킷의 Lock옵션을 비활성화] 오브젝트의 LegalHold설정 조회 실패 확인")
	public void test_object_lock_get_legal_hold_invalid_bucket() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(BucketName);

		var Key = "file1";
		Client.putObject(BucketName, Key, "abc");

		var e = assertThrows(AmazonServiceException.class, () -> Client
				.getObjectLegalHold(new GetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key)));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(400, StatusCode);
		assertEquals(MainData.InvalidRequest, ErrorCode);
	}

	@Test
	@DisplayName("test_object_lock_delete_object_with_legal_hold_on")
	@Tag("LegalHold")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 LegalHold가 활성화되어 있을 경우 오브젝트 삭제 실패 확인")
	public void test_object_lock_delete_object_with_legal_hold_on() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Key = "file1";
		var PutResponse = Client.putObject(BucketName, Key, "abc");

		var LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.ON);
		Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold));

		var e = assertThrows(AmazonServiceException.class,
				() -> Client.deleteVersion(BucketName, Key, PutResponse.getVersionId()));
		var StatusCode = e.getStatusCode();
		var ErrorCode = e.getErrorCode();
		assertEquals(403, StatusCode);
		assertEquals(MainData.AccessDenied, ErrorCode);

		LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.OFF);
		Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold));
	}

	@Test
	@DisplayName("test_object_lock_delete_object_with_legal_hold_off")
	@Tag("LegalHold")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 LegalHold가 비활성화되어 있을 경우 오브젝트 삭제 확인")
	public void test_object_lock_delete_object_with_legal_hold_off() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Key = "file1";
		var PutResponse = Client.putObject(BucketName, Key, "abc");

		var LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.OFF);
		Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold));

		Client.deleteVersion(BucketName, Key, PutResponse.getVersionId());
	}

	@Test
	@DisplayName("test_object_lock_get_obj_metadata")
	@Tag("LegalHold")
	//@Tag("[버킷의 Lock옵션을 활성화] 오브젝트의 LegalHold와 Lock유지기한 설정이 모두 적용되는지 메타데이터를 통해 확인")
	public void test_object_lock_get_obj_metadata() {
		var BucketName = GetNewBucketName();
		var Client = GetClient();
		Client.createBucket(new CreateBucketRequest(BucketName).withObjectLockEnabledForBucket(true));

		var Key = "file1";
		var PutResponse = Client.putObject(BucketName, Key, "abc");

		var LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.ON);
		Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold));

		var Retention = new ObjectLockRetention().withMode(ObjectLockRetentionMode.GOVERNANCE)
				.withRetainUntilDate(new Calendar.Builder().setDate(2030, 1, 1)
						.setTimeZone(TimeZone.getTimeZone(("GMT"))).build().getTime());
		Client.setObjectRetention(
				new SetObjectRetentionRequest().withBucketName(BucketName).withKey(Key).withRetention(Retention));

		var Response = Client.getObjectMetadata(BucketName, Key);
		assertEquals(Retention.getMode(), Response.getObjectLockMode());
		assertEquals(Retention.getRetainUntilDate(), Response.getObjectLockRetainUntilDate());
		assertEquals(LegalHold.getStatus(), Response.getObjectLockLegalHoldStatus());

		LegalHold = new ObjectLockLegalHold().withStatus(ObjectLockLegalHoldStatus.OFF);
		Client.setObjectLegalHold(
				new SetObjectLegalHoldRequest().withBucketName(BucketName).withKey(Key).withLegalHold(LegalHold));
		Client.deleteVersion(new DeleteVersionRequest(BucketName, Key, PutResponse.getVersionId())
				.withBypassGovernanceRetention(true));
	}
}
