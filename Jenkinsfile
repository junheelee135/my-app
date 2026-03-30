pipeline {
    agent any

    environment {
      IMAGE_NAME = "my-app"
      CONTAINER_NAME = "my-app-container"
      APP_YML_FILE = 'APPLICATION_YML_FILE'
    }
    
    stages {
        // 감지 = main : push (commit)
        stage('Check Out') {
            steps {
                echo 'Git Checkout'
                checkout scm
            }
        }
    
        // gradlew 권한 부여
        stage('Gradle Permission') {
            steps {
                sh '''
                    chmod +x gradlew
                '''
            }
        }

        // build 
        stage('Gradle Build') {
            steps {
                sh '''
                    ./gradlew clean build -x test
                '''
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

        // 컨테이너 실행
        stage('Run Container') {
            steps {
                withCredentials([file(credentialsId: APP_YML_FILE, variable: 'APP_YML')]) {
                    sh '''
                         docker stop $CONTAINER_NAME || true
                         docker rm $CONTAINER_NAME || true
                         docker run -d -p 9090:9090 \
                            --name $CONTAINER_NAME  \
                            -v $APP_YML:/app/application.yml:ro \
                            $IMAGE_NAME
                    '''
                }
            }
        }

    }
}
