# SEMU Spring Boot Backend API

SEMU is a Spring Boot-based REST API designed for managing conversations. It offers a suite of features for starting, updating, and managing user conversations, including support for text, audio, and image-based exchanges.

## Getting Started

To get started with the SEMU API, you'll need to set up the project and configure your environment.

### Prerequisites

- JDK 11
- Maven
- Spring Boot
- An IDE of your choice (IntelliJ IDEA, Eclipse, etc.)

### Setting Up the Project

1. Clone the SEMU repository to your local machine.
2. Open the project in your IDE and wait for the dependencies to be resolved.
3. Configure your application properties including the database connection and any other environment-specific settings.

### Configuring Google Default Credentials

To run SEMU with Maven and to use services that require Google Default Credentials, follow these steps:

1. Create a service account key in the Google Cloud Platform Console.
2. Download the JSON key file.
3. Set the environment variable `GOOGLE_APPLICATION_CREDENTIALS` to the path of the JSON key file.
```
export GOOGLE_APPLICATION_CREDENTIALS="path/to/your/service-account-file.json"
```
4. Run the project with Maven.
```
mvn spring-boot:run
```

## API Endpoints

Below are the endpoints provided by the SEMU API:

### Conversation Endpoints

- `POST /api/conversations/startConversation`: Start a new conversation with a list of messages.
- `POST /api/conversations/addMessage`: Add a message to an existing conversation by specifying the conversation ID.
- `GET /api/conversations/getConversation`: Retrieve the details of a specific conversation.
- `GET /api/conversations/getAllUserConversations`: Get all conversations for the authenticated user.
- `POST /api/conversations/startAudioConversation`: Start a new conversation with an audio message file.
- `POST /api/conversations/addAudioMessage`: Add an audio message to an existing conversation.
- `GET /api/conversations/getAudioResponse`: Get an audio response for a specific conversation.

### User Endpoints

- `POST /api/users/register`: Register a new user with user details.
- `POST /api/users/authenticate`: Authenticate a user and receive an authentication token.

## Data Models

### DTOs

- **AudioDTO**: Used to encapsulate audio data with a textual content descriptor.
- **ConversationDTO**: Represents a conversation, including its ID, title, messages, and timestamps.
- **MessageDTO**: Encapsulates a message's ID, content, timestamp, and a flag indicating if the user or the assistant sent it.
- **ReplyDTO**: Contains information about the reply to a request within a conversation.
- **TranscriptionDTO**: Contains transcription data from an audio message.
- **AuthDTO**: Used for transferring authentication tokens and status after user registration or login.

### Entities

- **Conversation**: Represents the conversation entity with its unique ID, associated user, and messages.
- **Message**: Represents a message within a conversation, including its ID, content, and timestamp.

## Live API URL

The live API can be accessed at [https://tribal-saga-397814.lm.r.appspot.com/api/](https://tribal-saga-397814.lm.r.appspot.com/api/).

## Development Notes

When developing against the SEMU API:

- Use the DTOs to understand the request and response structures for each endpoint.
- Ensure you have the correct authorization headers when making requests to secured endpoints.

For front-end developers connecting to the SEMU API:

- Use the documented endpoints to integrate conversation features into your application.
- Handle user authentication and store tokens securely for subsequent requests.
