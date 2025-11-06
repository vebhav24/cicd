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
                git branch: 'main', url: 'https://github.com/vebhav24/ci-cd-myapp.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
                junit 'target/surefire-reports/*.xml'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                sshagent (credentials: ["${SSH_CRED}"]) {
                    sh """
                    scp -o StrictHostKeyChecking=no target/${WAR_NAME} ubuntu@${TOMCAT_HOST}:/tmp/
                    ssh -o StrictHostKeyChecking=no ubuntu@${TOMCAT_HOST} '
                        sudo systemctl stop tomcat9
                        sudo rm -rf /var/lib/tomcat9/webapps/myapp*
                        sudo mv /tmp/${WAR_NAME} /var/lib/tomcat9/webapps/
                        sudo systemctl start tomcat9
                    '
                    """
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sh 'curl -I http://35.175.198.186:8080/myapp'
            }
        }
    }

    post {
        success {
            echo '✅ Build & Deployment successful.'
        }
        failure {
            echo '❌ Build or Deployment failed. Check logs.'
        }
    }
}
