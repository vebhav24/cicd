pipeline {
    agent any

    tools {
        jdk 'jdk-21'
        maven 'maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master',
                    credentialsId: 'f9172797-3192-474d-a03c-bc5a15f2ed32',
                    url: 'https://github.com/vebhav24/cicd.git'
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
