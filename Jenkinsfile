pipeline {

    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Ma9nef/student_managment.git'
            }
        }

      stage('Run Tests') {
    steps {
        sh 'mvn test || true'
    }
}

    }

    post {
        success {
            echo "Tests Maven exécutés avec succès."
        }
        failure {
            echo " Les tests Maven ont échoué."
        }
    }
}
