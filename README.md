# BeMonke

Project Proposal Wiki: [BeMonke](https://git.uwaterloo.ca/c36zeng/monke346/-/wikis/Project-Proposal#project-plan). Changes to project goals, requirements, and design diagrams are made in the project proposal file. Discussions are a newly added section.

## Team members
- Henry Zhang
- Andrew Lee
- Sudhish Meenakshisundaram
- Coen Zeng

## Software Releases

- [Sprint 1 Release](https://git.uwaterloo.ca/c36zeng/monke346/-/wikis/Sprint-1-Release)
- [Sprint 2 Release](https://git.uwaterloo.ca/c36zeng/monke346/-/wikis/Sprint-2-Release)
- [Sprint 3 Release](https://git.uwaterloo.ca/c36zeng/monke346/-/wikis/Sprint-3-Release)
- [Sprint 4 Release](https://git.uwaterloo.ca/c36zeng/monke346/-/wikis/Sprint-4-Release)

## Instructions to run the app

- The server is loaded into a Docker image. To access this Docker image, run the command `docker pull sudhish3120/be_monke_server_image:latest`. 
- Next, run `docker run -p 8080:8080 sudhish3120/be_monke_server_image:latest`
- This should start the Ktor backend project
- To run the front-end, drag the [.apk](https://git.uwaterloo.ca/c36zeng/monke346/-/blob/main/BeMonke-release.apk?ref_type=heads) file to an Android emulator
- We have started both the front and back end, so the app should fully work as expected
- The Postgres SQL database is hosted on Cockroach Labs, so there is no specific steps needed to configure the database
- If the app's log in page appears disoriented, try moving the .apk file to a different emulator. For example, Pixel 7 API 26 is an emulator that displays the log in page correctly
- Suggested testing steps: Go to settings tab, log out and log back in through Google OAuth (through any email of choice). Once logged in, upload pictures, add friends, and explore other functionalities of the app
- If the Home page is empty, it means that you have no friend. Go to the search tab, and add friends from the list of default friends
- If the profile page is empty, that means there are no posts from the user that is uploaded. Go to the + tab to upload posts

## How to create a signed APK in intellij

1. Go to "Build" on the top bar
2. Go to "Generate Signed Bundle / APK"
3. Click on the second option, which is APK
4. Now enter in the exact following info in the form:
- keystore file = 'your_keystore.jks'
- storePassword = 'your_keystore_password'
- keyAlias = 'your_key_alias'
- keyPassword = 'your_key_password'
5. Click on "release" (not debug)
6. After clicking next, the APK will be created. Click on "locate" to find where the APK was created.

<!-- ## Add your files

- [ ] [Create](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#create-a-file) or [upload](https://docs.gitlab.com/ee/user/project/repository/web_editor.html#upload-a-file) files
- [ ] [Add files using the command line](https://docs.gitlab.com/ee/gitlab-basics/add-file.html#add-a-file-using-the-command-line) or push an existing Git repository with the following command:

```
cd existing_repo
git remote add origin https://git.uwaterloo.ca/c36zeng/monke346.git
git branch -M main
git push -uf origin main
```

## Integrate with your tools

- [ ] [Set up project integrations](https://git.uwaterloo.ca/c36zeng/monke346/-/settings/integrations)

## Collaborate with your team

- [ ] [Invite team members and collaborators](https://docs.gitlab.com/ee/user/project/members/)
- [ ] [Create a new merge request](https://docs.gitlab.com/ee/user/project/merge_requests/creating_merge_requests.html)
- [ ] [Automatically close issues from merge requests](https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues-automatically)
- [ ] [Enable merge request approvals](https://docs.gitlab.com/ee/user/project/merge_requests/approvals/)
- [ ] [Set auto-merge](https://docs.gitlab.com/ee/user/project/merge_requests/merge_when_pipeline_succeeds.html)

## Test and Deploy

Use the built-in continuous integration in GitLab.

- [ ] [Get started with GitLab CI/CD](https://docs.gitlab.com/ee/ci/quick_start/index.html)
- [ ] [Analyze your code for known vulnerabilities with Static Application Security Testing(SAST)](https://docs.gitlab.com/ee/user/application_security/sast/)
- [ ] [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy](https://docs.gitlab.com/ee/topics/autodevops/requirements.html)
- [ ] [Use pull-based deployments for improved Kubernetes management](https://docs.gitlab.com/ee/user/clusters/agent/)
- [ ] [Set up protected environments](https://docs.gitlab.com/ee/ci/environments/protected_environments.html)

***

# Editing this README

When you're ready to make this README your own, just edit this file and use the handy template below (or feel free to structure it however you want - this is just a starting point!). Thank you to [makeareadme.com](https://www.makeareadme.com/) for this template.

## Suggestions for a good README
Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.

## Name
Choose a self-explaining name for your project.

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.

## How to create a signed APK in intellij

1. Go to "Build" on the top bar
2. Go to "Generate Signed Bundle / APK"
3. Click on the second option, which is APK
4. Now enter in the exact following info in the form:
keystore file = 'your_keystore.jks'
storePassword = 'your_keystore_password'
keyAlias = 'your_key_alias'
keyPassword = 'your_key_password'
5. Click on "release" (not debug)
6. After clicking next, the APK will be created. Click on "locate" to find where the APK was created. -->

