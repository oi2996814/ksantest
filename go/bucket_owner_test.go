package s3tests

import (
	"bytes"
	"context"
	"errors"
	"fmt"
	"io"
	"testing"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	smithyhttp "github.com/aws/smithy-go/transport/http"
)

// 응답 코드만 확인한다. HeadBucket 등 본문이 없는 요청은 에러 코드를 알 수 없다.
func assertS3Status(t *testing.T, err error, status int) {
	t.Helper()
	if err == nil {
		t.Fatal("operation succeeded, want S3 API error")
	}
	var responseErr *smithyhttp.ResponseError
	if !errors.As(err, &responseErr) {
		t.Fatalf("error type = %T, want HTTP response error: %v", err, err)
	}
	if got := responseErr.HTTPStatusCode(); got != status {
		t.Fatalf("HTTP status = %d, want %d", got, status)
	}
}

// 올바른 소유자 아이디로 HeadBucket이 가능한지 확인하는 테스트
func TestBucketOwnerHeadBucket(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 1)

	if _, err := s.client.HeadBucket(context.Background(), &s3.HeadBucketInput{Bucket: aws.String(b), ExpectedBucketOwner: s.owner()}); err != nil {
		t.Fatal(err)
	}
}

// 잘못된 소유자 아이디로 HeadBucket을 실패하는지 확인하는 테스트
func TestBucketOwnerHeadBucketMismatch(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 2)

	_, err := s.client.HeadBucket(context.Background(), &s3.HeadBucketInput{Bucket: aws.String(b), ExpectedBucketOwner: s.wrongOwner()})
	assertS3Status(t, err, 403)
}

// 올바른 소유자 아이디로 ListObjectsV2가 가능한지 확인하는 테스트
func TestBucketOwnerListObjectsV2(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 3)
	key := "TestBucketOwnerListObjectsV2"
	put(t, s, b, key, key, nil)

	out, err := s.client.ListObjectsV2(context.Background(), &s3.ListObjectsV2Input{Bucket: aws.String(b), ExpectedBucketOwner: s.owner()})
	if err != nil {
		t.Fatal(err)
	}
	if got := aws.ToInt32(out.KeyCount); got != 1 {
		t.Fatalf("KeyCount = %d, want 1", got)
	}
}

// 잘못된 소유자 아이디로 ListObjectsV2를 실패하는지 확인하는 테스트
func TestBucketOwnerListObjectsV2Mismatch(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 4)
	key := "TestBucketOwnerListObjectsV2Mismatch"
	put(t, s, b, key, key, nil)

	_, err := s.client.ListObjectsV2(context.Background(), &s3.ListObjectsV2Input{Bucket: aws.String(b), ExpectedBucketOwner: s.wrongOwner()})
	assertS3Error(t, err, 403, "AccessDenied")
}

// 올바른 소유자 아이디로 오브젝트 업로드가 가능한지 확인하는 테스트
func TestBucketOwnerPutObject(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 5)
	key := "TestBucketOwnerPutObject"

	if _, err := s.client.PutObject(context.Background(), &s3.PutObjectInput{Bucket: aws.String(b), Key: aws.String(key), Body: bytes.NewReader([]byte(key)), ExpectedBucketOwner: s.owner()}); err != nil {
		t.Fatal(err)
	}
	if got := read(t, s, b, key); got != key {
		t.Fatalf("body = %q, want %q", got, key)
	}
}

// 잘못된 소유자 아이디로 오브젝트 업로드를 실패하는지 확인하는 테스트
func TestBucketOwnerPutObjectMismatch(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 6)
	key := "TestBucketOwnerPutObjectMismatch"

	_, err := s.client.PutObject(context.Background(), &s3.PutObjectInput{Bucket: aws.String(b), Key: aws.String(key), Body: bytes.NewReader([]byte(key)), ExpectedBucketOwner: s.wrongOwner()})
	assertS3Error(t, err, 403, "AccessDenied")
}

// 올바른 소유자 아이디로 오브젝트 다운로드가 가능한지 확인하는 테스트
func TestBucketOwnerGetObject(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 7)
	key := "TestBucketOwnerGetObject"
	put(t, s, b, key, key, nil)

	out, err := s.client.GetObject(context.Background(), &s3.GetObjectInput{Bucket: aws.String(b), Key: aws.String(key), ExpectedBucketOwner: s.owner()})
	if err != nil {
		t.Fatal(err)
	}
	defer out.Body.Close()
	body, err := io.ReadAll(out.Body)
	if err != nil {
		t.Fatal(err)
	}
	if got := string(body); got != key {
		t.Fatalf("body = %q, want %q", got, key)
	}
}

