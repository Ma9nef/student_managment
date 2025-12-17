pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        IMAGE_NAME = "manef99/student-management"
        IMAGE_TAG = "1.0.0-${env.BUILD_NUMBER}"
        K8S_DEPLOY_FILE = "spring-deployment.yaml"
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
                sh 'mvn -DskipTests=true clean verify'
            }
        }

     stage('Run Tests & Coverage') {
    steps {
        sh 'mvn clean verify'
    }
}

stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('sonarqube') {
            withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                sh """
                  mvn sonar:sonar \
                  -Dsonar.token=$SONAR_TOKEN \
                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                """
            }
        }
    }
}


        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker Image: ${IMAGE_NAME}:${IMAGE_TAG}"

                    sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                    sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-hub-cred', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                        docker push ${IMAGE_NAME}:${IMAGE_TAG}
                        docker push ${IMAGE_NAME}:latest
                    """
                }
            }
        }

        stage('Update K8s Deployment File With Latest Image') {
            steps {
                script {
                    echo "Updating ${K8S_DEPLOY_FILE} with latest image tag..."

                    sh """
                        sed -i 's|image: ${IMAGE_NAME}:.*|image: ${IMAGE_NAME}:${IMAGE_TAG}|g' ${K8S_DEPLOY_FILE}
                    """

                    echo "Updated deployment file:"
                    sh "cat ${K8S_DEPLOY_FILE}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    echo "🚀 Deploying to Kubernetes..."

                    sh "kubectl apply -f configmap.yaml -n devops"
                    sh "kubectl apply -f secret.yaml -n devops"
                    sh "kubectl apply -f mysql-deployment.yaml -n devops"
                    sh "kubectl apply -f ${K8S_DEPLOY_FILE} -n devops"

                    sh "kubectl rollout restart deployment spring-deployment -n devops"
                    sh "kubectl get pods -n devops"
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline executed successfully. New image deployed: ${IMAGE_TAG}"
        }
        failure {
            echo "❌ Pipeline failed."
        }
        cleanup {
            sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG} || true"
            sh "docker rmi ${IMAGE_NAME}:latest || true"
        }
    }
}
