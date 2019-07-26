# s3backup [![pipeline](https://gitlab.com/steve.stonehouse/s3backup/badges/master/pipeline.svg)](https://gitlab.com/steve-stonehouse/s3backup/commits/master)
A simple plugin for Spigot that uploads compressed backups of your server to s3.

Also provides ways of downloading the backups for restoration.

## Requirements
 - An AWS account.
 - An AWS s3 bucket.

The AWS resources are not managed by this plugin. They can either be created through the console or via tools such as Terraform. This is to leave the AWS side open ended. You can then configure things such as Emails through SES via bucket event notifications and s3 lifecycles yourself.

It is recommended that default s3 encryption at rest or via a custom KMS key be enabled for the s3 bucket as plugin configurations and data can contain sensitive information.

 ### IAM user credentials
 - An IAM user with programmatic access keys.
 - An IAM group where the IAM user is a member.
 - A policy assigned to the IAM user's group to grant access to the s3 bucket.

### IAM profile (only for servers running on EC2)
- An instance profile attached to the EC2 instance where the server is running.
- A policy assigned to the profile to grant access to the s3 bucket.

## Plugin configuration
This configuration will produce a `zip` backup of the server directory every 4 hours and upload it to your s3 bucket with the path `my-backup-bucket/s3backup/00-00-0000-00-00-00.zip`.

The backup process will also remove any old backups beyond the `max-backups`. This can be set to `0` if you would like to manage this yourself by other means.

File paths can be added to `ignore-files` if you do not want certain files to be contained in the backup. Useful for sensitive information. By default, the s3backup configuration is not included, as it may contain AWS access keys.
```
access-key-id: AKIAIOSFODNN7EXAMPLE
access-key-secret: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
backup-date-format: dd-MM-yyyy-HH-mm-ss
backup-interval: 240
bucket: my-backup-bucket
debug: false
ignore-files:
  - s3backup/config.yml
max-backups: 60
prefix: s3backup/
region: eu-west-1
```

Backup names (including date format) and bucket prefix must only consist of alphanumeric characters, hyphens or underscores. The prefix can also contain forward slashes to denote folders in s3 (useful for seperating multi-server backups in one bucket).

## Commands
- `/s3backup` - Displays command usage.
- `/s3backup backup [name]` - Initiates a manual backup. Optional name to prepend.
- `/s3backup delete [backup]` - Deletes the specified backup in s3.
- `/s3backup get [backup]` - Downloads the specified backup to the local `backup` directory.
- `/s3backup list` - Lists the backups in s3.
- `/s3backup sign [backup]` - Generates a temporary URL to download a backup locally.

All commands auto-complete including the `get` and `delete` commands to fill in backup names.

## Permissions
- `s3backup` - Allows displaying command usage. Granted by any other permission.
- `s3backup.backup` - Allows creation of backups.
- `s3backup.delete` - Allows deletion of backups.
- `s3backup.get` - Allows downloading backups to the server backup directory.
- `s3backup.list` - Allows the listing of backups.
- `s3backup.sign` - Allows downloading backups locally via generating a signed URL.
- `s3backup.*` - Grants all of the above permissions.

## Example IAM policy
This policy will allow `list`, `get`, `put` and `delete` requests on the s3 bucket `my-backup-bucket` under the `s3backup` prefix. These are the only actions required for s3backup.

The resource ARN will need to have the `prefix` appended if used as shown below.
```
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::my-backup-bucket",
                "arn:aws:s3:::my-backup-bucket/*"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject",
                "s3:ListMultipartUploadParts"
            ],
            "Resource": [
                "arn:aws:s3:::my-backup-bucket/s3backup/*"
            ]
        }
    ]
}
```