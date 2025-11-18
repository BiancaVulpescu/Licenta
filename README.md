# DrivoCare - Mobile Car Management Application

## Overview
DrivoCare is an Android mobile application designed to help drivers manage their vehicle responsibilities and identify dashboard warning lights. The app combines vehicle maintenance tracking, AI-powered symbol recognition, and a social community platform into one unified solution.

## Key Features
- **Dashboard Warning Light Recognition**: Scan and identify warning symbols using your phone's camera with AI-powered image classification
- **Vehicle Management**: Track multiple vehicles with maintenance history, service records, and important document expiration dates
- **Smart Calendar**: Receive automated reminders for insurance renewals, inspections, and scheduled maintenance
- **Community Platform**: Share experiences, ask questions, and connect with other car owners through posts and comments
- **Real-time Notifications**: Stay informed about upcoming vehicle events and community interactions

## Technologies Used

### Mobile Development
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit for building native interfaces
- **Android SDK** - Android application development
- **MVVM Architecture** - Clean separation of concerns with Model-View-ViewModel pattern

### Backend & Database
- **Firebase Cloud Firestore** - NoSQL database for real-time data synchronization
- **Firebase Authentication** - Secure user authentication and account management
- **Firebase Cloud Storage** - Image and media file storage

### Machine Learning
- **TensorFlow Lite** - On-device machine learning inference
- **MobileNetV2** - CNN architecture for image classification
- **Python & Keras** - Model training and optimization

### Development Tools
- **Android Studio** - Integrated development environment
- **Figma** - UI/UX design and prototyping

## Machine Learning Model
The warning light recognition system uses a custom-trained CNN model based on MobileNetV2 architecture, achieving ~83% accuracy across 22 different dashboard symbol classes. The model runs entirely on-device using TensorFlow Lite for fast, offline predictions.

## Architecture
The application follows the MVVM (Model-View-ViewModel) architectural pattern, ensuring:
- Clean separation between UI and business logic
- Reactive data flow using StateFlow and LiveData
- Reusable and testable components
- Efficient state management

## Installation
1. Clone this repository
2. Open the project in Android Studio
3. Configure Firebase (add your `google-services.json` file)
4. Build and run on an Android device or emulator

## Requirements
- Android SDK 24 or higher
- Kotlin 1.9+
- Android Studio Hedgehog or newer

## Author
Bianca-Gabriela Vulpescu  
Faculty of Computer Science, "Alexandru Ioan Cuza" University of Iași  
Bachelor's Thesis Project - July 2025

## License
This project was developed as an academic thesis project.
