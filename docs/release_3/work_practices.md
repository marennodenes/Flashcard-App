# Work Practices

## Meeting Practices

The team meets twice a week, a schedule that was agreed upon early in the development process. Unless otherwise communicated, all team members are expected to attend these sessions. We also book a room so we can work physically together.

Meeting times:

- Monday 09:00 - 15:00
- Thursday 09:00 - 12:00

## Task Distribution

During the meetings, issues are assigned to team members based on the nature of the task and availability. We work in pairs, but the pairing is flexible and changes based on what makes most sense for each specific task or feature.

For example, pairs are formed based on:

- Who has relevant experience or knowledge for a particular task
- Who is available at the same time
- What part of the system needs work

This flexible approach allows us to:

- Match expertise to tasks more effectively
- Share knowledge across the team
- Adapt to changing schedules and priorities

We divided the workflow into main issues, and assigned pairs accordingly:

- Enhancing the login and signup/ user authentication features
- fxui enhancements and updating fxui to use the REST API
- Implement the server controllers and mappers in shared
- Implement the server service classes
- Implement DTO objects

Each pair divides tasks internally and coordinates additional meetings if necessary.

## Communication Flow

Team communication outside of meetings primarily took place in a Facebook Messenger group.

## Guidelines

We followed a few lightweight guidelines to ensure consistency:

Use Conventional Commits (see [commit_message_standard.md](commit_message_standard.md)).

Use issue templates and pull request templates to make it easier for other group members to understand without having to ask (see [issue_templates](/.github/issue_templates/FeatureRequest.md) and [pull_request_templates](/.github/pull_request_templates/FeatureImplementation.md)).

Write descriptive method names to improve readability and make the code easier to understand for others.

## Pull Requests and Code Reviews

When merging code from feature branches into the dev branch, the following process is applied:

1. The feature branch is updated by pulling the latest changes from dev and resolving any conflicts.
2. A pull request is created, which must be reviewed and approved by another team member.
3. Only after all issues in a release are resolved is the dev branch merged into main.
