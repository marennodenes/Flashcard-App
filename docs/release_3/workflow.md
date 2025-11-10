# Workflow

## Development Methodology

We apply selected practices from the Agile Scrum methodology. In particular, we use:

- User stories as the basis for defining work.
- Keeping user stories as small as possible, to prevent issues from becoming too large or complex.
- Daily standup at the start of our meetings, to udpate the rest of the group about work done since last meeting.
- Pairprogramming on majority of programmingtasks to improve code quality share knowledge, and reduce errors early in the process.
- Regular discussions during team meetings to refine and prioritize tasks.

This lightweight approach allows us to maintain a good development flow, avoid bottlenecks, and ensure that progress is trackable throughout the release cycle.

## Git and GitHub Usage

Our Git workflow follows a feature branch model:

- Each user story is created as an issue on GitHub.
- Every issue is implemented in its own branch, named consistently after the issue.
- When the issue is resolved, the branch is merged into the dev branch via a pull request.
- Only stable and tested code is allowed in dev.
- At the end of each release, dev is merged into main, representing the release version.

## Project Management

We use GitHub Projects to manage the development process:

- Issues are tracked on a project board, moving through statuses such as To Do, In Progress, and Done.
- We make active use of labels to categorize issues (e.g., feature, bug, documentation).
- Issues are assigned to specific team members or pairs, making responsibility clear.
- Consistent use of Conventional Commits provides traceability from commits back to issues.

## Review and Quality Control

Pull requests are reviewed by at least one other team member before merging. Conflicts with dev are resolved locally before a pull request is submitted. This ensures that the dev branch always remains in a working state.
