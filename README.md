# s3backup
A simple plugin for Spigot that uploads compressed backups of your server to s3 or an s3 compatible server.

Also provides ways of downloading the backups for restoration.

## Requirements
 - An AWS account.
 - An AWS s3 bucket.

A 3rd party solution such as [Minio](https://min.io/) can also be used and self hosted. The plugin configuration for this is mostly the same (see [plugin configuration](#plugin-configuration)). You will need a valid SSL certificate for HTTPS configured on your custom endpoint. [Let’s Encrypt](https://letsencrypt.org/) is recommended.

The AWS resources used are not managed by this plugin. They can either be created through the console or via tools such as Terraform. This is to leave the AWS side open ended. You can then configure things such as Emails through SES via bucket event notifications and s3 lifecycles yourself.

It is recommended that default s3 encryption at rest or via a custom KMS key be enabled for the s3 bucket as plugin configurations and data can contain sensitive information.

 ### IAM user credentials
 - An IAM user with programmatic access keys.
 - An IAM group where the IAM user is a member.
 - A policy assigned to the IAM user's group to grant access to the s3 bucket.

### IAM profile (only for servers running on AWS)
- An instance profile attached to the instance where the server is running.
- A policy assigned to the profile to grant access to the s3 bucket.

## Plugin configuration
This configuration will produce a `zip` backup of the server directory every 4 hours and upload it to your s3 bucket with the path `my-backup-bucket/s3backup/00-00-0000-00-00-00.zip`.

The backup process will also remove any old backups beyond the `max-backups`. This can be set to `0` if you would like to manage this yourself by other means.

File paths can be added to `ignore-files` if you do not want certain files to be contained in the backup. Useful for sensitive information. By default, the s3backup configuration is not included, as it may contain AWS access keys.
```
# AWS access keys for authentication
# Leave these blank if you wish to use an instance profile
access-key-id: AKIAIOSFODNN7EXAMPLE
access-key-secret: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY

signed-url-duration: 60

# For use with third party s3 servers such as Minio
custom-endpoint: ''
signer-override: 'AWSS3V4SignerType'
path-style-access: true

# See https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html for formatting options
# Note only hyphens and underscores are permitted alongside alphanumeric characters
backup-date-format: dd-MM-yyyy-HH-mm-ss

# Interval in minutes in which to create an automatic backup
backup-interval: 240

# Name of your s3 bucket
bucket: 'my-backup-bucket'

# Enable this only for debugging. It will print stack traces to the console alongside normal error messages
debug: false

# List of files to omit from a backup. Useful for files with sensitive information
# Files will be ignored if they contain this substring anywhere
ignore-files:
  - s3backup/config.yml
  - s3backup\config.yml

# Maximum backups in s3 before automatically deleting the oldest. Set to 0 to disable this
max-backups: 60

# Optional prefix to prepend to the backup in s3. Useful for having one bucket for multiple servers
# This is not visible to the plugin when listing/getting backups etc
# Forward slashes denote a folder in s3. So a value of 's3backup/' will store all backups in a folder called s3backup
prefix: 's3backup/'

# Region in which your s3 bucket resides
region: us-west-2
```
For 3rd party storage solutions `custom-endpoint` will need to be set. E.g. `custom-endpoint: 'https://minio.mydomain.com:9000'`.

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
                "s3:ListMultipartUploadParts",
                "s3:AbortMultipartUpload"
            ],
            "Resource": [
                "arn:aws:s3:::my-backup-bucket/s3backup/*"
            ]
        }
    ]
}
```