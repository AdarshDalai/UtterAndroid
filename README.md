# Utter

Utter is a social media application that empowers users to connect, share, and engage with others through posts, comments, likes, and more. With a sleek interface and robust backend, Utter brings a modern social media experience to both Android and iOS platforms.

## Features

### Core Features
- **User Authentication**: Secure account creation and login using Supabase.
- **User Profiles**: Customize profiles with bio and profile pictures.
- **Posts**: Share content with text, images, or videos.
- **Comments and Likes**: Engage with posts through comments and likes.
- **Followers/Following**: Build connections by following other users.
- **Notifications**: Stay updated on likes, comments, and follows.

### Media Storage
- All media files are securely stored and served using Cloudflare.

### API
- RESTful endpoints for managing users, posts, comments, likes, and more, built with FastAPI.

### Cross-Platform Mobile Apps
- Android: Built with Jetpack Compose.
- iOS: Built with SwiftUI.

## Tech Stack

### Backend
- **FastAPI**: For creating RESTful APIs.
- **Supabase**: For authentication and database management (PostgreSQL).
- **Cloudflare**: For media storage and delivery.

### Mobile
- **Android**: Jetpack Compose for UI, MVVM architecture, Ktor for API calls, and DataStore for secure storage.
- **iOS**: SwiftUI for UI, following MVVM architecture.

### Database
- **PostgreSQL**: Managed by Supabase.

## File Structure

### Backend
```
project/
├── app/
│   ├── main.py         # Entry point for the FastAPI app
│   ├── routes/         # API route definitions
│   ├── services/       # Business logic and integrations
│   ├── models/         # Pydantic models
│   ├── utils/          # Utility functions
│   └── tests/          # Unit tests
```

### Mobile App (Android)
```
project/
├── domain/             # Central models (Profile, Posts, Likes, Comments, etc.)
├── features/           # Feature-specific folders with Data, Domain, Presentation layers
│   ├── Auth/
│   ├── Profile/
│   ├── Feed/
│   ├── Chat/
│   ├── AddPost/
│   └── Notification/
└── MainActivity.kt     # Entry point for the Android app
```

### Mobile Apps
#### Android
1. Open the Android folder in Android Studio.
2. Sync the project with Gradle files.
3. Run the app on an emulator or physical device.

#### iOS
1. Open the iOS project in Xcode.
2. Build and run the app on a simulator or physical device.

## API Endpoints

### Authentication
- `POST /auth/register`: Register a new user.
- `POST /auth/login`: Log in an existing user.
- `POST /auth/logout`: Log out the current user.

### Posts
- `GET /posts`: Fetch all posts.
- `POST /posts`: Create a new post.
- `DELETE /posts/{post_id}`: Delete a post.

### Comments
- `GET /posts/{post_id}/comments`: Fetch comments for a post.
- `POST /posts/{post_id}/comments`: Add a comment to a post.

### Likes
- `POST /posts/{post_id}/like`: Like a post.
- `DELETE /posts/{post_id}/like`: Unlike a post.

## Contribution

Contributions are welcome! If you'd like to contribute:
1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "Add your message here"
   ```
4. Push the changes:
   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a pull request.

**Start connecting and sharing with Utter today!**