// 잘못된 소유자 아이디로 오브젝트 다운로드를 실패하는지 확인하는 테스트
func TestBucketOwnerGetObjectMismatch(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 8)
	key := "TestBucketOwnerGetObjectMismatch"
	put(t, s, b, key, key, nil)

	_, err := s.client.GetObject(context.Background(), &s3.GetObjectInput{Bucket: aws.String(b), Key: aws.String(key), ExpectedBucketOwner: s.wrongOwner()})
	assertS3Error(t, err, 403, "AccessDenied")
}

// 올바른 소유자 아이디로 오브젝트 삭제가 가능한지 확인하는 테스트
func TestBucketOwnerDeleteObject(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 9)
	key := "TestBucketOwnerDeleteObject"
	put(t, s, b, key, key, nil)

	if _, err := s.client.DeleteObject(context.Background(), &s3.DeleteObjectInput{Bucket: aws.String(b), Key: aws.String(key), ExpectedBucketOwner: s.owner()}); err != nil {
		t.Fatal(err)
	}
	_, err := s.client.GetObject(context.Background(), &s3.GetObjectInput{Bucket: aws.String(b), Key: aws.String(key)})
	assertS3Error(t, err, 404, "NoSuchKey")
}

// 잘못된 소유자 아이디로 오브젝트 삭제를 실패하는지 확인하는 테스트
func TestBucketOwnerDeleteObjectMismatch(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	b := s.bucket(t, 10)
	key := "TestBucketOwnerDeleteObjectMismatch"
	put(t, s, b, key, key, nil)

	_, err := s.client.DeleteObject(context.Background(), &s3.DeleteObjectInput{Bucket: aws.String(b), Key: aws.String(key), ExpectedBucketOwner: s.wrongOwner()})
	assertS3Error(t, err, 403, "AccessDenied")

	if got := read(t, s, b, key); got != key {
		t.Fatalf("body = %q, want %q", got, key)
	}
}

// 올바른 소스/대상 소유자 아이디로 오브젝트 복사가 가능한지 확인하는 테스트
func TestSourceBucketOwnerCopyObject(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	sourceBucket := s.bucket(t, 11)
	targetBucket := s.bucket(t, 11)
	source, target := "TestSourceBucketOwnerCopyObjectSource", "TestSourceBucketOwnerCopyObjectTarget"
	put(t, s, sourceBucket, source, source, nil)

	if _, err := s.client.CopyObject(context.Background(), &s3.CopyObjectInput{
		Bucket: aws.String(targetBucket), Key: aws.String(target), CopySource: copySource(sourceBucket, source, ""),
		ExpectedBucketOwner:       s.owner(),
		ExpectedSourceBucketOwner: s.owner(),
	}); err != nil {
		t.Fatal(err)
	}
	assertCopied(t, s.client, targetBucket, target, source, nil)
}

// 잘못된 소스 소유자 아이디로 오브젝트 복사를 실패하는지 확인하는 테스트
func TestSourceBucketOwnerCopyObjectMismatch(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	sourceBucket := s.bucket(t, 12)
	targetBucket := s.bucket(t, 12)
	source, target := "TestSourceBucketOwnerCopyObjectMismatchSource", "TestSourceBucketOwnerCopyObjectMismatchTarget"
	put(t, s, sourceBucket, source, source, nil)

	_, err := s.client.CopyObject(context.Background(), &s3.CopyObjectInput{
		Bucket: aws.String(targetBucket), Key: aws.String(target), CopySource: copySource(sourceBucket, source, ""),
		ExpectedBucketOwner:       s.owner(),
		ExpectedSourceBucketOwner: s.wrongOwner(),
	})
	assertS3Error(t, err, 403, "AccessDenied")

	_, err = s.client.GetObject(context.Background(), &s3.GetObjectInput{Bucket: aws.String(targetBucket), Key: aws.String(target)})
	assertS3Error(t, err, 404, "NoSuchKey")
}

// 잘못된 대상 소유자 아이디로 오브젝트 복사를 실패하는지 확인하는 테스트
func TestBucketOwnerCopyObjectMismatch(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	sourceBucket := s.bucket(t, 13)
	targetBucket := s.bucket(t, 13)
	source, target := "TestBucketOwnerCopyObjectMismatchSource", "TestBucketOwnerCopyObjectMismatchTarget"
	put(t, s, sourceBucket, source, source, nil)

	_, err := s.client.CopyObject(context.Background(), &s3.CopyObjectInput{
		Bucket: aws.String(targetBucket), Key: aws.String(target), CopySource: copySource(sourceBucket, source, ""),
		ExpectedBucketOwner:       s.wrongOwner(),
		ExpectedSourceBucketOwner: s.owner(),
	})
	assertS3Error(t, err, 403, "AccessDenied")

	_, err = s.client.GetObject(context.Background(), &s3.GetObjectInput{Bucket: aws.String(targetBucket), Key: aws.String(target)})
	assertS3Error(t, err, 404, "NoSuchKey")
}

