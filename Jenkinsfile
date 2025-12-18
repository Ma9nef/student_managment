pipeline {
  agent any

  tools {
    jdk 'JAVA_HOME'
    maven 'M2_HOME'
    nodejs 'NodeJS'
  }

  environment {
    BACKEND_IMAGE  = "manef99/student-management-backend"
    FRONTEND_IMAGE = "manef99/student-management-frontend"
    IMAGE_TAG      = "1.0.0-${env.BUILD_NUMBER}"
    K8S_NAMESPACE  = "devops"
  }

  options {
    timestamps()
  }

  stages {

    stage('Checkout Source') {
      steps {
        checkout scm
      }
    }

    /* ===================== BACKEND ===================== */

    stage('Backend - Tests & Build') {
      steps {
        dir('backend') {
          sh 'mvn -B clean verify'
        }
      }
    }

    stage('Backend - SonarQube Analysis') {
      steps {
        dir('backend') {
          withSonarQubeEnv('sonarqube') {
            withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
              sh '''
                mvn -B sonar:sonar \
                  -Dsonar.token=$SONAR_TOKEN \
                  -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
              '''
            }
          }
        }
      }
    }

    stage('Backend - Docker Build & Push') {
      steps {
        dir('backend') {
          withCredentials([usernamePassword(
            credentialsId: 'docker-hub-cred',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
          )]) {
            sh '''
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker build -t $BACKEND_IMAGE:$IMAGE_TAG .
              docker tag $BACKEND_IMAGE:$IMAGE_TAG $BACKEND_IMAGE:latest
              docker push $BACKEND_IMAGE:$IMAGE_TAG
              docker push $BACKEND_IMAGE:latest
            '''
          }
        }
      }
    }

    /* ===================== FRONTEND ===================== */

    stage('Frontend - Build Angular') {
      steps {
        dir('frontend') {
          sh 'npm ci'
          sh 'npm run build'
        }
      }
    }

    stage('Frontend - Docker Build & Push') {
      steps {
        dir('frontend') {
          withCredentials([usernamePassword(
            credentialsId: 'docker-hub-cred',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
          )]) {
            sh '''
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker build -t $FRONTEND_IMAGE:$IMAGE_TAG .
              docker tag $FRONTEND_IMAGE:$IMAGE_TAG $FRONTEND_IMAGE:latest
              docker push $FRONTEND_IMAGE:$IMAGE_TAG
              docker push $FRONTEND_IMAGE:latest
            '''
          }
        }
      }
    }

    /* ===================== KUBERNETES ===================== */

    stage('Deploy to Kubernetes (3 pods)') {
      steps {
        dir('k8s') {
          sh '''
            set -e

            # 1) Namespace (idempotent)
            kubectl get ns $K8S_NAMESPACE >/dev/null 2>&1 || kubectl create ns $K8S_NAMESPACE

            # 2) Apply manifests
            kubectl apply -n $K8S_NAMESPACE -f configmap.yaml
            kubectl apply -n $K8S_NAMESPACE -f secret.yaml
            kubectl apply -n $K8S_NAMESPACE -f mysql-deployment.yaml
            kubectl apply -n $K8S_NAMESPACE -f spring-deployment.yaml
            kubectl apply -n $K8S_NAMESPACE -f angular-deployment.yaml

            # 3) Force Spring & Angular to use the new images (NO guessing)
            # IMPORTANT: container name must match your YAML container name
            kubectl set image deployment/student-management-backend  student-management-backend=$BACKEND_IMAGE:$IMAGE_TAG  -n $K8S_NAMESPACE
            kubectl set image deployment/student-management-frontend student-management-frontend=$FRONTEND_IMAGE:$IMAGE_TAG -n $K8S_NAMESPACE

            # 4) Wait for rollouts (avoid "pods not updated" confusion)
            kubectl rollout status deployment/student-management-backend  -n $K8S_NAMESPACE --timeout=180s
            kubectl rollout status deployment/student-management-frontend -n $K8S_NAMESPACE --timeout=180s
            kubectl rollout status deployment/student-mysql               -n $K8S_NAMESPACE --timeout=180s

            # 5) Proof: show the 3 pods
            kubectl get pods -n $K8S_NAMESPACE -o wide
          '''
        }
      }
    }
  }

  post {
    success {
      echo "✅ Deployed successfully (tag: ${IMAGE_TAG})"
    }
    failure {
      echo "❌ Pipeline failed"
    }
    cleanup {
      sh 'docker system prune -f'
    }
  }
}
