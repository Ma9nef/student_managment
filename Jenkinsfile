pipeline {
  agent any

  tools {
    jdk 'JAVA_HOME'
    maven 'M2_HOME'
    nodejs 'NODE18'
  }

  environment {
    BACKEND_IMAGE  = "manef99/student-management"
    FRONTEND_IMAGE = "manef99/student-management-frontend"
    IMAGE_TAG      = "1.0.0-${env.BUILD_NUMBER}"
    K8S_NAMESPACE  = "devops"
  }

  options {
    timestamps()
  }

  stages {

    /* ===================== CHECKOUT ===================== */

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
              set -e
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker build -t $BACKEND_IMAGE:$IMAGE_TAG .
              docker push $BACKEND_IMAGE:$IMAGE_TAG
            '''
          }
        }
      }
    }

    /* ===================== FRONTEND ===================== */

    stage('Frontend - Docker Build & Push') {
      steps {
        dir('frontend') {
          withCredentials([usernamePassword(
            credentialsId: 'docker-hub-cred',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
          )]) {
            sh '''
              set -e
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
              docker build -t $FRONTEND_IMAGE:$IMAGE_TAG .
              docker push $FRONTEND_IMAGE:$IMAGE_TAG
            '''
          }
        }
      }
    }

    /* ===================== KUBERNETES ===================== */

    stage('Deploy to Kubernetes') {
      steps {
        dir('k8s') {
          sh '''
            set -e

            echo "==> Ensure namespace"
            kubectl get ns $K8S_NAMESPACE >/dev/null 2>&1 || kubectl create ns $K8S_NAMESPACE

            echo "==> Restore clean manifests"
            git checkout -- spring-deployment.yaml angular-deployment.yaml || true

            echo "==> Inject image tags"
            sed -i "s|__BACKEND_IMAGE__|$BACKEND_IMAGE:$IMAGE_TAG|g" spring-deployment.yaml
            sed -i "s|__FRONTEND_IMAGE__|$FRONTEND_IMAGE:$IMAGE_TAG|g" angular-deployment.yaml

            echo "==> Apply manifests"
            kubectl apply -n $K8S_NAMESPACE -f configmap.yaml
            kubectl apply -n $K8S_NAMESPACE -f secret.yaml
            kubectl apply -n $K8S_NAMESPACE -f mysql-deployment.yaml
            kubectl apply -n $K8S_NAMESPACE -f spring-deployment.yaml
            kubectl apply -n $K8S_NAMESPACE -f angular-deployment.yaml
            kubectl apply -n $K8S_NAMESPACE -f angular-service.yaml

            echo "==> Force rollout"
            kubectl rollout restart deployment spring-deployment  -n $K8S_NAMESPACE
            kubectl rollout restart deployment angular-deployment -n $K8S_NAMESPACE

            echo "==> Wait for readiness"
            kubectl rollout status deployment spring-deployment  -n $K8S_NAMESPACE --timeout=180s
            kubectl rollout status deployment angular-deployment -n $K8S_NAMESPACE --timeout=180s

            echo "==> Images actually running"
            kubectl get pods -n $K8S_NAMESPACE -o jsonpath='{range .items[*]}{.metadata.name}{" => "}{range .spec.containers[*]}{.image}{"\\n"}{end}{end}'
          '''
        }
      }
    }
  }

  post {
    success {
      echo "✅ Deployed successfully with tag: ${IMAGE_TAG}"
    }
    failure {
      echo "❌ Pipeline failed"
    }
    cleanup {
      sh 'docker system prune -f'
    }
  }
}
