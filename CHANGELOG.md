## 2.3.0
### New
- Prefixes Discord messages with the prefix specified in the config

## 2.2.0
### New
- Added Discord webhooks (requires setting new `webhook-url` config option)

## 2.1.0

### New
- Added `/s3backup dry-run`, which saves a file with a list of server files that would be included with the backup

## 2.0.0

### Breaking Changes

- The config `ignore-files`  setting will now ignore a file if the ignore string is present anywhere in its path

## 1.1.6

### New
- Update to support Spigot 1.15
- Update bStats
- Update AWS s3 SDK

## 1.1.5

### Bugfixes
- Relocate `org.apache` to resolve dependency conflicts.

## 1.1.4

### Bugfixes
- Check for ongoing backups before initiating a new one

## 1.1.3

### Bugfixes
- Ordering list output

### New
- Adding disk usage of backups to list command

## 1.1.2

### New
- Adding user variable for signed URL duration

## 1.1.1

### Bugfixes
- Fix issue where a blank prefix would result in the regex check failing

### New
- Add support for third party s3 services

## 1.1.0

### New
- Update to support Spigot 1.14.4

## 1.0.1

### New
- Update to support Spigot 1.14.1

## 1.0.0

### New
- Initial release