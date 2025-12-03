pipeline {

    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
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

                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {

                        sh """
                            mvn sonar:sonar \
                            -Dsonar.token=$SONAR_TOKEN
                        """
                    }
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
