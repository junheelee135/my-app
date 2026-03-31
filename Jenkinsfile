pipeline {
    agent any

    environment {
      IMAGE_NAME = "my-app"
      SERVER_IP = "127.0.0.1"
      APP_DIR = "~/app"
    }
    
    stages {
        // 감지 = main : push (commit)
        stage('Check Out') {
            steps {
                echo 'Git Checkout'
                checkout scm
            }
        }
    
        stage('Build with Gradle') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build -x test'
            }
        }

        // Docker Build 
        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build -t $IMAGE_NAME .
                '''
            }
        }

        // Docker Compose 실행
        stage('Deploy with Docker Compose') {
            steps {
                sshagent(['UBUNTU_SSH_KEY']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no blue@$SERVER_IP '
                            cd $APP_DIR
                            docker compose pull
                            docker compose up -d --remove-orphans
                        '
                    """
                }
            }
        }

    }
}
