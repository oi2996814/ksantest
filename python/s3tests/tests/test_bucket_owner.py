"""BucketOwner tests ported from Java testV2/BucketOwner.java."""

from __future__ import annotations

import pytest

from s3tests.data import main_data as md
from s3tests.test_base import S3TestBase


class TestBucketOwner(S3TestBase):
    @pytest.mark.tag("Check")
    def test_bucket_owner_head_bucket(self):
        client = self.get_client()
        bucket_name = self.create_bucket(client, 1)

        client.head_bucket(Bucket=bucket_name, ExpectedBucketOwner=self.get_owner())

    @pytest.mark.tag("ERROR")
    def test_bucket_owner_head_bucket_mismatch(self):
        client = self.get_client()
        bucket_name = self.create_bucket(client, 2)

        self.assert_client_error(
            lambda: client.head_bucket(Bucket=bucket_name, ExpectedBucketOwner=self.get_wrong_owner()),
            403,
            md.ACCESS_DENIED,
        )

    @pytest.mark.tag("Check")
    def test_bucket_owner_list_objects_v2(self):
        key = "test_bucket_owner_list_objects_v2"
        client = self.get_client()
        bucket_name = self.create_objects(client, 3, key)

        response = client.list_objects_v2(Bucket=bucket_name, ExpectedBucketOwner=self.get_owner())
        assert response["KeyCount"] == 1

    @pytest.mark.tag("ERROR")
    def test_bucket_owner_list_objects_v2_mismatch(self):
        key = "test_bucket_owner_list_objects_v2_mismatch"
        client = self.get_client()
        bucket_name = self.create_objects(client, 4, key)

        self.assert_client_error(
            lambda: client.list_objects_v2(Bucket=bucket_name, ExpectedBucketOwner=self.get_wrong_owner()),
            403,
            md.ACCESS_DENIED,
        )

    @pytest.mark.tag("Check")
    def test_bucket_owner_put_object(self):
        key = "test_bucket_owner_put_object"
        client = self.get_client()
        bucket_name = self.create_bucket(client, 5)

        client.put_object(
            Bucket=bucket_name,
            Key=key,
            Body=key.encode("utf-8"),
            ExpectedBucketOwner=self.get_owner(),
        )

        self.succeed_get_object(client, bucket_name, key, key)

    @pytest.mark.tag("ERROR")
    def test_bucket_owner_put_object_mismatch(self):
        key = "test_bucket_owner_put_object_mismatch"
        client = self.get_client()
        bucket_name = self.create_bucket(client, 6)

        self.assert_client_error(
            lambda: client.put_object(
                Bucket=bucket_name,
                Key=key,
                Body=key.encode("utf-8"),
                ExpectedBucketOwner=self.get_wrong_owner(),
            ),
            403,
            md.ACCESS_DENIED,
        )

    @pytest.mark.tag("Check")
    def test_bucket_owner_get_object(self):
        key = "test_bucket_owner_get_object"
        client = self.get_client()
        bucket_name = self.create_objects(client, 7, key)

        response = client.get_object(Bucket=bucket_name, Key=key, ExpectedBucketOwner=self.get_owner())
        assert self.get_body(response) == key

    @pytest.mark.tag("ERROR")
    def test_bucket_owner_get_object_mismatch(self):
        key = "test_bucket_owner_get_object_mismatch"
        client = self.get_client()
        bucket_name = self.create_objects(client, 8, key)

        self.assert_client_error(
            lambda: client.get_object(Bucket=bucket_name, Key=key, ExpectedBucketOwner=self.get_wrong_owner()),
            403,
            md.ACCESS_DENIED,
        )

    @pytest.mark.tag("Check")
    def test_bucket_owner_delete_object(self):
        key = "test_bucket_owner_delete_object"
        client = self.get_client()
        bucket_name = self.create_objects(client, 9, key)

        client.delete_object(Bucket=bucket_name, Key=key, ExpectedBucketOwner=self.get_owner())

        self.failed_get_object(client, bucket_name, key, 404, md.NO_SUCH_KEY)

    @pytest.mark.tag("ERROR")
    def test_bucket_owner_delete_object_mismatch(self):
        key = "test_bucket_owner_delete_object_mismatch"
        client = self.get_client()
        bucket_name = self.create_objects(client, 10, key)

        self.assert_client_error(
            lambda: client.delete_object(Bucket=bucket_name, Key=key, ExpectedBucketOwner=self.get_wrong_owner()),
            403,
            md.ACCESS_DENIED,
        )

        self.succeed_get_object(client, bucket_name, key, key)

    @pytest.mark.tag("Check")
    def test_source_bucket_owner_copy_object(self):
        source = "test_source_bucket_owner_copy_object_source"
        target = "test_source_bucket_owner_copy_object_target"
        client = self.get_client()
        source_bucket = self.create_objects(client, 11, source)
        target_bucket = self.create_bucket(client, 11)

        client.copy_object(
            Bucket=target_bucket,
            Key=target,
            CopySource={"Bucket": source_bucket, "Key": source},
            ExpectedBucketOwner=self.get_owner(),
            ExpectedSourceBucketOwner=self.get_owner(),
        )

        self.succeed_get_object(client, target_bucket, target, source)

    @pytest.mark.tag("ERROR")
    def test_source_bucket_owner_copy_object_mismatch(self):
        source = "test_source_bucket_owner_copy_object_mismatch_source"
        target = "test_source_bucket_owner_copy_object_mismatch_target"
        client = self.get_client()
        source_bucket = self.create_objects(client, 12, source)
        target_bucket = self.create_bucket(client, 12)

        self.assert_client_error(
            lambda: client.copy_object(
                Bucket=target_bucket,
                Key=target,
                CopySource={"Bucket": source_bucket, "Key": source},
                ExpectedBucketOwner=self.get_owner(),
                ExpectedSourceBucketOwner=self.get_wrong_owner(),
            ),
            403,
            md.ACCESS_DENIED,
        )

        self.failed_get_object(client, target_bucket, target, 404, md.NO_SUCH_KEY)

    @pytest.mark.tag("ERROR")
    def test_bucket_owner_copy_object_mismatch(self):
        source = "test_bucket_owner_copy_object_mismatch_source"
        target = "test_bucket_owner_copy_object_mismatch_target"
        client = self.get_client()
        source_bucket = self.create_objects(client, 13, source)
        target_bucket = self.create_bucket(client, 13)

        self.assert_client_error(
            lambda: client.copy_object(
                Bucket=target_bucket,
                Key=target,
                CopySource={"Bucket": source_bucket, "Key": source},
                ExpectedBucketOwner=self.get_wrong_owner(),
                ExpectedSourceBucketOwner=self.get_owner(),
            ),
            403,
            md.ACCESS_DENIED,
        )

        self.failed_get_object(client, target_bucket, target, 404, md.NO_SUCH_KEY)

    @pytest.mark.tag("Check")
    def test_source_bucket_owner_upload_part_copy(self):
        source = "test_source_bucket_owner_upload_part_copy_source"
        target = "test_source_bucket_owner_upload_part_copy_target"
        client = self.get_client()
        source_bucket = self.create_objects(client, 14, source)
        target_bucket = self.create_bucket(client, 14)

        upload_id = client.create_multipart_upload(Bucket=target_bucket, Key=target)["UploadId"]

        response = client.upload_part_copy(
            Bucket=target_bucket,
            Key=target,
            UploadId=upload_id,
            PartNumber=1,
            CopySource={"Bucket": source_bucket, "Key": source},
            ExpectedBucketOwner=self.get_owner(),
            ExpectedSourceBucketOwner=self.get_owner(),
        )
        assert response["CopyPartResult"]["ETag"]

        client.abort_multipart_upload(Bucket=target_bucket, Key=target, UploadId=upload_id)

    @pytest.mark.tag("ERROR")
    def test_source_bucket_owner_upload_part_copy_mismatch(self):
        source = "test_source_bucket_owner_upload_part_copy_mismatch_source"
        target = "test_source_bucket_owner_upload_part_copy_mismatch_target"
        client = self.get_client()
        source_bucket = self.create_objects(client, 15, source)
        target_bucket = self.create_bucket(client, 15)

        upload_id = client.create_multipart_upload(Bucket=target_bucket, Key=target)["UploadId"]

        self.assert_client_error(
            lambda: client.upload_part_copy(
                Bucket=target_bucket,
                Key=target,
                UploadId=upload_id,
                PartNumber=1,
                CopySource={"Bucket": source_bucket, "Key": source},
                ExpectedBucketOwner=self.get_owner(),
                ExpectedSourceBucketOwner=self.get_wrong_owner(),
            ),
            403,
            md.ACCESS_DENIED,
        )

        client.abort_multipart_upload(Bucket=target_bucket, Key=target, UploadId=upload_id)

    @pytest.mark.tag("ERROR")
    def test_bucket_owner_upload_part_copy_mismatch(self):
        source = "test_bucket_owner_upload_part_copy_mismatch_source"
        target = "test_bucket_owner_upload_part_copy_mismatch_target"
        client = self.get_client()
        source_bucket = self.create_objects(client, 16, source)
        target_bucket = self.create_bucket(client, 16)

        upload_id = client.create_multipart_upload(Bucket=target_bucket, Key=target)["UploadId"]

        self.assert_client_error(
            lambda: client.upload_part_copy(
                Bucket=target_bucket,
                Key=target,
                UploadId=upload_id,
                PartNumber=1,
                CopySource={"Bucket": source_bucket, "Key": source},
                ExpectedBucketOwner=self.get_wrong_owner(),
                ExpectedSourceBucketOwner=self.get_owner(),
            ),
            403,
            md.ACCESS_DENIED,
        )

        client.abort_multipart_upload(Bucket=target_bucket, Key=target, UploadId=upload_id)
