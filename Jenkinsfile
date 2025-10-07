pipeline {
    agent any

    tools {
        jdk 'jdk21'
        maven 'maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling latest code from GitHub...'
                git branch: 'main', url: 'https://github.com/<your-username>/ci-cd-myapp.git'
            }
        }

        stage('Build with Maven') {
            steps {
                echo 'Building the project...'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Running unit and UI tests...'
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo 'Deploying WAR to Tomcat...'
                deploy adapters: [tomcat9(
                    credentialsId: 'tomcat-credentials',
                    path: '/myapp',
                    url: 'http://localhost:8080'
                )], contextPath: null, war: 'target/myapp.war'
            }
        }

        stage('Verify Deployment') {
            steps {
                echo 'Verifying deployed app...'
                bat 'curl -I http://localhost:8080/myapp'
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully.'
        }
        failure {
            echo '❌ Pipeline failed. Check console logs.'
        }
    }
}
