# Challenges

The team has encountered several challenges during this iteration of the project, primarily due to increased technical requirements and a restructuring of the project. We identified two key challenges that we deliberately chose to focus on and address throughout the process. These were the need to carry out an intentional change of the Java version midway through the iteration, as well as the development and implementation of tests.

## 1. Java version

During the development process, we encountered several unexpected errors caused by using different Java versions across team members. Some contributors had older versions of Java installed, while others used newer ones. This created compatibility issues when running the application and caused certain dependencies and libraries to fail during compilation. At first, it was unclear why the same code worked for some developers but not for others, which made debugging time-consuming and frustrating.

To solve this problem, we first compared our local enviornments and discovered that the Java version was the main difference. We then agreed that everyone should use the same version to avoid further inconsistencies. After some discussion, we decided to standarize on Java version 21, as it was compatible with all our dependencies and included useful new features. We updated or pom.xml file and other configuration files accordingly to ensure that the entire project used the same setup. Once everyone had installed the same version and the dependencies were aligned, the errors disappeared, and the project became much more stable.

From this experience, we learned the importance of establishing consistent development enviornments at the start of a project. In the future, we will make sure to document the required tools, frameworks, and versions clearly befor anoyne starts coding. This will help us avoid similar compatibility problems and improve collaboration across the team.

## 2. Testing

The team has encountered several challenges in developing tests for our Java classes. Setting up the tests has proven technically demanding, leading to errors, failing tests, and low Jacoco coverage. This has required many hours of debugging and has caused frustration, especially when the effort doesn't seem to yield results.

To address this, we have kept a low threshold for committing and pushing code, allowing multiple team members to contribute quickly when someone gets stuck. Although some tests have been assigned to individuals, we have worked together to find solutions. Our experience has shown that a fresh pair of eyes often makes debugging easier and more efficient.

Through this collaboration, we have learned the importance of supporting each other when dealing with technically complex tasks. Being able to share challenges and solve them collectively has made the work both faster and less overwhelming. This experience has strengthened our sense of teamwork and reminded us of the value of collaboration—especially when the tasks feel difficult and demanding.
