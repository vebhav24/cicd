pipeline {
    agent any

    tools {
        jdk 'jdk-17'
        maven 'maven3'
    }

    environment {
        TOMCAT_HOST = '35.175.198.186'
        SSH_CRED = 'shame'     // 👈 Use your actual credential ID
        SSH_USER = 'ubuntu'
        WAR_NAME = 'myapp.war'
        TOMCAT_PATH = '/tomcat/apache-tomcat-8.5.58'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/vebhav24/cicd.git'
            }
        }

        stage('Build') {
            steps {
                dir('myapp') {
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
                echo '🚀 Deploying WAR to Tomcat server...'
                sshagent (credentials: [env.SSH_CRED]) {
                    sh """
                        # Copy WAR file to remote server
                        scp -o StrictHostKeyChecking=no myapp/target/${WAR_NAME} ${SSH_USER}@${TOMCAT_HOST}:/home/${SSH_USER}/

                        # Deploy on Tomcat
                        ssh ${SSH_USER}@${TOMCAT_HOST} '
                            echo "🔍 Checking if Tomcat is already running..."
                            TOMCAT_PID=$(pgrep -f "tomcat")

                            if [ ! -z "$TOMCAT_PID" ]; then
                                echo "🛑 Stopping running Tomcat (PID: $TOMCAT_PID)..."
                                sudo kill -9 $TOMCAT_PID || true
                            else
                                echo "✅ No running Tomcat process found."
                            fi

                            echo "🧹 Cleaning old deployments..."
                            sudo rm -rf ${TOMCAT_PATH}/webapps/*

                            echo "📦 Moving new WAR to Tomcat webapps..."
                            sudo mv /home/${SSH_USER}/${WAR_NAME} ${TOMCAT_PATH}/webapps/

                            echo "🔧 Starting Tomcat..."
                            cd ${TOMCAT_PATH}/bin
                            sudo chmod 777 *.sh
                            ./startup.sh
                            echo "✅ Tomcat restarted successfully."
                        '
                    """
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                echo '🔎 Verifying deployment...'
                sh 'sleep 10 && curl -I http://35.175.198.186/myapp || true'
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
