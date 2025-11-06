pipeline {
    agent any

    tools {
        jdk 'jdk-17'
        maven 'maven3'
    }

    environment {
        TOMCAT_HOST = '35.175.198.186'
        SSH_CRED = 'tomcat-server'
        WAR_NAME = 'myapp.war'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/vebhav24/cicd.git'
            }
        }

        stage('Build') {
            steps {
                dir('myapp') {    // 👈 this ensures we’re inside folder containing pom.xml
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                dir('myapp') {
                    sh 'mvn test'
                }
                junit 'myapp/target/surefire-reports/*.xml'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                echo 'Deploying WAR to Tomcat server...'
                sshagent (credentials: ['ssh_tomcat']) {
                    sh '''
                        scp -o StrictHostKeyChecking=no myapp/target/myapp.war ubuntu@35.175.198.186:/opt/tomcat/webapps/
                        ssh ubuntu@35.175.198.186 "sudo systemctl restart tomcat9"
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sh 'curl -I http://35.175.198.186:8080/myapp || true'
            }
        }
    }

    post {
        success {
            echo '✅ Build and deployment successful!'
        }
        failure {
            echo '❌ Build failed. Check console logs.'
        }
    }
}
