"""User credential and grant data."""

from __future__ import annotations

from dataclasses import dataclass, field


@dataclass
class UserData:
    display_name: str = ""
    id: str = ""
    account_id: str = ""
    email: str = ""
    access_key: str = ""
    secret_key: str = ""
    kms: str = ""
    x_auth_token: str = ""

    @property
    def expected_owner_id(self) -> str:
        """x-amz-expected-bucket-owner 등 버킷 소유자 검증에 사용하는 아이디.

        AWS는 12자리 계정 아이디를 요구하므로 설정에 AccountId가 있으면 그 값을,
        없으면(ksan 등) 소유자 아이디를 그대로 사용한다.
        """
        return self.account_id.strip() or self.id

    def to_grantee(self) -> dict:
        return {"ID": self.id, "Type": "CanonicalUser"}

    def to_grant(self, permission: str) -> dict:
        return {"Grantee": self.to_grantee(), "Permission": permission}

    def to_owner(self) -> dict:
        return {"ID": self.id, "DisplayName": self.display_name}