// 올바른 소스/대상 소유자 아이디로 파트 복사가 가능한지 확인하는 테스트
func TestSourceBucketOwnerUploadPartCopy(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	ctx := context.Background()
	sourceBucket := s.bucket(t, 14)
	targetBucket := s.bucket(t, 14)
	source, target := "TestSourceBucketOwnerUploadPartCopySource", "TestSourceBucketOwnerUploadPartCopyTarget"
	put(t, s, sourceBucket, source, source, nil)

	created, err := s.client.CreateMultipartUpload(ctx, &s3.CreateMultipartUploadInput{Bucket: aws.String(targetBucket), Key: aws.String(target)})
	if err != nil {
		t.Fatal(err)
	}

	copied, err := s.client.UploadPartCopy(ctx, &s3.UploadPartCopyInput{
		Bucket: aws.String(targetBucket), Key: aws.String(target), UploadId: created.UploadId, PartNumber: aws.Int32(1),
		CopySource: copySource(sourceBucket, source, ""), CopySourceRange: aws.String(fmt.Sprintf("bytes=0-%d", len(source)-1)),
		ExpectedBucketOwner:       s.owner(),
		ExpectedSourceBucketOwner: s.owner(),
	})
	if err != nil {
		t.Fatal(err)
	}
	if copied.CopyPartResult == nil || aws.ToString(copied.CopyPartResult.ETag) == "" {
		t.Fatal("CopyPartResult.ETag is empty")
	}

	if _, err := s.client.AbortMultipartUpload(ctx, &s3.AbortMultipartUploadInput{Bucket: aws.String(targetBucket), Key: aws.String(target), UploadId: created.UploadId}); err != nil {
		t.Fatal(err)
	}
}

// 잘못된 소스 소유자 아이디로 파트 복사를 실패하는지 확인하는 테스트
func TestSourceBucketOwnerUploadPartCopyMismatch(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	ctx := context.Background()
	sourceBucket := s.bucket(t, 15)
	targetBucket := s.bucket(t, 15)
	source, target := "TestSourceBucketOwnerUploadPartCopyMismatchSource", "TestSourceBucketOwnerUploadPartCopyMismatchTarget"
	put(t, s, sourceBucket, source, source, nil)

	created, err := s.client.CreateMultipartUpload(ctx, &s3.CreateMultipartUploadInput{Bucket: aws.String(targetBucket), Key: aws.String(target)})
	if err != nil {
		t.Fatal(err)
	}

	_, err = s.client.UploadPartCopy(ctx, &s3.UploadPartCopyInput{
		Bucket: aws.String(targetBucket), Key: aws.String(target), UploadId: created.UploadId, PartNumber: aws.Int32(1),
		CopySource: copySource(sourceBucket, source, ""), CopySourceRange: aws.String(fmt.Sprintf("bytes=0-%d", len(source)-1)),
		ExpectedBucketOwner:       s.owner(),
		ExpectedSourceBucketOwner: s.wrongOwner(),
	})
	assertS3Error(t, err, 403, "AccessDenied")

	if _, err := s.client.AbortMultipartUpload(ctx, &s3.AbortMultipartUploadInput{Bucket: aws.String(targetBucket), Key: aws.String(target), UploadId: created.UploadId}); err != nil {
		t.Fatal(err)
	}
}

// 잘못된 대상 소유자 아이디로 파트 복사를 실패하는지 확인하는 테스트
func TestBucketOwnerUploadPartCopyMismatch(t *testing.T) {
	t.Parallel()
	s := newSuite(t)
	ctx := context.Background()
	sourceBucket := s.bucket(t, 16)
	targetBucket := s.bucket(t, 16)
	source, target := "TestBucketOwnerUploadPartCopyMismatchSource", "TestBucketOwnerUploadPartCopyMismatchTarget"
	put(t, s, sourceBucket, source, source, nil)

	created, err := s.client.CreateMultipartUpload(ctx, &s3.CreateMultipartUploadInput{Bucket: aws.String(targetBucket), Key: aws.String(target)})
	if err != nil {
		t.Fatal(err)
	}

	_, err = s.client.UploadPartCopy(ctx, &s3.UploadPartCopyInput{
		Bucket: aws.String(targetBucket), Key: aws.String(target), UploadId: created.UploadId, PartNumber: aws.Int32(1),
		CopySource: copySource(sourceBucket, source, ""), CopySourceRange: aws.String(fmt.Sprintf("bytes=0-%d", len(source)-1)),
		ExpectedBucketOwner:       s.wrongOwner(),
		ExpectedSourceBucketOwner: s.owner(),
	})
	assertS3Error(t, err, 403, "AccessDenied")

	if _, err := s.client.AbortMultipartUpload(ctx, &s3.AbortMultipartUploadInput{Bucket: aws.String(targetBucket), Key: aws.String(target), UploadId: created.UploadId}); err != nil {
		t.Fatal(err)
	}
}
