pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    environment {
        IMAGE_NAME = "manef99/student-management"
        IMAGE_TAG = "1.0.0-${env.BUILD_NUMBER}"
        K8S_NAMESPACE = "devops"
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
                sh 'mvn clean verify -DskipTests'
            }
        }

        stage('Build Package') {
            steps {
                sh 'mvn package -DskipTests'
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
                    sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'docker-hub-cred', 
                                     usernameVariable: 'DOCKER_USER', 
                                     passwordVariable: 'DOCKER_PASS')
                ]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${IMAGE_NAME}:${IMAGE_TAG}
                        docker push ${IMAGE_NAME}:latest
                    """
                }
            }
        }

        stage('Create K8s Docker Secret') {
            steps {
                withCredentials([
                    usernamePassword(credentialsId: 'docker-hub-cred', 
                                     usernameVariable: 'DOCKER_USER', 
                                     passwordVariable: 'DOCKER_PASS')
                ]) {
                    sh """
                        echo "🔐 Updating Kubernetes Docker registry secret..."

                        kubectl delete secret regcred -n ${K8S_NAMESPACE} --ignore-not-found

                        kubectl create secret docker-registry regcred \
                          --docker-server=https://index.docker.io/v1/ \
                          --docker-username=$DOCKER_USER \
                          --docker-password=$DOCKER_PASS \
                          --docker-email=${DOCKER_USER}@users.noreply.github.com \
                          -n ${K8S_NAMESPACE}
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    echo "🚀 Deploying to Kubernetes..."

                    sh "kubectl apply -f configmap.yaml -n ${K8S_NAMESPACE}"
                    sh "kubectl apply -f secret.yaml -n ${K8S_NAMESPACE}"
                    sh "kubectl apply -f mysql-deployment.yaml -n ${K8S_NAMESPACE}"
                    sh "kubectl apply -f spring-deployment.yaml -n ${K8S_NAMESPACE}"

                    sh "kubectl get pods -n ${K8S_NAMESPACE}"
                }
            }
        }

    }

    post {
        success {
            echo "✅ Pipeline executed successfully. Application deployed on Kubernetes."
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
