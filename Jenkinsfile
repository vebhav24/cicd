pipeline {
    agent any

    tools {
        jdk 'jdk-17'
        maven 'maven3'
    }

    environment {
    TOMCAT_HOST = '35.175.198.186'
    SSH_CRED = 'shame'
    SSH_USER = 'ubuntu'
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
        sshagent (credentials: [env.SSH_CRED]) {
            sh """
                scp -o StrictHostKeyChecking=no myapp/target/${WAR_NAME} ${SSH_USER}@${TOMCAT_HOST}:/home/${SSH_USER}/
                ssh ${SSH_USER}@${TOMCAT_HOST} "sudo mv /home/${SSH_USER}/${WAR_NAME} /tomcat/apache-tomcat-8.5.58/webapps/ && sudo ./tomcat/apache-tomcat-8.5.58/bin/startup.sh"
            """
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
