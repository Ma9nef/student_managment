pipeline {

    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        IMAGE_NAME = "manef99/alpine"
        IMAGE_TAG = "1.0.0"
    }

    stages {

        /* 🔹 Checkout source code */
        stage('Checkout Source') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Ma9nef/student_managment.git'
            }
        }

        /* 🔹 Run tests + generate JaCoCo coverage */
        stage('Run Tests') {
            steps {
                sh 'mvn clean verify'
            }
        }

        /* 🔹 Build package */
        stage('Build Package') {
            steps {
                sh 'mvn package'
            }
        }

        /* 🔹 SonarQube analysis */
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    withCredentials([
                        string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')
                    ]) {
                        sh """
                            mvn sonar:sonar \
                                -Dsonar.token=$SONAR_TOKEN
                        """
                    }
                }
            }
        }

        /* 🔹 Build Docker Image */
        stage('Build Docker Image') {
            steps {
                sh """
                    docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                """
            }
        }

        /* 🔹 Push Docker Image to Docker Hub */
        stage('Push Docker Image') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-hub-cred',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
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
            echo "✅ Pipeline executed successfully."
        }
        failure {
            echo "❌ Pipeline failed. Check logs for details."
        }
    }
}
