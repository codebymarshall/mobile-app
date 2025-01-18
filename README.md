# Vacation and Excursion Management App

## Overview

This application is designed to help users manage their vacations and excursions. Users can create, update, and delete vacation details, as well as add excursions to their vacations. The app also supports notifications and sharing vacation details.

## Features

- **Vacation Management**: Create, update, and delete vacation details.
- **Excursion Management**: Add excursions to vacations, including details like name, price, date, and notes.
- **Notifications**: Receive notifications about vacation start and end dates.
- **Sharing**: Share vacation details with others.

## Getting Started

To get started with the Vacation and Excursion Management App, follow these steps:

1. **Clone the Repository**: Clone the project repository to your local machine using Git.
2. **Open in Android Studio**: Open the cloned project in Android Studio.
3. **Sync Gradle Files**: Sync the Gradle files to download the necessary dependencies.
4. **Build the Project**: Build the project to ensure that all dependencies are correctly configured.
5. **Run the App**: Connect an Android device or start an emulator, then run the app from Android Studio.

For detailed instructions on cloning the repository, creating a branch, building the project, and running the app, refer to the sections below.

### Prerequisites

- Android Studio
- Git

### Cloning the Repository

To clone the project to Android Studio using Git:

1. Ensure that Android Studio and Git are installed on your system.
2. Open Android Studio and select "Get from VCS" or "File > New > Project from Version Control".
3. Enter the repository URL and clone the project.

### Creating a Branch

To create a branch and start development:

- **GitLab method**:
  - Press the '+' button near your branch name.
  - Select 'New branch' and name your branch.
  - Press 'Create Branch' to push the branch to your repository.
- **Android Studio method**:
  - Go to the 'Git' button on the top toolbar.
  - Select 'New branch' and name your branch.
  - Ensure 'Checkout branch' is selected and press 'Create'.
  - Add a commit message and push the new branch to the local repo.

### Building the Project

To build the project, open the project in Android Studio and sync the Gradle files. Ensure you have the necessary dependencies and SDK versions specified in the `build.gradle` files.

### Running the App

To run the app:

1. Connect an Android device or start an emulator.
2. Click the 'Run' button in Android Studio.

## Directions for Operating the Application

1. **Enter, Update, and Delete Vacations**: Use the Room Framework to manage vacation details. The app prevents deletion of vacations with associated excursions.
2. **Add Vacation Details**: Include title, hotel/accommodation, and start/end dates for each vacation.
3. **View and Update Vacation Details**: Access detailed views to add or update vacation information.
4. **Validation**: Ensure all input dates are correctly formatted and that the end date is after the start date.
5. **Set Alerts**: Schedule notifications for vacation start and end dates.
6. **Share Vacation Details**: Use the sharing feature to share vacation details via email, clipboard, or SMS.
7. **Manage Excursions**: Add, update, and delete excursions, including title and date for each excursion.

## Project Structure

- **MainActivity**: The main entry point of the application.
- **VacationDetails**: Activity to manage vacation details.
- **ExcursionDetails**: Activity to manage excursion details.
- **Repository**: Handles data operations and interactions with the database.

## Important Files

- `AndroidManifest.xml`: Contains the app's configuration and activities.
- `build.gradle`: Contains the build configuration and dependencies.
- `strings.xml`: Contains the app's string resources.
- `activity_main.xml`: Layout file for the main activity.
- `activity_vacation_details.xml`: Layout file for the vacation details activity.
- `activity_excursion_details.xml`: Layout file for the excursion details activity.

## Notifications

The app creates a notification channel for vacation notifications. This is done in the `createNotificationChannel` method in the `VacationDetails` activity.

## Sharing

Users can share vacation details using the `shareVacationDetails` method in the `VacationDetails` activity.

## License

This project is licensed under the Apache License, Version 2.0. See the `LICENSE` file for more details.

## Support

If you need additional support, please navigate to the course page and reach out to your course instructor.

## Future Use

Take this opportunity to create or add to a simple resume portfolio to highlight and showcase your work for future use in career search, experience, and education.

## References

- [AndroidManifest.xml](xml:app/src/main/AndroidManifest.xml)
  startLine: 1
  endLine: 50

- [build.gradle](app/build.gradle)
  startLine: 1
  endLine: 46

- [strings.xml](xml:app/src/main/res/values/strings.xml)
  startLine: 1
  endLine: 5

- [activity_main.xml](xml:app/src/main/res/layout/activity_main.xml)
  startLine: 1
  endLine: 50

- [activity_vacation_details.xml](xml:app/src/main/res/layout/activity_vacation_details.xml)
  startLine: 1
  endLine: 110

- [activity_excursion_details.xml](xml:app/src/main/res/layout/activity_excursion_details.xml)
  startLine: 1
  endLine: 76

- [VacationDetails.java](java:app/src/main/java/com/vacationtracker/mobile_app/ui/VacationDetails.java)
  startLine: 1
  endLine: 408

- [ExcursionDetails.java](java:app/src/main/java/com/vacationtracker/mobile_app/ui/ExcursionDetails.java)
  startLine: 1
  endLine: 58

- [Repository.java](java:app/src/main/java/com/vacationtracker/mobile_app/database/Repository.java)
  startLine: 1
  endLine: 39

## Deployment Information

The signed APK is deployed to Android version 34.
