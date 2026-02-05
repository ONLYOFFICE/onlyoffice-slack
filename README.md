# ONLYOFFICE app for Slack

Welcome to the official ONLYOFFICE app for Slack! This app seamlessly integrates ONLYOFFICE Docs into your [Slack](https://slack.com/) workspace, allowing you to create, view, and edit documents, spreadsheets, and presentations within your channels and direct messages.

## Features ⭐️

- **Editing office files:** Open and edit .docx, .xlsx, and .pptx files shared in Slack without leaving the platform.
- **Collaborative editing:** All participants in a chat have simultaneous editing access to the files.
- **Creating new files:** Quickly generate new documents, spreadsheets, or presentations using a simple slash command.
- **Automatic saving:** Your changes are saved automatically, ensuring no work is lost.

## App installation 📥

Find and install the app directly from the [Slack Marketplace](https://slack.com/marketplace).

**Note:** Each user within your Slack workspace must individually install the app to use its features.

## App configuration ⚙️

### For Slack workspace admins

As an admin, you can configure the global settings for the ONLYOFFICE app that will apply to your entire Slack workspace.

Locate the ONLYOFFICE app in your app list on the left-hand sidebar. Navigate to the Home tab within the app.

Enter the required details for ONLYOFFICE Docs:

- ONLYOFFICE Docs address (URL)
- ONLYOFFICE Docs secret key ([JWT Secret](https://helpcenter.onlyoffice.com/docs/installation/docs-configure-jwt.aspx))
- JWT Header

You can also connect to the public test server of ONLYOFFICE Docs for one month by checking the corresponding box.

### For non-admin users

If you are not an admin, you simply need to install the app. The settings configured by your workspace admin will be applied automatically.

If you try to access the ONLYOFFICE app without installing it first, Slack will prompt you to do so. You will not be able to use the integration until the app is installed.

## App usage

### How to edit an existing file 📝

You can open and edit any compatible office file sent in a Slack message. All participants in the chat are granted editing permissions by default.

- Hover over the message containing the file, open its context menu, and select ONLYOFFICE.
- In the window that appears, click the Open button next to the file you wish to edit.
- The editor will open in a new browser tab for a full-featured experience.

After you save your edits, a notification will be posted in the message's thread to inform other chat members that the file has been updated.

### How to create a new file 📄

You can create a new document, spreadsheet, or presentation directly within any chat.

- In the message field, type the `/file` command.
- A dialog box will appear. Enter a file name and choose the desired file format (Document, Spreadsheet, or Presentation).
- Click Create.

The newly created file will be posted in the chat. To start editing it, simply follow the steps in the section above.

### Getting help 🤔

If you encounter any issues or need a quick reminder on how to use the app, just type the `/help` command in any chat where the ONLYOFFICE app is present.

You will receive a private message with a brief set of instructions to guide you.

## Installing ONLYOFFICE Docs

To be able to work with office files within Slack, you will need an instance of [ONLYOFFICE Docs](https://www.onlyoffice.com/office-suite.aspx). You can install the self-hosted version of the editors or opt for ONLYOFFICE Docs Cloud which doesn't require downloading and installation.

**Self-hosted editors**

You can install [free Community version](https://www.onlyoffice.com/download-community.aspx#docs-community) of ONLYOFFICE Docs or scalable [Enterprise Edition](https://www.onlyoffice.com/download.aspx#docs-enterprise).

To install free Community version, use [Docker](https://github.com/onlyoffice/Docker-DocumentServer) (recommended) or follow [these instructions](https://helpcenter.onlyoffice.com/docs/installation/docs-community-install-ubuntu.aspx) for Debian, Ubuntu, or derivatives.

To install Enterprise Edition, follow the instructions [here](https://helpcenter.onlyoffice.com/docs/installation/enterprise).

**ONLYOFFICE Docs Cloud**

To get ONLYOFFICE Docs Cloud, get started [here](https://www.onlyoffice.com/docs-registration.aspx).

## Need help? Feedback & Support 💡

In case of technical problems, the best way to get help is to submit your issues [here](https://github.com/ONLYOFFICE/onlyoffice-slack/issues). Alternatively, you can contact ONLYOFFICE team via [community.onlyoffice.com](https://community.onlyoffice.com) or [feedback.onlyoffice.com](https://feedback.onlyoffice.com/forums/966080-your-voice-matters).