# Work Practices

## Meeting Practices

The team meets twice a week, a schedule that was agreed upon early in the development process. Unless otherwise communicated, all team members are expected to attend these sessions. We also book a room so we can work physically together.

Meeting times:

- Monday 09:00–15:00

- Thursday 09:00–12:00

## Task Distribution

During the meetings, issues are assigned to team members. For this release, we decided to work in pairs, with each pair responsible for a specific part of the system:

- JSON handling and documentation

- The view for displaying all flashcards

- The learning mode view for practicing flashcards

Each pair divided tasks internally and coordinated additional meetings if necessary.

## Communication Flow

Team communication outside of meetings primarily took place in a Facebook Messenger group.

## Guidelines

We followed a few lightweight guidelines to ensure consistency:

Use Conventional Commits (see [commit_message_standard.md](commit_message_standard.md)).

Write descriptive method names to improve readability and make the code easier to understand for others.

## Pull Requests and Code Reviews

When merging code from feature branches into the dev branch, the following process is applied:

The feature branch is updated by pulling the latest changes from dev and resolving any conflicts.

A pull request is created, which must be reviewed and approved by another team member.

Only after all issues in a release are resolved is the dev branch merged into main.
