pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        // 🔹 CHANGE THIS: This will create a repo named 'student-management' in your Docker Hub
        IMAGE_NAME = "manef99/student-management" 
        // 🔹 This creates a unique tag per build (e.g., 1.0.0-1, 1.0.0-2)
        IMAGE_TAG = "1.0.0-${env.BUILD_NUMBER}"
    }

    stages {
        
        stage('Checkout Source') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Ma9nef/student_managment.git'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn clean verify'
            }
        }

        // 'verify' usually packages the app, but running package ensures the JAR is in /target
        stage('Build Package') {
            steps {
                sh 'mvn package -DskipTests' // Skip tests here as they ran in the previous stage
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh "mvn sonar:sonar -Dsonar.token=$SONAR_TOKEN"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker Image: ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-cred', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline executed successfully. Image pushed to Docker Hub: ${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "❌ Pipeline failed."
        }
        cleanup {
            // Optional: Remove the image from the Jenkins server to save space
            sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG} || true"
        }
    }
}
