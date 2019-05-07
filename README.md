# s3backup [![pipeline](https://gitlab.com/steve.stonehouse/s3backup/badges/master/pipeline.svg)](https://gitlab.com/steve-stonehouse/s3backup/commits/master)
A simple backup plugin for Spigot that uploads to s3.

## Requirements
 - An AWS account
 - An IAM user with programmatic access keys
 - An AWS s3 bucket
 - An IAM group that the IAM user is a member of
 - A policy assigned to the IAM user's group to grant access to the s3 bucket

## Plugin configuration
This configuration will produce a `zip` backup of the server directory every 4 hours and upload it to your s3 bucket with the path `my-backup-bucket/s3backup/00-00-0000-00-00-00.zip`.

The backup process will also remove any old backups beyond the `max-backups`. This can be set to `0` if you would like to manage this yourself.
```
backup-date-format: dd-MM-yyyy-HH-mm-ss
local-prefix: s3backup
backup-interval: 240
debug: false
max-backups: 60
region: eu-west-1
access-key-id: AKIAIOSFODNN7EXAMPLE
access-key-secret: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
bucket: my-backup-bucket
prefix: s3backup/
```

## Commands
- `/s3backup` - Initates a manual backup.
- `/s3backup list` - Lists the backups in s3.
- `/s3backup get [backup]` - Downloads the specified backup to the `local-prefix` directory specified in the configuration.
- `/s3backup delete [backup]` - Deletes the specified backup in s3.

All commands auto-complete including the `get` and `delete` commands to fill in backup names.

## Permissions
`s3backup.use` - Allows the use of the `/s3backup` commands and sub-commands.

## Example IAM policy
This policy will allow `list`, `get`, `put` and `delete` requests on the s3 bucket `my-backup-bucket` under the `s3backup` prefix. This policy is all that is required for the s3backup plugin.

You will not need to retrict the `ListBucket` action if you are not using a `prefix`.
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:ListBucket"
            ],
            "Condition": {
                "StringLike": {
                    "s3:prefix": [
                        "s3backup/"
                    ]
                }
            },
            "Resource": [
                "arn:aws:s3:::my-backup-bucket*"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject"
            ],
            "Resource": [
                "arn:aws:s3:::my-backup-bucket/s3backup/*"
            ]
        }
    ]
}
```