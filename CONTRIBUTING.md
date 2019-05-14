## How to contribute
### Found a bug?
Try your best to replicate the issue. Once you feel something is not right:

- Ensure the bug has not already been reported. Check the [issue list](https://gitlab.com/steve.stonehouse/s3backup/issues)
- Report the bug as an [issue](https://gitlab.com/steve.stonehouse/s3backup/issues/new)
- Ensure your bug report follows the [guidelines](https://gitlab.com/steve.stonehouse/s3backup/blob/master/CONTRIBUTING.md#guidelines-for-raising-a-bug-report)

### Do you have a patch for a bug?
If you have a patch for a bug you found or any bugs in the issue list:
- Open a PR with the patch
- Ensure the PR follows the [guidelines](https://gitlab.com/steve.stonehouse/s3backup/blob/master/CONTRIBUTING.md#guidelines-for-raising-a-pr)

### Have an idea for an improvement or new feature?
- Ensure the idea has not already been suggested [here](https://gitlab.com/steve.stonehouse/s3backup/issues)
- Raise an issue clearly describing your proposed improvement or feature
- If you have a patch for your idea, raise a PR. It will then be reviewed and tested. Ensure this follows the [guidelines](https://gitlab.com/steve.stonehouse/s3backup/blob/master/CONTRIBUTING.md#guidelines-for-raising-a-pr)
- Accepted ideas requested by raising an issue will be worked on in my free time. Feel free to work on these yourself and raise a PR, as this will speed up the process and is always appreciated

### Something missing from the documentation?
- Raise an issue detailing any missing or incorrect information you have identified

### Formatting fixes
Found a rogue whitespace or newline?

- Do not raise this as an issue
- Raise a PR instead. They will be merged in the next release
- There will be no new release solely for formatting fixes

### Guidelines for raising a bug report
Include the following in your issue, along with a detailed description of what causes the bug:
- Spigot version
- s3backup version
- Your current s3backup configuration
- Your IAM policy granting access to s3 (if applicable)
- Any errors in the console or output to the player. Enable debug mode for console stack traces

**DO NOT INCLUDE AWS CREDENTIALS IN THE REPORT!** I am not responsible for any credentials posted in the repository
### Guidelines for raising a PR
If you are raising a pull request for a bug/improvement/feature:
- Ensure the PR clearly describes the problem/solution/improvement linking to any relevant issues
- Ensure commit messages are precise and to the point. Describe what you're fixing or adding, not what the commit changes
- Squash down commits as much as possible, but keep it to one commit per fix or idea. A commit should be revertible without depending on any others
