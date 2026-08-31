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
using s3tests.Utils;
using System;
using System.Net;
using Xunit;

namespace s3tests.Test
{
	public class BucketOwner : TestBase
	{
		public BucketOwner(Xunit.Abstractions.ITestOutputHelper Output) => this.Output = Output;

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "Check")]
		[Trait(MainData.Explanation, "올바른 소유자 아이디로 HeadBucket이 가능한지 확인")]
		[Trait(MainData.Result, MainData.ResultSuccess)]
		public void TestBucketOwnerHeadBucket()
		{
			TestId = 1;
			var client = GetClient();
			var bucketName = GetNewBucket(client);

			client.HeadBucket(bucketName, expectedBucketOwner: Owner);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "ERROR")]
		[Trait(MainData.Explanation, "잘못된 소유자 아이디로 HeadBucket을 실패하는지 확인")]
		[Trait(MainData.Result, MainData.ResultFailure)]
		public void TestBucketOwnerHeadBucketMismatch()
		{
			TestId = 2;
			var client = GetClient();
			var bucketName = GetNewBucket(client);

			var e = Assert.Throws<AggregateException>(() => client.HeadBucket(bucketName, expectedBucketOwner: WrongOwner));
			Assert.Equal(HttpStatusCode.Forbidden, GetStatus(e));
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "Check")]
		[Trait(MainData.Explanation, "올바른 소유자 아이디로 ListObjectsV2가 가능한지 확인")]
		[Trait(MainData.Result, MainData.ResultSuccess)]
		public void TestBucketOwnerListObjectsV2()
		{
			TestId = 3;
			var key = "TestBucketOwnerListObjectsV2";
			var bucketName = SetupObjects([key]);
			var client = GetClient();

			var response = client.ListObjectsV2(bucketName, expectedBucketOwner: Owner);
			Assert.Equal(1, response.KeyCount);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "ERROR")]
		[Trait(MainData.Explanation, "잘못된 소유자 아이디로 ListObjectsV2를 실패하는지 확인")]
		[Trait(MainData.Result, MainData.ResultFailure)]
		public void TestBucketOwnerListObjectsV2Mismatch()
		{
			TestId = 4;
			var key = "TestBucketOwnerListObjectsV2Mismatch";
			var bucketName = SetupObjects([key]);
			var client = GetClient();

			var e = Assert.Throws<AggregateException>(() => client.ListObjectsV2(bucketName, expectedBucketOwner: WrongOwner));
			Assert.Equal(HttpStatusCode.Forbidden, GetStatus(e));
			Assert.Equal(MainData.ACCESS_DENIED, GetErrorCode(e));
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "Check")]
		[Trait(MainData.Explanation, "올바른 소유자 아이디로 오브젝트 업로드가 가능한지 확인")]
		[Trait(MainData.Result, MainData.ResultSuccess)]
		public void TestBucketOwnerPutObject()
		{
			TestId = 5;
			var key = "TestBucketOwnerPutObject";
			var client = GetClient();
			var bucketName = GetNewBucket(client);

			client.PutObject(bucketName, key, body: key, expectedBucketOwner: Owner);

			SucceedGetObject(client, bucketName, key, key);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "ERROR")]
		[Trait(MainData.Explanation, "잘못된 소유자 아이디로 오브젝트 업로드를 실패하는지 확인")]
		[Trait(MainData.Result, MainData.ResultFailure)]
		public void TestBucketOwnerPutObjectMismatch()
		{
			TestId = 6;
			var key = "TestBucketOwnerPutObjectMismatch";
			var client = GetClient();
			var bucketName = GetNewBucket(client);

			var e = Assert.Throws<AggregateException>(() => client.PutObject(bucketName, key, body: key, expectedBucketOwner: WrongOwner));
			Assert.Equal(HttpStatusCode.Forbidden, GetStatus(e));
			Assert.Equal(MainData.ACCESS_DENIED, GetErrorCode(e));
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "Check")]
		[Trait(MainData.Explanation, "올바른 소유자 아이디로 오브젝트 다운로드가 가능한지 확인")]
		[Trait(MainData.Result, MainData.ResultSuccess)]
		public void TestBucketOwnerGetObject()
		{
			TestId = 7;
			var key = "TestBucketOwnerGetObject";
			var bucketName = SetupObjects([key]);
			var client = GetClient();

			using var response = client.GetObject(bucketName, key, expectedBucketOwner: Owner);
			Assert.Equal(key, S3Utils.GetBody(response));
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "ERROR")]
		[Trait(MainData.Explanation, "잘못된 소유자 아이디로 오브젝트 다운로드를 실패하는지 확인")]
		[Trait(MainData.Result, MainData.ResultFailure)]
		public void TestBucketOwnerGetObjectMismatch()
		{
			TestId = 8;
			var key = "TestBucketOwnerGetObjectMismatch";
			var bucketName = SetupObjects([key]);
			var client = GetClient();

			var e = Assert.Throws<AggregateException>(() => client.GetObject(bucketName, key, expectedBucketOwner: WrongOwner));
			Assert.Equal(HttpStatusCode.Forbidden, GetStatus(e));
			Assert.Equal(MainData.ACCESS_DENIED, GetErrorCode(e));
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "Check")]
		[Trait(MainData.Explanation, "올바른 소유자 아이디로 오브젝트 삭제가 가능한지 확인")]
		[Trait(MainData.Result, MainData.ResultSuccess)]
		public void TestBucketOwnerDeleteObject()
		{
			TestId = 9;
			var key = "TestBucketOwnerDeleteObject";
			var bucketName = SetupObjects([key]);
			var client = GetClient();

			client.DeleteObject(bucketName, key, expectedBucketOwner: Owner);

			FailedGetObject(client, bucketName, key, HttpStatusCode.NotFound, MainData.NO_SUCH_KEY);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "ERROR")]
		[Trait(MainData.Explanation, "잘못된 소유자 아이디로 오브젝트 삭제를 실패하는지 확인")]
		[Trait(MainData.Result, MainData.ResultFailure)]
		public void TestBucketOwnerDeleteObjectMismatch()
		{
			TestId = 10;
			var key = "TestBucketOwnerDeleteObjectMismatch";
			var bucketName = SetupObjects([key]);
			var client = GetClient();

			var e = Assert.Throws<AggregateException>(() => client.DeleteObject(bucketName, key, expectedBucketOwner: WrongOwner));
			Assert.Equal(HttpStatusCode.Forbidden, GetStatus(e));
			Assert.Equal(MainData.ACCESS_DENIED, GetErrorCode(e));

			SucceedGetObject(client, bucketName, key, key);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "Check")]
		[Trait(MainData.Explanation, "올바른 소스/대상 소유자 아이디로 오브젝트 복사가 가능한지 확인")]
		[Trait(MainData.Result, MainData.ResultSuccess)]
		public void TestSourceBucketOwnerCopyObject()
		{
			TestId = 11;
			var source = "TestSourceBucketOwnerCopyObjectSource";
			var target = "TestSourceBucketOwnerCopyObjectTarget";
			var sourceBucket = SetupObjects([source]);
			var client = GetClient();
			var targetBucket = GetNewBucket(client);

			client.CopyObject(sourceBucket, source, targetBucket, target,
				expectedBucketOwner: Owner, expectedSourceBucketOwner: Owner);

			SucceedGetObject(client, targetBucket, target, source);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "ERROR")]
		[Trait(MainData.Explanation, "잘못된 소스 소유자 아이디로 오브젝트 복사를 실패하는지 확인")]
		[Trait(MainData.Result, MainData.ResultFailure)]
		public void TestSourceBucketOwnerCopyObjectMismatch()
		{
			TestId = 12;
			var source = "TestSourceBucketOwnerCopyObjectMismatchSource";
			var target = "TestSourceBucketOwnerCopyObjectMismatchTarget";
			var sourceBucket = SetupObjects([source]);
			var client = GetClient();
			var targetBucket = GetNewBucket(client);

			var e = Assert.Throws<AggregateException>(() => client.CopyObject(sourceBucket, source, targetBucket, target,
				expectedBucketOwner: Owner, expectedSourceBucketOwner: WrongOwner));
			Assert.Equal(HttpStatusCode.Forbidden, GetStatus(e));
			Assert.Equal(MainData.ACCESS_DENIED, GetErrorCode(e));

			FailedGetObject(client, targetBucket, target, HttpStatusCode.NotFound, MainData.NO_SUCH_KEY);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "ERROR")]
		[Trait(MainData.Explanation, "잘못된 대상 소유자 아이디로 오브젝트 복사를 실패하는지 확인")]
		[Trait(MainData.Result, MainData.ResultFailure)]
		public void TestBucketOwnerCopyObjectMismatch()
		{
			TestId = 13;
			var source = "TestBucketOwnerCopyObjectMismatchSource";
			var target = "TestBucketOwnerCopyObjectMismatchTarget";
			var sourceBucket = SetupObjects([source]);
			var client = GetClient();
			var targetBucket = GetNewBucket(client);

			var e = Assert.Throws<AggregateException>(() => client.CopyObject(sourceBucket, source, targetBucket, target,
				expectedBucketOwner: WrongOwner, expectedSourceBucketOwner: Owner));
			Assert.Equal(HttpStatusCode.Forbidden, GetStatus(e));
			Assert.Equal(MainData.ACCESS_DENIED, GetErrorCode(e));

			FailedGetObject(client, targetBucket, target, HttpStatusCode.NotFound, MainData.NO_SUCH_KEY);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "Check")]
		[Trait(MainData.Explanation, "올바른 소스/대상 소유자 아이디로 파트 복사가 가능한지 확인")]
		[Trait(MainData.Result, MainData.ResultSuccess)]
		public void TestSourceBucketOwnerUploadPartCopy()
		{
			TestId = 14;
			var source = "TestSourceBucketOwnerUploadPartCopySource";
			var target = "TestSourceBucketOwnerUploadPartCopyTarget";
			var sourceBucket = SetupObjects([source]);
			var client = GetClient();
			var targetBucket = GetNewBucket(client);

			var uploadId = client.InitiateMultipartUpload(targetBucket, target).UploadId;

			var response = client.CopyPart(sourceBucket, source, targetBucket, target, uploadId, 1, 0, source.Length - 1,
				expectedBucketOwner: Owner, expectedSourceBucketOwner: Owner);
			Assert.NotNull(response.ETag);

			client.AbortMultipartUpload(targetBucket, target, uploadId);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "ERROR")]
		[Trait(MainData.Explanation, "잘못된 소스 소유자 아이디로 파트 복사를 실패하는지 확인")]
		[Trait(MainData.Result, MainData.ResultFailure)]
		public void TestSourceBucketOwnerUploadPartCopyMismatch()
		{
			TestId = 15;
			var source = "TestSourceBucketOwnerUploadPartCopyMismatchSource";
			var target = "TestSourceBucketOwnerUploadPartCopyMismatchTarget";
			var sourceBucket = SetupObjects([source]);
			var client = GetClient();
			var targetBucket = GetNewBucket(client);

			var uploadId = client.InitiateMultipartUpload(targetBucket, target).UploadId;

			var e = Assert.Throws<AggregateException>(() => client.CopyPart(sourceBucket, source, targetBucket, target,
				uploadId, 1, 0, source.Length - 1,
				expectedBucketOwner: Owner, expectedSourceBucketOwner: WrongOwner));
			Assert.Equal(HttpStatusCode.Forbidden, GetStatus(e));
			Assert.Equal(MainData.ACCESS_DENIED, GetErrorCode(e));

			client.AbortMultipartUpload(targetBucket, target, uploadId);
		}

		[Fact]
		[Trait(MainData.Major, "BucketOwner")]
		[Trait(MainData.Minor, "ERROR")]
		[Trait(MainData.Explanation, "잘못된 대상 소유자 아이디로 파트 복사를 실패하는지 확인")]
		[Trait(MainData.Result, MainData.ResultFailure)]
		public void TestBucketOwnerUploadPartCopyMismatch()
		{
			TestId = 16;
			var source = "TestBucketOwnerUploadPartCopyMismatchSource";
			var target = "TestBucketOwnerUploadPartCopyMismatchTarget";
			var sourceBucket = SetupObjects([source]);
			var client = GetClient();
			var targetBucket = GetNewBucket(client);

			var uploadId = client.InitiateMultipartUpload(targetBucket, target).UploadId;

			var e = Assert.Throws<AggregateException>(() => client.CopyPart(sourceBucket, source, targetBucket, target,
				uploadId, 1, 0, source.Length - 1,
				expectedBucketOwner: WrongOwner, expectedSourceBucketOwner: Owner));
			Assert.Equal(HttpStatusCode.Forbidden, GetStatus(e));
			Assert.Equal(MainData.ACCESS_DENIED, GetErrorCode(e));

			client.AbortMultipartUpload(targetBucket, target, uploadId);
		}
	}
}
