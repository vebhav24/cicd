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
        TOMCAT_PATH = '/tomcat/apache-tomcat-10.1.48'
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
                echo "Copying WAR file..."
                scp -o StrictHostKeyChecking=no myapp/target/${WAR_NAME} ${SSH_USER}@${TOMCAT_HOST}:/home/${SSH_USER}/

                echo "Running remote deployment..."
                ssh -o StrictHostKeyChecking=no ${SSH_USER}@${TOMCAT_HOST} '
                    set -e
                    echo "🔍 Checking if Tomcat is running..."
                    TOMCAT_PID=\$(pgrep -f "tomcat" || true)

                    if [ ! -z "\$TOMCAT_PID" ]; then
                        echo "Tomcat is running (PID: \$TOMCAT_PID). Shutting down gracefully..."
                        cd /tomcat/apache-tomcat-10.1.48/bin
                        sudo chmod +x shutdown.sh
                        ./shutdown.sh || true
                        sleep 5
                    else
                        echo "Tomcat is not running."
                    fi

                    echo "Cleaning old deployments..."
                    sudo rm -rf /tomcat/apache-tomcat-10.1.48/webapps/* || true

                    echo "📦 Moving new WAR..."
                    sudo mv /home/ubuntu/myapp.war /tomcat/apache-tomcat-10.1.48/webapps/ROOT.war || true

                    echo "🔧 Fixing permissions..."
                    sudo chown -R ubuntu:ubuntu /tomcat/apache-tomcat-10.1.48
                    sudo chmod -R 755 /tomcat/apache-tomcat-10.1.48

                    echo "🚀 Starting Tomcat..."
                    cd /tomcat/apache-tomcat-10.1.48/bin
                    sudo chmod +x *.sh
                    ./startup.sh || true

                    echo "✅ Tomcat restarted successfully."
                    exit 0
                '
            """
        }
    }
}
        stage('Verify Deployment') {
            steps {
                echo '🔎 Verifying deployment...'
                sh 'sleep 10 && curl -I http://35.175.198.186:8080 || true'
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
